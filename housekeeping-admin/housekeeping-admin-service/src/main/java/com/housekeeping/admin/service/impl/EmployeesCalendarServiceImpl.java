package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.*;
import com.housekeeping.admin.entity.*;
import com.housekeeping.admin.mapper.EmployeesCalendarMapper;
import com.housekeeping.admin.service.*;
import com.housekeeping.admin.vo.RecommendedEmployeesVo;
import com.housekeeping.admin.vo.TimeSlot;
import com.housekeeping.admin.vo.TimeSlotVo;
import com.housekeeping.common.entity.ConversionRatio;
import com.housekeeping.common.entity.PeriodOfTime;
import com.housekeeping.common.utils.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @Author su
 * @create 2020/11/12 16:22
 */
@Service("employeesCalendarService")
public class EmployeesCalendarServiceImpl extends ServiceImpl<EmployeesCalendarMapper, EmployeesCalendar> implements IEmployeesCalendarService {

    @Resource
    private IEmployeesCalendarService employeesCalendarService;
    @Resource
    private IEmployeesCalendarDetailsService employeesCalendarDetailsService;
    @Resource
    private IEmployeesContractService employeesContractService;
    @Resource
    private IEmployeesContractDetailsService employeesContractDetailsService;
    @Resource
    private ICompanyDetailsService companyDetailsService;
    @Resource
    private EmployeesDetailsService employeesDetailsService;
    @Resource
    private ManagerDetailsService managerDetailsService;
    @Resource
    private ICustomerDetailsService customerDetailsService;
    @Resource
    private ICurrencyService currencyService;

    @Override
    public R setCalendar(SetEmployeesCalendarDTO dto) {
        /* dto合理性判斷 */
        List<String> res = this.rationalityJudgmentA(dto);
        if (res.size() == 0){
            //这是合理的
        }else {
            return R.failed(res, "數據不合理");
        }

        /* 鉴权处理 */
        String authenticationResult = this.authenticationProcessing(dto.getEmployeesId());
        if (!authenticationResult.equals(CommonConstants.AUTHENTICATION_SUCCESSFUL)){
            return R.failed(null, authenticationResult);
        }

        /* 员工存在性判断 */
        Boolean isOk = employeesDetailsService.judgmentOfExistence(dto.getEmployeesId());
        if (isOk){

        }else {
            return R.failed(null, "該員工不存在");
        }

        /* 删掉原有的 */
        QueryWrapper deleteQw = new QueryWrapper();
        deleteQw.eq("employees_id", dto.getEmployeesId());
        deleteQw.eq("stander", "");
        List<EmployeesCalendar> willDeleteList = this.list(deleteQw);
        willDeleteList.forEach(x->{
            QueryWrapper deleteDependency1 = new QueryWrapper();
            deleteDependency1.eq("calendar_id", x.getId());
            employeesCalendarDetailsService.remove(deleteDependency1);//删除依赖
        });
        this.remove(deleteQw);
        /* 添加新的 */
        dto.getTimeSlotList().forEach(timeSlot -> {
            EmployeesCalendar employeesCalendar =
                    new EmployeesCalendar(
                            dto.getEmployeesId(),
                            null,
                            null,
                            null,
                            timeSlot.getTimeSlotStart(),
                            timeSlot.getTimeSlotLength()
                    );
            Integer maxCalendarId = 0;
            synchronized (this){
                baseMapper.insert(employeesCalendar);
                maxCalendarId = ((EmployeesCalendar) CommonUtils.getMaxId("employees_calendar", this)).getId();
            }
            List<EmployeesCalendarDetails> employeesCalendarDetailsList = new ArrayList<>();
            Integer finalMaxCalendarId = maxCalendarId;
            timeSlot.getJobAndPriceList().forEach(jobAndPrice -> {
                EmployeesCalendarDetails employeesCalendarDetails =
                        new EmployeesCalendarDetails(
                                finalMaxCalendarId,
                                jobAndPrice.getJobId(),
                                jobAndPrice.getPrice(),
                                jobAndPrice.getCode()
                        );
                employeesCalendarDetailsList.add(employeesCalendarDetails);
            });
            employeesCalendarDetailsService.saveBatch(employeesCalendarDetailsList);
        });
        return R.ok("設置成功");
    }

