package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.housekeeping.admin.dto.DemandDto;
import com.housekeeping.admin.dto.ReleaseRequirementBDTO;
import com.housekeeping.admin.dto.ReleaseRequirementUDTO;
import com.housekeeping.admin.entity.*;
import com.housekeeping.admin.mapper.DemandOrderMapper;
import com.housekeeping.admin.service.*;
import com.housekeeping.admin.vo.DemandOrderDTO;
import com.housekeeping.admin.vo.RulesWeekVo;
import com.housekeeping.admin.vo.TimeSlot;
import com.housekeeping.common.entity.PeriodOfTime;
import com.housekeeping.common.utils.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
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
    @Resource
    private DemandOrderMapper demandOrderMapper;
    @Resource
    private ICustomerAddressService customerAddressService;
    @Resource
    private ISysIndexService sysIndexService;
    @Resource
    private IDemandEmployeesService demandEmployeesService;
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

        if(CommonUtils.isEmpty(dto.getParentId())){
            return R.failed("请选择工作类型");
        }
        DemandOrder demandOrder = new DemandOrder(
                null,
                customerId,
                dto.getAddressId(),
                dto.getLiveAtHome(),
                dto.getServerPlaceType(),
                dto.getNote(),
                dto.getParentId(),
                jobIdsStr.get(),
                dto.getHousingArea(),
                dto.getEstimatedSalary(),
                dto.getCode(),
                dto.getRulesWeekVo().getStart(),
                dto.getRulesWeekVo().getEnd(),
                dto.getRulesWeekVo().getWeek(),
                0
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

    @Override
    public R getAllRequirement(Integer cusId, Page page) {
        QueryWrapper qw = new QueryWrapper<DemandOrder>();
        qw.eq("customer_id",cusId);
        List<DemandOrder> list3 = demandOrderService.list(qw);
        if(CommonUtils.isEmpty(list3)){
            return R.ok(null);
        }
        ArrayList<DemandOrderDTO> list = new ArrayList<>();
        for (int i = 0; i < list3.size(); i++) {
            DemandOrderDTO demandOrderDTO = new DemandOrderDTO();

            QueryWrapper<DemandEmployees> qw3 = new QueryWrapper<>();
            qw3.eq("demand_order_id",list3.get(i).getId());
            int count = demandEmployeesService.count(qw3);
            demandOrderDTO.setCount(count);

            //需求单工作内容
            String jobs = list3.get(i).getJobIds();
            List<Skill> skills = new ArrayList<>();
            List<String> strings = Arrays.asList(jobs.split(" "));
            for (int x = 0; x < strings.size(); x++) {
                Skill skill = new Skill();
                int id = Integer.parseInt(strings.get(x));
                skill.setJobId(id);
                skill.setContent(sysJobContendService.getById(id).getContend());
                skills.add(skill);
            }
            demandOrderDTO.setWorkContent(skills);

            //需求单工作类型
            String type = list3.get(i).getParentId();
            List<Skill> types = new ArrayList<>();
            List<String> strings1 = Arrays.asList(type.split(" "));
            for (int x = 0; x < strings1.size(); x++) {
                Skill skill = new Skill();
                int id = Integer.parseInt(strings1.get(x));
                skill.setJobId(id);
                skill.setContent(sysIndexService.getById(id).getName());
                types.add(skill);
            }
            demandOrderDTO.setWorkType(types);

            //需求单地址
            CustomerAddress byId = customerAddressService.getById(list3.get(i).getAddressId());
            demandOrderDTO.setCustomerAddress(byId);

            demandOrderDTO.setCustomerId(list3.get(i).getCustomerId());
            demandOrderDTO.setAddressId(list3.get(i).getAddressId());
            demandOrderDTO.setCode(list3.get(i).getCode());
            demandOrderDTO.setEndDate(list3.get(i).getEndDate());
            demandOrderDTO.setEstimatedSalary(list3.get(i).getEstimatedSalary());
            demandOrderDTO.setHousingArea(list3.get(i).getHousingArea());
            demandOrderDTO.setId(list3.get(i).getId());
            demandOrderDTO.setParentId(list3.get(i).getParentId());
            demandOrderDTO.setJobIds(list3.get(i).getJobIds());
            demandOrderDTO.setLiveAtHome(list3.get(i).getLiveAtHome());
            demandOrderDTO.setNote(list3.get(i).getNote());
            demandOrderDTO.setServerPlaceType(list3.get(i).getServerPlaceType());
            demandOrderDTO.setStartDate(list3.get(i).getStartDate());
            demandOrderDTO.setWeek(list3.get(i).getWeek());
            demandOrderDTO.setStatus(list3.get(i).getStatus());
            list.add(demandOrderDTO);
        }
        for (int i = 0; i < list.size(); i++) {

            Integer demandId = list.get(i).getId();
            List<TimeSlot> timeSlots = demandOrderMapper.getTimes(demandId);

            list.get(i).setTimeSlots(timeSlots);
        }
        Page pages = PageUtils.getPages((int) page.getCurrent(), (int) page.getSize(), list);
        return R.ok(pages);
    }

    @Override
    public R getAllRequirementsByCompany(DemandDto demandDto,Page page) {
        QueryWrapper<DemandOrder> qw = new QueryWrapper<>();
        if(CommonUtils.isNotEmpty(demandDto.getJobIds())){
            String jobs = demandDto.getJobIds().replaceAll(",", " ");
            qw.like("job_ids",jobs);
        }
        if(CommonUtils.isNotEmpty(demandDto.getWorkTypeIds())){
            String jobs = demandDto.getWorkTypeIds().replaceAll(",", " ");
            qw.like("job_ids",jobs);
        }
        if(CommonUtils.isNotEmpty(demandDto.getPlace())){
            qw.like("job_ids",demandDto.getPlace());
        }
        if(CommonUtils.isNotEmpty(demandDto.getStartDate())){
            qw.ge("start_date",demandDto.getStartDate());
        }
        if(CommonUtils.isNotEmpty(demandDto.getLowPrice())&&CommonUtils.isNotEmpty(demandDto.getHighPrice())){
            qw.between("estimated_salary",demandDto.getLowPrice(),demandDto.getHighPrice());
        }
        List<DemandOrder> list3 = demandOrderService.list(qw);
        ArrayList<DemandOrderDTO> list = new ArrayList<>();
        for (int i = 0; i < list3.size(); i++) {
            DemandOrderDTO demandOrderDTO = new DemandOrderDTO();

            //需求单工作内容
            String jobs = list3.get(i).getJobIds();
            List<Skill> skills = new ArrayList<>();
            List<String> strings = Arrays.asList(jobs.split(" "));
            for (int x = 0; x < strings.size(); x++) {
                Skill skill = new Skill();
                int id = Integer.parseInt(strings.get(x));
                skill.setJobId(id);
                skill.setContent(sysJobContendService.getById(id).getContend());
                skills.add(skill);
            }
            demandOrderDTO.setWorkContent(skills);

            //需求单工作类型
            String type = list3.get(i).getParentId();
            List<Skill> types = new ArrayList<>();
            List<String> strings1 = Arrays.asList(type.split(" "));
            for (int x = 0; x < strings1.size(); x++) {
                Skill skill = new Skill();
                int id = Integer.parseInt(strings1.get(x));
                skill.setJobId(id);
                skill.setContent(sysIndexService.getById(id).getName());
                types.add(skill);
            }
            demandOrderDTO.setWorkType(types);

            //需求单地址
            CustomerAddress byId = customerAddressService.getById(list3.get(i).getAddressId());
            demandOrderDTO.setCustomerAddress(byId);

            demandOrderDTO.setCustomerId(list3.get(i).getCustomerId());
            demandOrderDTO.setAddressId(list3.get(i).getAddressId());
            demandOrderDTO.setCode(list3.get(i).getCode());
            demandOrderDTO.setEndDate(list3.get(i).getEndDate());
            demandOrderDTO.setEstimatedSalary(list3.get(i).getEstimatedSalary());
            demandOrderDTO.setHousingArea(list3.get(i).getHousingArea());
            demandOrderDTO.setId(list3.get(i).getId());
            demandOrderDTO.setJobIds(list3.get(i).getJobIds());
            demandOrderDTO.setLiveAtHome(list3.get(i).getLiveAtHome());
            demandOrderDTO.setNote(list3.get(i).getNote());
            demandOrderDTO.setServerPlaceType(list3.get(i).getServerPlaceType());
            demandOrderDTO.setStartDate(list3.get(i).getStartDate());
            demandOrderDTO.setWeek(list3.get(i).getWeek());
            demandOrderDTO.setParentId(list3.get(i).getParentId());
            demandOrderDTO.setStatus(list3.get(i).getStatus());
            list.add(demandOrderDTO);
        }
        for (int i = 0; i < list.size(); i++) {
            Integer demandId = list.get(i).getId();
            List<TimeSlot> timeSlots = demandOrderMapper.getTimes(demandId);
            list.get(i).setTimeSlots(timeSlots);
        }
        Page pages = PageUtils.getPages((int) page.getCurrent(), (int) page.getSize(), list);
        return R.ok(pages);
    }

    @Override
    public R removedCusId(Integer id) {
        demandOrderService.removeById(id);
        QueryWrapper<DemandOrderDetails> qw = new QueryWrapper<>();
        qw.eq("demand_order_id",id);
        demandOrderDetailsService.remove(qw);
        return R.ok("刪除成功!");
    }

    @Override
    public R updateCus(ReleaseRequirementUDTO dto) throws InterruptedException {
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

        if(CommonUtils.isEmpty(dto.getParentId())){
            return R.failed("请选择工作类型");
        }
        DemandOrder demandOrder = new DemandOrder(
                dto.getId(),
                customerId,
                dto.getAddressId(),
                dto.getLiveAtHome(),
                dto.getServerPlaceType(),
                dto.getNote(),
                dto.getParentId(),
                jobIdsStr.get(),
                dto.getHousingArea(),
                dto.getEstimatedSalary(),
                dto.getCode(),
                dto.getRulesWeekVo().getStart(),
                dto.getRulesWeekVo().getEnd(),
                dto.getRulesWeekVo().getWeek(),
                dto.getStatus()
        );
        synchronized (this){
            demandOrderService.updateById(demandOrder);
        }
        List<DemandOrderDetails> demandOrderDetails = new ArrayList<>();
        Integer finalDemandOrderId = dto.getId();

        QueryWrapper<DemandOrderDetails> qw3 = new QueryWrapper<>();
        qw3.eq("demand_order_id",finalDemandOrderId);
        demandOrderDetailsService.remove(qw3);

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
        return R.ok("修改成功");
    }

    @Override
    public R getCusById(Integer id) {
        DemandOrder byId = demandOrderService.getById(id);
        DemandOrderDTO demandOrderDTO = new DemandOrderDTO();

        QueryWrapper<DemandEmployees> qw3 = new QueryWrapper<>();
        qw3.eq("demand_order_id",byId.getId());
        int count = demandEmployeesService.count(qw3);
        demandOrderDTO.setCount(count);

        //需求单工作内容
        String jobs = byId.getJobIds();
        List<Skill> skills = new ArrayList<>();
        List<String> strings = Arrays.asList(jobs.split(" "));
        for (int x = 0; x < strings.size(); x++) {
            Skill skill = new Skill();
            int contentId = Integer.parseInt(strings.get(x));
            skill.setJobId(contentId);
            skill.setContent(sysJobContendService.getById(contentId).getContend());
            skills.add(skill);
        }
        demandOrderDTO.setWorkContent(skills);

        //需求单工作类型
        String type = byId.getParentId();
        List<Skill> types = new ArrayList<>();
        List<String> strings1 = Arrays.asList(type.split(" "));
        for (int x = 0; x < strings1.size(); x++) {
            Skill skill = new Skill();
            int typeId = Integer.parseInt(strings1.get(x));
            skill.setJobId(typeId);
            skill.setContent(sysIndexService.getById(typeId).getName());
            types.add(skill);
        }
        demandOrderDTO.setWorkType(types);

        //需求单地址
        CustomerAddress customerAddress = customerAddressService.getById(byId.getAddressId());
        demandOrderDTO.setCustomerAddress(customerAddress);

        demandOrderDTO.setCustomerId(byId.getCustomerId());
        demandOrderDTO.setAddressId(byId.getAddressId());
        demandOrderDTO.setCode(byId.getCode());
        demandOrderDTO.setEndDate(byId.getEndDate());
        demandOrderDTO.setEstimatedSalary(byId.getEstimatedSalary());
        demandOrderDTO.setHousingArea(byId.getHousingArea());
        demandOrderDTO.setId(byId.getId());
        demandOrderDTO.setJobIds(byId.getJobIds());
        demandOrderDTO.setLiveAtHome(byId.getLiveAtHome());
        demandOrderDTO.setParentId(byId.getParentId());
        demandOrderDTO.setNote(byId.getNote());
        demandOrderDTO.setServerPlaceType(byId.getServerPlaceType());
        demandOrderDTO.setStartDate(byId.getStartDate());
        demandOrderDTO.setWeek(byId.getWeek());
        demandOrderDTO.setParentId(byId.getParentId());
        demandOrderDTO.setStatus(byId.getStatus());

        List<TimeSlot> timeSlots = demandOrderMapper.getTimes(id);
        demandOrderDTO.setTimeSlots(timeSlots);

        return R.ok(demandOrderDTO);
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
