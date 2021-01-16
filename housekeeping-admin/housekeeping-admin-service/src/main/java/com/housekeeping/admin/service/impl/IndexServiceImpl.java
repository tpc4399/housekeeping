package com.housekeeping.admin.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.IndexQueryDTO;
import com.housekeeping.admin.entity.*;
import com.housekeeping.admin.mapper.IndexMapper;
import com.housekeeping.admin.service.*;
import com.housekeeping.admin.vo.TimeSlot;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.OptionalBean;
import com.housekeeping.common.utils.R;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service("indexService")
public class IndexServiceImpl extends ServiceImpl<IndexMapper, Index> implements IIndexService {

    @Resource
    private ISysJobContendService jobContendService;
    @Resource
    private IEmployeesCalendarService employeesCalendarService;
    @Resource
    private IEmployeesJobsService employeesJobsService;
    @Resource
    private ICompanyPromotionService companyPromotionService;
    @Resource
    private IEmployeesPromotionService employeesPromotionService;
    @Resource
    private EmployeesDetailsService employeesDetailsService;
    @Resource
    private ICustomerAddressService customerAddressService;

    @Override
    public R getCusById(Integer id) {
        List<Integer> ids = baseMapper.getContentIds(id);
        ArrayList<SysJobContend> sysJobContends = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            SysJobContend byId = jobContendService.getById(ids.get(i));
            sysJobContends.add(byId);
        }
        return R.ok(sysJobContends);
    }

    @Override
    public R query(IndexQueryDTO indexQueryDTO) {

        /***
         * 判空
         */
        Integer indexId = OptionalBean.ofNullable(indexQueryDTO)
                .getBean(IndexQueryDTO::getIndexId).get();
        LocalDate date = OptionalBean.ofNullable(indexQueryDTO)
                .getBean(IndexQueryDTO::getDate).get();
        List<TimeSlot> timeSlotList = OptionalBean.ofNullable(indexQueryDTO)
                .getBean(IndexQueryDTO::getTimeSlotList).get();
        String code = OptionalBean.ofNullable(indexQueryDTO)
                .getBean(IndexQueryDTO::getCode).get();
        String lowestPrice = OptionalBean.ofNullable(indexQueryDTO)
                .getBean(IndexQueryDTO::getLowestPrice).get();
        String highestPrice = OptionalBean.ofNullable(indexQueryDTO)
                .getBean(IndexQueryDTO::getHighestPrice).get();
        Integer addressId = OptionalBean.ofNullable(indexQueryDTO)
                .getBean(IndexQueryDTO::getAddressId).get();

        List<String> resFailed = new ArrayList<>();
        if (CommonUtils.isEmpty(indexId)) resFailed.add("元素_id為空");
        if (CommonUtils.isEmpty(date)) resFailed.add("服務日期為空");
        if (CommonUtils.isEmpty(timeSlotList)) resFailed.add("上門時間段為空");
        if (CommonUtils.isEmpty(code)) resFailed.add("貨幣代碼為空");
        if (CommonUtils.isEmpty(lowestPrice)) resFailed.add("最低價為空");
        if (CommonUtils.isEmpty(highestPrice)) resFailed.add("最高價為空");
        if (CommonUtils.isEmpty(addressId)) resFailed.add("服務地址為空");
        if (!resFailed.isEmpty()){
            return R.failed(resFailed, "存在空值");
        }

        /** 返回结果 */
        Map<String, Object> map = new HashMap<>();
        /** searchPool 员工搜索池：只有符合条件的保洁员才会放进池子（设定了工作内容，设定了工作时间表） */
        List<Integer> searchPool = new ArrayList<>();
        QueryWrapper qw1 = new QueryWrapper();
        qw1.select("employees_id").groupBy("employees_id");
        List<Integer> employeeIdsFromCalendar = employeesCalendarService.listObjs(qw1);
        List<Integer> employeeIdsFromJob = employeesJobsService.listObjs(qw1);
        searchPool = getIntersection(employeeIdsFromCalendar, employeeIdsFromJob);
        /** promoteCompanyIds 推广公司搜索池 */
        List<Integer> promoteCompanyIds = new ArrayList<>();
        QueryWrapper qw2 = new QueryWrapper();
        qw2.select("company_id").gt("end_time", LocalDateTime.now());
        promoteCompanyIds = companyPromotionService.listObjs(qw2);
        /** promoteEmployeeIds 推广员工搜索池 */
        List<Integer> promoteEmployeeIds = new ArrayList<>();
        QueryWrapper qw3 = new QueryWrapper();
        qw3.select("employees_id").gt("end_time", LocalDateTime.now());
        promoteEmployeeIds = employeesPromotionService.listObjs(qw3);
        /** customerAddress 客戶地址准备 */
        CustomerAddress customerAddress = customerAddressService.getById(addressId);
        /** instanceMap 保洁员距离准备 */
        Map<Integer, Double> instanceMap = new HashMap<>();
        /** priceMap 保洁员价格准备 */
        Map<Integer, BigDecimal> priceMap = new HashMap<>();

        /**
         * 【推荐公司】  1、公司推广列表里面的公司 2、公司手底下有保洁员被匹配（时间段，工作内容）
         * 【推荐保洁员】1、员工推广列表里面的员工 2、员工可以被匹配（时间段，工作内容），按价格接近度排序
         * 【附近保洁员】1、匹配到的员工，按距离排序
         * 【最佳保洁员】1、匹配到的员工，按评分排序
         */

        searchPool.forEach(employeesId -> {
            /** calendarMap: 时间表map准备 */
            QueryWrapper qw4 = new QueryWrapper();
            qw4.eq("employees_id", employeesId);
            List<EmployeesCalendar> employeesCalendarList = employeesCalendarService.list(qw4);
            Map<Object, List<EmployeesCalendar>> calendarMap = new HashMap<>();
            List<EmployeesCalendar> a = new ArrayList<>();
            List<EmployeesCalendar> b = new ArrayList<>();
            List<EmployeesCalendar> c = new ArrayList<>();
            employeesCalendarList.forEach(x -> {
                if (x.getStander() == null) c.add(x);
                else if (x.getStander()) a.add(x);
                else b.add(x);
            });
            calendarMap.put(true,a);
            calendarMap.put(false, b);
            calendarMap.put("", c);

            /** jobContendIds: 工作内容准备 */
            QueryWrapper qw5 = new QueryWrapper();
            qw5.select("job_id").eq("employees_id", employeesId);
            List<Integer> jobContendIds = employeesJobsService.listObjs(qw5);

            /** instance: 距离准备 */
            EmployeesDetails employeesDetails = employeesDetailsService.getById(employeesId);
            String instance = CommonUtils.getInstanceByPoint(employeesDetails.getLng(),
                    employeesDetails.getLat(),
                    customerAddress.getLng(),
                    customerAddress.getLat()
            );
            instanceMap.put(employeesId, Double.valueOf(instance));

            /** price: 价格准备 */
            



        });
        return null;
    }

    public List<Integer> getIntersection(List<Integer> a, List<Integer> b){
        a.retainAll(b);
        return a;
    }
}