    @Override
    public R addCalendarWeek(SetEmployeesCalendarWeekDTO dto) {
        /* dto合理性判斷 */
        List<String> res = this.rationalityJudgmentB(dto);
        if (res.size() == 0){
            //这是合理的
        }else {
            return R.failed(res, "數據不合理");
        }

        /* 鉴权处理 */
        String authenticationResult = this.authenticationProcessing(dto.getEmployeesId());
        if (!authenticationResult.equals(CommonConstants.AUTHENTICATION_SUCCESSFUL)){
            return R.failed(null, authenticationResult);
        }

        /* 员工存在性判断 */
        Boolean isOk = employeesDetailsService.judgmentOfExistence(dto.getEmployeesId());
        if (isOk){

        }else {
            return R.failed(null, "該員工不存在");
        }

        /* 添加新的 */
        StringBuilder week = new StringBuilder();
        dto.getWeek().forEach(wk->{
            week.append(wk);
        });
        dto.getTimeSlotList().forEach(timeSlot -> {
            EmployeesCalendar employeesCalendar =
                    new EmployeesCalendar(
                            dto.getEmployeesId(),
                            true,
                            null,
                            week.toString(),
                            timeSlot.getTimeSlotStart(),
                            timeSlot.getTimeSlotLength()
                    );
            Integer maxCalendarId = 0;
            synchronized (this){
                baseMapper.insert(employeesCalendar);
                maxCalendarId = ((EmployeesCalendar) CommonUtils.getMaxId("employees_calendar", this)).getId();
            }
            List<EmployeesCalendarDetails> employeesCalendarDetailsList = new ArrayList<>();
            Integer finalMaxCalendarId = maxCalendarId;
            timeSlot.getJobAndPriceList().forEach(jobAndPrice -> {
                EmployeesCalendarDetails employeesCalendarDetails =
                        new EmployeesCalendarDetails(
                                finalMaxCalendarId,
                                jobAndPrice.getJobId(),
                                jobAndPrice.getPrice(),
                                jobAndPrice.getCode()
                        );
                employeesCalendarDetailsList.add(employeesCalendarDetails);
            });
            employeesCalendarDetailsService.saveBatch(employeesCalendarDetailsList);
        });
        return R.ok("設置成功");
    }

    @Override
    public R addCalendarDate(SetEmployeesCalendarDateDTO dto) {
        /* dto合理性判斷 */
        List<String> res = this.rationalityJudgmentC(dto);
        if (res.size() == 0){
            //这是合理的
        }else {
            return R.failed(res, "數據不合理");
        }

        /* 鉴权处理 */
        String authenticationResult = this.authenticationProcessing(dto.getEmployeesId());
        if (!authenticationResult.equals(CommonConstants.AUTHENTICATION_SUCCESSFUL)){
            return R.failed(null, authenticationResult);
        }

        /* 员工存在性判断 */
        Boolean isOk = employeesDetailsService.judgmentOfExistence(dto.getEmployeesId());
        if (isOk){

        }else {
            return R.failed(null, "該員工不存在");
        }

        /* 添加新的 */
        dto.getTimeSlotList().forEach(timeSlot -> {
            EmployeesCalendar employeesCalendar =
                    new EmployeesCalendar(
                            dto.getEmployeesId(),
                            false,
                            dto.getDate(),
                            null,
                            timeSlot.getTimeSlotStart(),
                            timeSlot.getTimeSlotLength()
                    );
            Integer maxCalendarId = 0;
            synchronized (this){
                baseMapper.insert(employeesCalendar);
                maxCalendarId = ((EmployeesCalendar) CommonUtils.getMaxId("employees_calendar", this)).getId();
            }
            List<EmployeesCalendarDetails> employeesCalendarDetailsList = new ArrayList<>();
            Integer finalMaxCalendarId = maxCalendarId;
            timeSlot.getJobAndPriceList().forEach(jobAndPrice -> {
                EmployeesCalendarDetails employeesCalendarDetails =
                        new EmployeesCalendarDetails(
                                finalMaxCalendarId,
                                jobAndPrice.getJobId(),
                                jobAndPrice.getPrice(),
                                jobAndPrice.getCode()
                        );
                employeesCalendarDetailsList.add(employeesCalendarDetails);
            });
            employeesCalendarDetailsService.saveBatch(employeesCalendarDetailsList);
        });
        return R.ok("設置成功");
    }

