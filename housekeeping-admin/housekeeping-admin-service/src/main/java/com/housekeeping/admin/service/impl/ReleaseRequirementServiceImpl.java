package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.housekeeping.admin.dto.ReleaseRequirementBDTO;
import com.housekeeping.admin.entity.*;
import com.housekeeping.admin.service.*;
import com.housekeeping.admin.vo.RulesWeekVo;
import com.housekeeping.admin.vo.TimeSlot;
import com.housekeeping.common.entity.PeriodOfTime;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.SortListUtil;
import com.housekeeping.common.utils.TokenUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Author su
 * @Date 2021/1/12 9:06
 */
@Service("releaseRequirementService")
public class ReleaseRequirementServiceImpl implements IReleaseRequirementService {

    @Resource
    private ISysJobContendService sysJobContendService;
    @Resource
    private IDemandOrderService demandOrderService;
    @Resource
    private IDemandOrderDetailsService demandOrderDetailsService;
    @Resource
    private ICustomerDetailsService customerDetailsService;

    @Override
    public R releaseRequirements(ReleaseRequirementBDTO dto) throws InterruptedException {

        Integer userId = TokenUtils.getCurrentUserId();
        QueryWrapper qw = new QueryWrapper();
        qw.eq("user_id", userId);
        CustomerDetails existCustomer = customerDetailsService.getOne(qw);
        Integer customerId = existCustomer.getId();

        //TODO 服务时间合理性判断
        List<String> resCollections = rationalityJudgmentWeek(dto);//不合理性结果收集
        if (resCollections.size() != 0){
            return R.failed(resCollections, "服務時間不合理");
        }

        //TODO 选中的工作内容标签
        List<Integer> jobIds = dto.getJobs();

        //TODO 服务时间二象化展开
        Map<LocalDate, List<PeriodOfTime>> listMap =  timeExpansion(dto.getRulesWeekVo());

        //TODO 生成订单+订单详情表记录存储
        AtomicReference<String> jobIdsStr = new AtomicReference<>("");
        jobIds.forEach(x -> {
            jobIdsStr.set(jobIdsStr.get() + x.toString() + " ");
        });
        DemandOrder demandOrder = new DemandOrder(
                null,
                customerId,
                dto.getLiveAtHome(),
                dto.getServerPlaceType(),
                dto.getNote(),
                jobIdsStr.get(),
                dto.getHousingArea(),
                dto.getEstimatedSalary(),
                dto.getCode()
        );
        Integer demandOrderId = 0;
        synchronized (this){
            demandOrderService.save(demandOrder);
            demandOrderId = ((DemandOrder) CommonUtils.getMaxId("demand_order", demandOrderService)).getId();
        }
        List<DemandOrderDetails> demandOrderDetails = new ArrayList<>();
        Integer finalDemandOrderId = demandOrderId;
        listMap.forEach((x, y) -> {
            y.forEach(z -> {
                DemandOrderDetails details = new DemandOrderDetails(
                        null,
                        finalDemandOrderId,
                        x,
                        z.getTimeSlotStart(),
                        z.getTimeSlotLength()
                );
                demandOrderDetails.add(details);
            });
        });
        demandOrderDetailsService.saveBatch(demandOrderDetails);
        return R.ok("發佈成功");
    }

    @Override
    public R page(IPage page) {
        return R.ok(demandOrderService.page(page), "獲取成功");
    }

    List<String> rationalityJudgmentWeek(ReleaseRequirementBDTO dto){
        List<String> resCollections = new ArrayList<>();//不合理性结果收集
        SortListUtil<TimeSlot> sort = new SortListUtil<>();
        List<TimeSlot> timeSlotVos = dto.getRulesWeekVo().getTimeSlotVos();
        sort.Sort(timeSlotVos, "getTimeSlotStart", null);
        for (int i = 0; i < timeSlotVos.size()-1; i++) {
            PeriodOfTime period1 = new PeriodOfTime(timeSlotVos.get(i).getTimeSlotStart(), timeSlotVos.get(i).getTimeSlotLength());
            PeriodOfTime period2 = new PeriodOfTime(timeSlotVos.get(i+1).getTimeSlotStart(), timeSlotVos.get(i+1).getTimeSlotLength());
            if (CommonUtils.doRechecking(period1, period2)){
                //重複的處理方式
                StringBuilder res = new StringBuilder();
                res.append("周模板存在時間段重複： week ").append(i).append("  ");
                res.append(period1.getTimeSlotStart().toString()).append("+").append(period1.getTimeSlotLength()).append("h");
                res.append("與");
                res.append(period2.getTimeSlotStart().toString()).append("+").append(period2.getTimeSlotLength()).append("h");
                resCollections.add(res.toString());
            }
        }
        return resCollections;
    }

    Map<LocalDate, List<PeriodOfTime>> timeExpansion(RulesWeekVo vo) throws InterruptedException {
        Map<LocalDate, List<PeriodOfTime>> map = new HashMap<>();
        LocalDate start = vo.getStart();
        LocalDate end = vo.getEnd();
        String week = vo.getWeek();
        List<TimeSlot> timeSlotVos = vo.getTimeSlotVos();
        ExecutorService ex = Executors.newCachedThreadPool();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            LocalDate finalDate = date;
            ex.submit(() -> {
                Integer existWeek = finalDate.getDayOfWeek().getValue();
                String weekStr = existWeek.toString();
                if (week.contains(weekStr)){
                    List<PeriodOfTime> periods = new ArrayList<>();
                    timeSlotVos.forEach(timeSlot -> {
                        PeriodOfTime period = new PeriodOfTime(timeSlot.getTimeSlotStart(), timeSlot.getTimeSlotLength());
                        periods.add(period);
                    });
                    map.put(finalDate, periods);
                }
            });
        }
        ex.shutdown();
        ex.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        return map;
    }

}
