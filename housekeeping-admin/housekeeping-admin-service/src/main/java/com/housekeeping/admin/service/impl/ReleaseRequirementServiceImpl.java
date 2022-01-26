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
import java.time.LocalDateTime;
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
    @Resource
    private ICompanyWorkListService workListService;

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
        if(CommonUtils.isEmpty(jobIds)){
            return R.failed("請選擇工作內容!");
        }

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
        CustomerAddress byId = customerAddressService.getById(dto.getAddressId());
        DemandOrder demandOrder = new DemandOrder(
                null,
                customerId,
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
                0,
                byId.getName(),
                byId.getAddress(),
                byId.getLat(),
                byId.getLng(),
                byId.getPhonePrefix(),
                byId.getPhone(),
                LocalDateTime.now(),
                LocalDateTime.now()
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
    public R getAllRequirement(Integer cusId, Page page,Integer status) {
        QueryWrapper qw = new QueryWrapper<DemandOrder>();
        qw.eq("customer_id",cusId);
        qw.orderByDesc("id");
        List<DemandOrder> list3 = demandOrderService.list(qw);
        if(CommonUtils.isEmpty(list3)){
            return R.ok(null);
        }

        if(status!=null) {
            if (status.equals(0)) {
                Iterator<DemandOrder> iterator = list3.iterator();
                while (iterator.hasNext()) {
                    DemandOrder next = iterator.next();
                    if (this.getStatus(next).equals(1)) {
                        iterator.remove();//使用迭代器的删除方法删除
                    }
                }
            }
            if (status.equals(1)) {
                Iterator<DemandOrder> iterator = list3.iterator();
                while (iterator.hasNext()) {
                    DemandOrder next = iterator.next();
                    if (this.getStatus(next).equals(0)) {
                        iterator.remove();//使用迭代器的删除方法删除
                    }
                }
            }
        }
        ArrayList<DemandOrderDTO> list = new ArrayList<>();
        for (int i = 0; i < list3.size(); i++) {
            DemandOrderDTO demandOrderDTO = new DemandOrderDTO();

            //报价单数量
            QueryWrapper<DemandEmployees> qw3 = new QueryWrapper<>();
            qw3.eq("demand_order_id",list3.get(i).getId());
            int count = demandEmployeesService.count(qw3);
            demandOrderDTO.setCount(count);

            //客户信息
            demandOrderDTO.setCustomerDetails(customerDetailsService.getById(list3.get(i).getCustomerId()));

            //需求单工作内容
            String jobs = list3.get(i).getJobIds();
            List<Skill> skills = new ArrayList<>();
            List<String> strings = Arrays.asList(jobs.split(" "));
            if(CommonUtils.isNotEmpty(strings)){
                for (int x = 0; x < strings.size(); x++) {
                    Skill skill = new Skill();
                    int id = Integer.parseInt(strings.get(x));
                    skill.setJobId(id);
                    skill.setContend(sysJobContendService.getById(id).getContend());
                    skills.add(skill);
                }
                demandOrderDTO.setWorkContent(skills);
            }else {
                demandOrderDTO.setWorkContent(null);
            }

            //需求单工作类型
            String type = list3.get(i).getParentId();
            List<Skill> types = new ArrayList<>();
            List<String> strings1 = Arrays.asList(type.split(" "));
            for (int x = 0; x < strings1.size(); x++) {
                Skill skill = new Skill();
                int id = Integer.parseInt(strings1.get(x));
                skill.setJobId(id);
                skill.setContend(sysIndexService.getById(id).getName());
                types.add(skill);
            }
            demandOrderDTO.setWorkType(types);

            //需求单地址
            demandOrderDTO.setCustomerName(list3.get(i).getCustomerName());
            demandOrderDTO.setAddress(list3.get(i).getAddress());
            demandOrderDTO.setLat(list3.get(i).getLat());
            demandOrderDTO.setLng(list3.get(i).getLng());
            demandOrderDTO.setPhonePrefix(list3.get(i).getPhonePrefix());
            demandOrderDTO.setPhone(list3.get(i).getPhone());

            demandOrderDTO.setCustomerId(list3.get(i).getCustomerId());
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
            demandOrderDTO.setCreateTime(list3.get(i).getCreateTime());
            demandOrderDTO.setUpdateTime(list3.get(i).getUpdateTime());
            demandOrderDTO.setStatus(this.getStatus(list3.get(i)));
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

        List<DemandOrder> list3 = demandOrderMapper.cusPage(demandDto);
        if(CommonUtils.isEmpty(list3)){
            return R.ok(null);
        }

        Iterator<DemandOrder> iterator = list3.iterator();
        while (iterator.hasNext()) {
            DemandOrder next = iterator.next();
            Integer status = this.getStatus(next);
            if (status.equals(1)) {
                iterator.remove();//使用迭代器的删除方法删除
            }
        }

        ArrayList<DemandOrderDTO> list = new ArrayList<>();
        for (int i = 0; i < list3.size(); i++) {
            DemandOrderDTO demandOrderDTO = new DemandOrderDTO();

            //客户信息
            demandOrderDTO.setCustomerDetails(customerDetailsService.getById(list3.get(i).getCustomerId()));

            //需求单工作内容
            String jobs = list3.get(i).getJobIds();
            List<Skill> skills = new ArrayList<>();
            List<String> strings = Arrays.asList(jobs.split(" "));
            for (int x = 0; x < strings.size(); x++) {
                Skill skill = new Skill();
                int id = Integer.parseInt(strings.get(x));
                skill.setJobId(id);
                skill.setContend(sysJobContendService.getById(id).getContend());
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
                skill.setContend(sysIndexService.getById(id).getName());
                types.add(skill);
            }
            demandOrderDTO.setWorkType(types);

            //需求单地址
            demandOrderDTO.setCustomerName(list3.get(i).getCustomerName());
            demandOrderDTO.setAddress(list3.get(i).getAddress());
            demandOrderDTO.setLat(list3.get(i).getLat());
            demandOrderDTO.setLng(list3.get(i).getLng());
            demandOrderDTO.setPhonePrefix(list3.get(i).getPhonePrefix());
            demandOrderDTO.setPhone(list3.get(i).getPhone());

            demandOrderDTO.setCustomerId(list3.get(i).getCustomerId());
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
            demandOrderDTO.setCreateTime(list3.get(i).getCreateTime());
            demandOrderDTO.setUpdateTime(list3.get(i).getUpdateTime());
            demandOrderDTO.setStatus(this.getStatus(list3.get(i)));
            list.add(demandOrderDTO);
        }

        //需求时间
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

        QueryWrapper<DemandOrderDetails> qw = new QueryWrapper<>();
        qw.eq("demand_order_id",id);
        demandOrderDetailsService.remove(qw);

        QueryWrapper<DemandEmployees> qw2 = new QueryWrapper<>();
        qw2.eq("demand_order_id",id);
        demandEmployeesService.remove(qw2);

        demandOrderService.removeById(id);
        return R.ok("刪除成功!");
    }

    @Override
    public R updateCus(ReleaseRequirementUDTO dto) throws InterruptedException {

        DemandOrder byId1 = demandOrderService.getById(dto.getId());
        Integer status = this.getStatus(byId1);
        if(status==1){
            return R.failed("该需求单已接，不能修改");
        }

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
        DemandOrder demandOrder1 = demandOrderService.getById(dto.getId());
        DemandOrder demandOrder = null;
        if(CommonUtils.isNotEmpty(dto.getAddressId())){
            CustomerAddress byId = customerAddressService.getById(dto.getAddressId());
            demandOrder = new DemandOrder(
                    dto.getId(),
                    customerId,
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
                    demandOrder1.getStatus(),
                    byId.getName(),
                    byId.getAddress(),
                    byId.getLat(),
                    byId.getLng(),
                    byId.getPhonePrefix(),
                    byId.getPhone(),
                    demandOrder1.getCreateTime(),
                    LocalDateTime.now()
            );
        }else {
            demandOrder = new DemandOrder(
                    dto.getId(),
                    customerId,
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
                    demandOrder1.getStatus(),
                    demandOrder1.getCustomerName(),
                    demandOrder1.getAddress(),
                    demandOrder1.getLat(),
                    demandOrder1.getLng(),
                    demandOrder1.getPhonePrefix(),
                    demandOrder1.getPhone(),
                    demandOrder1.getCreateTime(),
                    LocalDateTime.now()
            );
        }
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

        //报价单数量
        QueryWrapper<DemandEmployees> qw3 = new QueryWrapper<>();
        qw3.eq("demand_order_id",byId.getId());
        int count = demandEmployeesService.count(qw3);
        demandOrderDTO.setCount(count);

        //客户信息
        demandOrderDTO.setCustomerDetails(customerDetailsService.getById(byId.getCustomerId()));

        //需求单工作内容
        String jobs = byId.getJobIds();
        List<Skill> skills = new ArrayList<>();
        List<String> strings = Arrays.asList(jobs.split(" "));
        for (int x = 0; x < strings.size(); x++) {
            Skill skill = new Skill();
            int contentId = Integer.parseInt(strings.get(x));
            skill.setJobId(contentId);
            skill.setContend(sysJobContendService.getById(contentId).getContend());
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
            skill.setContend(sysIndexService.getById(typeId).getName());
            types.add(skill);
        }
        demandOrderDTO.setWorkType(types);

        //需求单地址
        demandOrderDTO.setCustomerName(byId.getCustomerName());
        demandOrderDTO.setAddress(byId.getAddress());
        demandOrderDTO.setLat(byId.getLat());
        demandOrderDTO.setLng(byId.getLng());
        demandOrderDTO.setPhonePrefix(byId.getPhonePrefix());
        demandOrderDTO.setPhone(byId.getPhone());

        demandOrderDTO.setCustomerId(byId.getCustomerId());
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
        demandOrderDTO.setCreateTime(byId.getCreateTime());
        demandOrderDTO.setUpdateTime(byId.getUpdateTime());
        demandOrderDTO.setStatus(this.getStatus(byId));

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

    public Integer getStatus(DemandOrder demandOrder) {
        Integer demandStatus = 0;
        QueryWrapper<DemandEmployees> qw = new QueryWrapper<>();
        qw.eq("demand_order_id", demandOrder.getId());
        List<DemandEmployees> list = demandEmployeesService.list(qw);
        if (CommonUtils.isEmpty(list)) {
            demandStatus = 0;
        }

        for (int i = 0; i < list.size(); i++) {
            Integer status = workListService.getStatus(list.get(i));
            if (status.equals(1)) {
                demandStatus = 1;
            }
        }
        return demandStatus;
    }

    @Override
    public R getAllRequirementsByAdmin(DemandDto demandDto, Page page) {
        List<DemandOrder> list3 = demandOrderMapper.cusPage(demandDto);
        if(CommonUtils.isEmpty(list3)){
            return R.ok(null);
        }

        ArrayList<DemandOrderDTO> list = new ArrayList<>();
        for (int i = 0; i < list3.size(); i++) {
            DemandOrderDTO demandOrderDTO = new DemandOrderDTO();

            //客户信息
            demandOrderDTO.setCustomerDetails(customerDetailsService.getById(list3.get(i).getCustomerId()));

            //需求单工作内容
            String jobs = list3.get(i).getJobIds();
            List<Skill> skills = new ArrayList<>();
            List<String> strings = Arrays.asList(jobs.split(" "));
            for (int x = 0; x < strings.size(); x++) {
                Skill skill = new Skill();
                int id = Integer.parseInt(strings.get(x));
                skill.setJobId(id);
                skill.setContend(sysJobContendService.getById(id).getContend());
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
                skill.setContend(sysIndexService.getById(id).getName());
                types.add(skill);
            }
            demandOrderDTO.setWorkType(types);

            //需求单地址
            demandOrderDTO.setCustomerName(list3.get(i).getCustomerName());
            demandOrderDTO.setAddress(list3.get(i).getAddress());
            demandOrderDTO.setLat(list3.get(i).getLat());
            demandOrderDTO.setLng(list3.get(i).getLng());
            demandOrderDTO.setPhonePrefix(list3.get(i).getPhonePrefix());
            demandOrderDTO.setPhone(list3.get(i).getPhone());

            demandOrderDTO.setCustomerId(list3.get(i).getCustomerId());
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
            demandOrderDTO.setCreateTime(list3.get(i).getCreateTime());
            demandOrderDTO.setUpdateTime(list3.get(i).getUpdateTime());
            demandOrderDTO.setStatus(this.getStatus(list3.get(i)));
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
}