    @Override
    public R setCalendar2(SetEmployeesCalendar2DTO dto) {
        /* dto合理性判斷 */
        List<String> res = this.rationalityJudgmentD(dto);
        if (res.size() == 0){
            //这是合理的
        }else {
            return R.failed(res, "數據不合理");
        }

        /* 员工存在性判断 */
        String roleType = TokenUtils.getRoleType();
        if (roleType.equals(CommonConstants.REQUEST_ORIGIN_ADMIN) || roleType.equals(CommonConstants.REQUEST_ORIGIN_EMPLOYEES)){
            if (!employeesDetailsService.judgmentOfExistence(dto.getEmployeesId())) return R.failed(null, "該員工不存在");
        }
        if (roleType.equals(CommonConstants.REQUEST_ORIGIN_COMPANY)){
            if (!employeesDetailsService.judgmentOfExistenceFromCompany(dto.getEmployeesId())) return R.failed(null, "該員工不存在");
        }
        if (roleType.equals(CommonConstants.REQUEST_ORIGIN_MANAGER)){
            if (!employeesDetailsService.judgmentOfExistenceHaveJurisdictionOverManager(dto.getEmployeesId())) return R.failed(null, "該員工不存在或不受您管辖");
        }

        /* 添加新的 */
        StringBuilder week = new StringBuilder();
        dto.getWeek().forEach(wk->{
            week.append(wk);
        });
        /* 填入工作内容，优先填入预设置的工作内容，如果为空，那么填入技能标签的工作内容 */
        List<Integer> jobIds = new ArrayList<>();
        EmployeesDetails employeesDetails = employeesDetailsService.getById(dto.getEmployeesId());
        String presetJobIds = employeesDetails.getPresetJobIds();
        if (CommonUtils.isNotEmpty(presetJobIds)){
            String[] ids = presetJobIds.split(" ");
            for (int i = 0; i < ids.length; i++) {
                jobIds.add(Integer.valueOf(ids[i]));
            }
        }else {
            List<SysJobContend> skillTag = (List<SysJobContend>) employeesCalendarService.getSkillTags(dto.getEmployeesId()).getData();
            jobIds = skillTag.stream().map(x->{
                return x.getId();
            }).collect(Collectors.toList());
        }

        List<Integer> finalJobIds = jobIds;
        dto.getTimeSlotPriceDTOList().forEach(timeSlot -> {
            EmployeesCalendar employeesCalendar =
                    new EmployeesCalendar(
                            dto.getEmployeesId(),
                            true,
                            null,
                            week.toString(),
                            timeSlot.getTimeSlotStart(),
                            timeSlot.getTimeSlotLength()
                    );
            Integer maxCalendarId = 0;
            synchronized (this){
                baseMapper.insert(employeesCalendar);
                maxCalendarId = ((EmployeesCalendar) CommonUtils.getMaxId("employees_calendar", this)).getId();
            }
            List<EmployeesCalendarDetails> employeesCalendarDetailsList = new ArrayList<>();
            Integer finalMaxCalendarId = maxCalendarId;
            finalJobIds.forEach(jobId -> {
                EmployeesCalendarDetails employeesCalendarDetails =
                        new EmployeesCalendarDetails(
                                finalMaxCalendarId,
                                jobId,
                                timeSlot.getPrice(),
                                timeSlot.getCode()
                        );
                employeesCalendarDetailsList.add(employeesCalendarDetails);
            });
            employeesCalendarDetailsService.saveBatch(employeesCalendarDetailsList);
        });

        return R.ok("設置成功");
    }

    @Override
    public R setJobs(SetEmployeesJobsDTO dto) {

        /* 员工存在性判断 */
        String roleType = TokenUtils.getRoleType();
        if (roleType.equals(CommonConstants.REQUEST_ORIGIN_ADMIN) || roleType.equals(CommonConstants.REQUEST_ORIGIN_EMPLOYEES)){
            if (!employeesDetailsService.judgmentOfExistence(dto.getEmployeesId())) return R.failed(null, "該員工不存在");
        }
        if (roleType.equals(CommonConstants.REQUEST_ORIGIN_COMPANY)){
            if (!employeesDetailsService.judgmentOfExistenceFromCompany(dto.getEmployeesId())) return R.failed(null, "該員工不存在");
        }
        if (roleType.equals(CommonConstants.REQUEST_ORIGIN_MANAGER)){
            if (!employeesDetailsService.judgmentOfExistenceHaveJurisdictionOverManager(dto.getEmployeesId())) return R.failed(null, "該員工不存在或不受您管辖");
        }

        /* 先设置到预设工作内容字段 */
        StringBuilder sb = new StringBuilder();
        dto.getJobIds().forEach(jobId->{
            sb.append(jobId);
            sb.append(" ");
        });
        String jobIdsString = new String(sb).trim();
        employeesDetailsService.setPresetJobIds(jobIdsString, dto.getEmployeesId());

        /* 再设置到现有的时间表中去 */
        QueryWrapper qw = new QueryWrapper();
        qw.eq("employees_id", dto.getEmployeesId());
        List<EmployeesCalendar> employeesCalendarList = employeesCalendarService.list(qw);
        List<Integer> calendarIds = employeesCalendarList.stream().map(x -> {
            return x.getId();
        }).collect(Collectors.toList());
        if (CommonUtils.isEmpty(employeesCalendarList)) return R.ok(null, "已設置預設鐘點工工作內容，但是該員工沒有設置鐘點工時間表");

        List<EmployeesCalendarDetails> employeesCalendarDetailsList = employeesCalendarDetailsService.groupByCalendarIdHaving(calendarIds);
        if (CommonUtils.isEmpty(employeesCalendarDetailsList)) return R.ok(null, "已設置預設鐘點工工作內容，但是該員工鐘點工時間表沒有設置工作內容");

        QueryWrapper qw2 = new QueryWrapper();
        qw2.in("calendar_id", calendarIds);
        employeesCalendarDetailsService.remove(qw2);

        List<EmployeesCalendarDetails> employeesCalendarDetailsList2 = new ArrayList<>();

        dto.getJobIds().forEach(jobId -> {
            employeesCalendarDetailsList.forEach(employeesCalendarDetails -> {
                EmployeesCalendarDetails res = new EmployeesCalendarDetails(
                        null,
                        employeesCalendarDetails.getCalendarId(),
                        jobId,
                        employeesCalendarDetails.getPrice(),
                        employeesCalendarDetails.getCode()
                );
                employeesCalendarDetailsList2.add(res);
            });
        });
        employeesCalendarDetailsService.saveBatch(employeesCalendarDetailsList2);

        return R.ok(null, "已設置預設鐘點工工作內容，同時也設置了鐘點工時間表的工作內容");
    }

    @Override
    public Map<LocalDate, List<TimeSlotDTO>> getCalendarByDateSlot(DateSlot dateSlot, Integer employeesId, String toCode) {

        /* 先得到三大map */
        SortListUtil<TimeSlotDTO> sort = new SortListUtil<TimeSlotDTO>();
        Map<LocalDate, List<TimeSlotDTO>> map1 = new HashMap<>();
        Map<Integer, List<TimeSlotDTO>> map2 = new HashMap<>();
        Map<String, List<TimeSlotDTO>> map3 = new HashMap<>();
        QueryWrapper qw = new QueryWrapper();
        qw.eq("employees_id", employeesId);
        List<EmployeesCalendar> employeesCalendarList = this.list(qw);
        if (CommonUtils.isEmpty(employeesCalendarList)){
            return null;
        }
        employeesCalendarList.forEach(employeesCalendar -> {
            QueryWrapper qw1 = new QueryWrapper();
            qw1.eq("calendar_id", employeesCalendar.getId());
            List<EmployeesCalendarDetails> employeesCalendarDetailsList = employeesCalendarDetailsService.list(qw1);
            List<JobAndPriceDTO> jobAndPriceDTOList = employeesCalendarDetailsList.stream().map(employeesCalendarDetails -> {
                JobAndPriceDTO jobAndPriceDTO = new JobAndPriceDTO();
                jobAndPriceDTO.setJobId(employeesCalendarDetails.getJobId());
                if (toCode.equals("")){
                    jobAndPriceDTO.setPrice(employeesCalendarDetails.getPrice());
                    jobAndPriceDTO.setCode(employeesCalendarDetails.getCode());
                }else {
                    BigDecimal price;
                    if (employeesCalendarDetails.getCode().equals(toCode)){
                        price = new BigDecimal(employeesCalendarDetails.getPrice());
                    }else {
                        price = currencyService.exchangeRateToBigDecimalAfterOptimization(employeesCalendarDetails.getCode(), toCode, new BigDecimal(employeesCalendarDetails.getPrice()));
                    }
                    jobAndPriceDTO.setPrice(new Float(price.toString()));
                    jobAndPriceDTO.setCode(toCode);
                }
                return jobAndPriceDTO;
            }).collect(Collectors.toList());
            TimeSlotDTO timeSlotDTO = new TimeSlotDTO();
            timeSlotDTO.setTimeSlotStart(employeesCalendar.getTimeSlotStart());
            timeSlotDTO.setTimeSlotLength(employeesCalendar.getTimeSlotLength());
            timeSlotDTO.setJobAndPriceList(jobAndPriceDTOList);
            if (CommonUtils.isEmpty(employeesCalendar.getStander())){
                List<TimeSlotDTO> timeSlotDTOS = map3.getOrDefault("", new ArrayList<>());
                timeSlotDTOS.add(timeSlotDTO);
                sort.Sort(timeSlotDTOS, "getTimeSlotStart", null);
                map3.put("", timeSlotDTOS);
            }else if (employeesCalendar.getStander() == false){ //日期
                List<TimeSlotDTO> timeSlotDTOS = map1.getOrDefault(employeesCalendar.getDate(), new ArrayList<>());
                timeSlotDTOS.add(timeSlotDTO);
                sort.Sort(timeSlotDTOS, "getTimeSlotStart", null);
                map1.put(employeesCalendar.getDate(), timeSlotDTOS);
            }else if (employeesCalendar.getStander() == true){ //周
                String weekString = employeesCalendar.getWeek();
                for (int i = 0; i < weekString.length(); i++) {
                    Integer weekInteger = Integer.valueOf(String.valueOf(weekString.charAt(i)));
                    List<TimeSlotDTO> timeSlotDTOS = map2.getOrDefault(weekInteger, new ArrayList<>());
                    timeSlotDTOS.add(timeSlotDTO);
                    sort.Sort(timeSlotDTOS, "getTimeSlotStart", null);
                    map2.put(weekInteger, timeSlotDTOS);
                }
            }
        });

        Map<LocalDate, List<TimeSlotDTO>> calendarMap = new HashMap<>();
        LocalDate start = dateSlot.getStart();
        LocalDate end = dateSlot.getEnd();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)){
            if (map1.containsKey(date)){
                //日期模板生效
                calendarMap.put(date, map1.get(date));
            }else if (!map2.isEmpty()){
                //周模板生效
                calendarMap.put(date, map2.getOrDefault(date.getDayOfWeek().getValue(), new ArrayList<>()));
            }else if (!map3.isEmpty()){
                //通用模板生效
                calendarMap.put(date, map3.get(""));
            }
        }

        return calendarMap;
    }

    @Override
    public Map<LocalDate, List<TimeSlotDTO>> getFreeTimeByDateSlot(DateSlot dateSlot, Integer employeesId, String toCode) {
        /* 2021-2-4 暂时先这样写着，目前还没做派任务，所以空闲时间=时间表 */
        return this.getCalendarByDateSlot(dateSlot, employeesId, toCode);
    }

    @Override
    public R getSkillTags(Integer employeesId) {
        /***
         * select c.id, c.contend
         *         from employees_calendar as a, employees_calendar_details as b, sys_job_contend as c
         *         where a.id = b.calendar_id and b.job_id = c.id and employees_id = #{employeesId}
         *         group by job_id;
         */
        return R.ok(baseMapper.getSkillTags(employeesId),"獲取成功");
    }

    @Override
    public List<FreeDateDTO> getFreeTimeByDateSlot2(DateSlot dateSlot, Integer empId, String code) {
        /* 2021-2-4 暂时先这样写着，目前还没做派任务，所以空闲时间=时间表 */
        return this.getCalendarByDateSlot2(dateSlot, empId, code);
    }

    @Override
    public R makeAnAppointment(MakeAnAppointmentDTO dto) {

        return null;
    }

    /*時間段合理性判斷   假設都不為空*/
    public List<String> rationalityJudgmentA(SetEmployeesCalendarDTO dto){
        List<String> resCollections = new ArrayList<>();//不合理性结果收集
        SortListUtil<TimeSlotDTO> sortList = new SortListUtil<TimeSlotDTO>();
        List<TimeSlotDTO> timeSlotDTOS = dto.getTimeSlotList();
        sortList.Sort(timeSlotDTOS, "getTimeSlotStart", null);
        for (int i = 0; i < timeSlotDTOS.size()-1; i++) {
            PeriodOfTime period1 = new PeriodOfTime(timeSlotDTOS.get(i).getTimeSlotStart(), timeSlotDTOS.get(i).getTimeSlotLength());
            PeriodOfTime period2 = new PeriodOfTime(timeSlotDTOS.get(i+1).getTimeSlotStart(), timeSlotDTOS.get(i+1).getTimeSlotLength());
            if (CommonUtils.doRechecking(period1, period2)){
                //重複的處理方式
                StringBuilder res = new StringBuilder();
                res.append("通用模板存在時間段重複：");
                res.append(period1.getTimeSlotStart().toString()).append("+").append(period1.getTimeSlotLength()).append("h");
                res.append("與");
                res.append(period2.getTimeSlotStart().toString()).append("+").append(period2.getTimeSlotLength()).append("h");
                resCollections.add(res.toString());
            }
        }
        return resCollections;
    }
    /*時間段合理性判斷：周   假設都不為空*/
    public List<String> rationalityJudgmentB(SetEmployeesCalendarWeekDTO dto){
        List<String> resCollections = new ArrayList<>();//不合理性结果收集
        Map<Integer, List<PeriodOfTime>> map = new HashMap<>();
        QueryWrapper qw = new QueryWrapper<>();
        qw.eq("employees_id", dto.getEmployeesId());
        qw.eq("stander", true);
        List<EmployeesCalendar> employeesCalendarList = this.list(qw);
        employeesCalendarList.forEach(calendar -> {
            List<Integer> weekList = new ArrayList<>();
            String weekStr = calendar.getWeek();
            for (int i = 0; i < weekStr.length(); i++) {
                weekList.add(Integer.valueOf(weekStr.charAt(i)-48));
            }
            PeriodOfTime period = new PeriodOfTime(calendar.getTimeSlotStart(), calendar.getTimeSlotLength());
            weekList.forEach(week -> {
                List<PeriodOfTime> exist = map.getOrDefault(week, new ArrayList<>());
                exist.add(period);
                map.put(week, exist);
            });
        });
        /*已准备好现有数据*/

        List<Integer> weekList = dto.getWeek();
        List<TimeSlotDTO> timeSlotDTOS = dto.getTimeSlotList();
        weekList.forEach(week -> {
            timeSlotDTOS.forEach(timeSlot -> {
                List<PeriodOfTime> exist = map.getOrDefault(week, new ArrayList<>());
                exist.add(new PeriodOfTime(timeSlot.getTimeSlotStart(), timeSlot.getTimeSlotLength()));
                map.put(week, exist);
            });
            /*下面对一周的每一天进行筛查*/
            List<PeriodOfTime> existDay = map.getOrDefault(week, new ArrayList<>());
            SortListUtil<PeriodOfTime> sortList = new SortListUtil<PeriodOfTime>();
            sortList.Sort(existDay, "getTimeSlotStart", null);
            for (int i = 0; i < existDay.size()-1; i++) {
                PeriodOfTime period1 = existDay.get(i);
                PeriodOfTime period2 = existDay.get(i+1);
                if (CommonUtils.doRechecking(period1, period2)){
                    //重複的處理方式
                    StringBuilder res = new StringBuilder();
                    res.append("周模板存在時間段重複： week ").append(week).append("  ");
                    res.append(period1.getTimeSlotStart().toString()).append("+").append(period1.getTimeSlotLength()).append("h");
                    res.append("與");
                    res.append(period2.getTimeSlotStart().toString()).append("+").append(period2.getTimeSlotLength()).append("h");
                    resCollections.add(res.toString());
                }
            }
        });
        return resCollections;
    }
    /*時間段合理性判斷：日期   假設都不為空*/
    public List<String> rationalityJudgmentC(SetEmployeesCalendarDateDTO dto){
        List<String> resCollections = new ArrayList<>();//不合理性结果收集
        QueryWrapper qw = new QueryWrapper<>();
        qw.eq("employees_id", dto.getEmployeesId());
        qw.eq("stander", false);
        qw.eq("date", dto.getDate());
        List<EmployeesCalendar> employeesCalendarList = this.list(qw);
        List<PeriodOfTime> periodOfTimeList1 = employeesCalendarList.stream().map(calendar -> {
            return new PeriodOfTime(calendar.getTimeSlotStart(), calendar.getTimeSlotLength());
        }).collect(Collectors.toList());
        List<TimeSlotDTO> timeSlotDTOList = dto.getTimeSlotList();
        List<PeriodOfTime> periodOfTimeList2 = timeSlotDTOList.stream().map(calendar -> {
            return new PeriodOfTime(calendar.getTimeSlotStart(), calendar.getTimeSlotLength());
        }).collect(Collectors.toList());
        periodOfTimeList1.addAll(periodOfTimeList2);
        /* periodOfTimeList1是当天的全部时间段 */
        SortListUtil<PeriodOfTime> sortList = new SortListUtil<PeriodOfTime>();
        sortList.Sort(periodOfTimeList1, "getTimeSlotStart", null);
        for (int i = 0; i < periodOfTimeList1.size()-1; i++) {
            PeriodOfTime period1 = periodOfTimeList1.get(i);
            PeriodOfTime period2 = periodOfTimeList1.get(i+1);
            if (CommonUtils.doRechecking(period1, period2)){
                //重複的處理方式
                StringBuilder res = new StringBuilder();
                res.append("日期模板存在時間段重複： date ").append(dto.getDate()).append("  ");
                res.append(period1.getTimeSlotStart().toString()).append("+").append(period1.getTimeSlotLength()).append("h");
                res.append("與");
                res.append(period2.getTimeSlotStart().toString()).append("+").append(period2.getTimeSlotLength()).append("h");
                resCollections.add(res.toString());
            }
        }
        return resCollections;
    }

    /*時間段合理性判斷：周   假設都不為空*/
    public List<String> rationalityJudgmentD(SetEmployeesCalendar2DTO dto){
        List<String> resCollections = new ArrayList<>();//不合理性结果收集
        Map<Integer, List<PeriodOfTime>> map = new HashMap<>();
        QueryWrapper qw = new QueryWrapper<>();
        qw.eq("employees_id", dto.getEmployeesId());
        qw.eq("stander", true);
        List<EmployeesCalendar> employeesCalendarList = this.list(qw);
        employeesCalendarList.forEach(calendar -> {
            List<Integer> weekList = new ArrayList<>();
            String weekStr = calendar.getWeek();
            for (int i = 0; i < weekStr.length(); i++) {
                weekList.add(Integer.valueOf(weekStr.charAt(i)-48));
            }
            PeriodOfTime period = new PeriodOfTime(calendar.getTimeSlotStart(), calendar.getTimeSlotLength());
            weekList.forEach(week -> {
                List<PeriodOfTime> exist = map.getOrDefault(week, new ArrayList<>());
                exist.add(period);
                map.put(week, exist);
            });
        });
        /*已准备好现有数据*/

        List<Integer> weekList = dto.getWeek();
        List<TimeSlotPriceDTO> timeSlotDTOS = dto.getTimeSlotPriceDTOList();
        weekList.forEach(week -> {
            timeSlotDTOS.forEach(timeSlot -> {
                List<PeriodOfTime> exist = map.getOrDefault(week, new ArrayList<>());
                exist.add(new PeriodOfTime(timeSlot.getTimeSlotStart(), timeSlot.getTimeSlotLength()));
                map.put(week, exist);
            });
            /*下面对一周的每一天进行筛查*/
            List<PeriodOfTime> existDay = map.getOrDefault(week, new ArrayList<>());
            SortListUtil<PeriodOfTime> sortList = new SortListUtil<PeriodOfTime>();
            sortList.Sort(existDay, "getTimeSlotStart", null);
            for (int i = 0; i < existDay.size()-1; i++) {
                PeriodOfTime period1 = existDay.get(i);
                PeriodOfTime period2 = existDay.get(i+1);
                if (CommonUtils.doRechecking(period1, period2)){
                    //重複的處理方式
                    StringBuilder res = new StringBuilder();
                    res.append("周模板存在時間段重複： week ").append(week).append("  ");
                    res.append(period1.getTimeSlotStart().toString()).append("+").append(period1.getTimeSlotLength()).append("h");
                    res.append("與");
                    res.append(period2.getTimeSlotStart().toString()).append("+").append(period2.getTimeSlotLength()).append("h");
                    resCollections.add(res.toString());
                }
            }
        });
        return resCollections;
    }


    private String authenticationProcessing(Integer employeesId){
        String roleType = TokenUtils.getRoleType();
        if(roleType.equals(CommonConstants.REQUEST_ORIGIN_ADMIN)){
            return CommonConstants.AUTHENTICATION_SUCCESSFUL;//直接鉴权放行
        }else if (roleType.equals(CommonConstants.REQUEST_ORIGIN_COMPANY)){
            //需要检查公司账户下有没有这个保洁员id
            return companyDetailsService.thereIsACleaner(employeesId) ? CommonConstants.AUTHENTICATION_SUCCESSFUL : "保潔員不存在";
        }else if (roleType.equals(CommonConstants.REQUEST_ORIGIN_CUSTOMER)){
            return "客戶怎麼能調這個藉口";
        }else if (roleType.equals(CommonConstants.REQUEST_ORIGIN_MANAGER)){
            /* 判断该保洁员受不受到我管辖 */
            return managerDetailsService.thereIsACleaner(employeesId) ? CommonConstants.AUTHENTICATION_SUCCESSFUL : "該保潔員不被您管轄或者保潔員不存在";
        }else if (roleType.equals(CommonConstants.REQUEST_ORIGIN_EMPLOYEES)){
            /* 判斷保潔員是不是我 */
            return employeesDetailsService.isMe(employeesId) ? CommonConstants.AUTHENTICATION_SUCCESSFUL : "保潔員只能設置自己的時間表";
        }else {
            return CommonConstants.AUTHENTICATION_FAILED;
        }
    }


    public List<FreeDateDTO> getCalendarByDateSlot2(DateSlot dateSlot, Integer employeesId, String toCode) {

        /* 先得到三大map */
        SortListUtil<TimeSlotDTO> sort = new SortListUtil<TimeSlotDTO>();
        Map<LocalDate, List<TimeSlotDTO>> map1 = new HashMap<>();
        Map<Integer, List<TimeSlotDTO>> map2 = new HashMap<>();
        Map<String, List<TimeSlotDTO>> map3 = new HashMap<>();
        QueryWrapper qw = new QueryWrapper();
        qw.eq("employees_id", employeesId);
        List<EmployeesCalendar> employeesCalendarList = this.list(qw);
        if (CommonUtils.isEmpty(employeesCalendarList)){
            return null;
        }
        employeesCalendarList.forEach(employeesCalendar -> {
            QueryWrapper qw1 = new QueryWrapper();
            qw1.eq("calendar_id", employeesCalendar.getId());
            List<EmployeesCalendarDetails> employeesCalendarDetailsList = employeesCalendarDetailsService.list(qw1);
            List<JobAndPriceDTO> jobAndPriceDTOList = employeesCalendarDetailsList.stream().map(employeesCalendarDetails -> {
                JobAndPriceDTO jobAndPriceDTO = new JobAndPriceDTO();
                jobAndPriceDTO.setJobId(employeesCalendarDetails.getJobId());
                if (toCode.equals("")){
                    jobAndPriceDTO.setPrice(employeesCalendarDetails.getPrice());
                    jobAndPriceDTO.setCode(employeesCalendarDetails.getCode());
                }else {
                    BigDecimal price;
                    if (employeesCalendarDetails.getCode().equals(toCode)){
                        price = new BigDecimal(employeesCalendarDetails.getPrice());
                    }else {
                        price = currencyService.exchangeRateToBigDecimalAfterOptimization(employeesCalendarDetails.getCode(), toCode, new BigDecimal(employeesCalendarDetails.getPrice()));
                    }
                    jobAndPriceDTO.setPrice(new Float(price.toString()));
                    jobAndPriceDTO.setCode(toCode);
                }
                return jobAndPriceDTO;
            }).collect(Collectors.toList());
            TimeSlotDTO timeSlotDTO = new TimeSlotDTO();
            timeSlotDTO.setTimeSlotStart(employeesCalendar.getTimeSlotStart());
            timeSlotDTO.setTimeSlotLength(employeesCalendar.getTimeSlotLength());
            timeSlotDTO.setJobAndPriceList(jobAndPriceDTOList);
            if (CommonUtils.isEmpty(employeesCalendar.getStander())){
                List<TimeSlotDTO> timeSlotDTOS = map3.getOrDefault("", new ArrayList<>());
                timeSlotDTOS.add(timeSlotDTO);
                sort.Sort(timeSlotDTOS, "getTimeSlotStart", null);
                map3.put("", timeSlotDTOS);
            }else if (employeesCalendar.getStander() == false){ //日期
                List<TimeSlotDTO> timeSlotDTOS = map1.getOrDefault(employeesCalendar.getDate(), new ArrayList<>());
                timeSlotDTOS.add(timeSlotDTO);
                sort.Sort(timeSlotDTOS, "getTimeSlotStart", null);
                map1.put(employeesCalendar.getDate(), timeSlotDTOS);
            }else if (employeesCalendar.getStander() == true){ //周
                String weekString = employeesCalendar.getWeek();
                for (int i = 0; i < weekString.length(); i++) {
                    Integer weekInteger = Integer.valueOf(String.valueOf(weekString.charAt(i)));
                    List<TimeSlotDTO> timeSlotDTOS = map2.getOrDefault(weekInteger, new ArrayList<>());
                    timeSlotDTOS.add(timeSlotDTO);
                    sort.Sort(timeSlotDTOS, "getTimeSlotStart", null);
                    map2.put(weekInteger, timeSlotDTOS);
                }
            }
        });

        List<FreeDateDTO> freeDateDTOS = new ArrayList<>();

        LocalDate start = dateSlot.getStart();
        LocalDate end = dateSlot.getEnd();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)){
            FreeDateDTO freeDateDTO = new FreeDateDTO();
            if (map1.containsKey(date)){
                //日期模板生效
                freeDateDTO.setDate(date);
                freeDateDTO.setTimes(map1.get(date));
            }else if (!map2.isEmpty()){
                //周模板生效
                freeDateDTO.setDate(date);
                freeDateDTO.setTimes(map2.getOrDefault(date.getDayOfWeek().getValue(), new ArrayList<>()));
            }else if (!map3.isEmpty()){
                freeDateDTO.setDate(date);
                freeDateDTO.setTimes(map3.get(""));
                //通用模板生效
            }
            freeDateDTOS.add(freeDateDTO);
        }

        return freeDateDTOS;
    }

}
