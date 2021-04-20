package com.housekeeping.admin.service.impl;

import com.aliyun.oss.OSSClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.AddEmployeesContractDTO;
import com.housekeeping.admin.dto.DateSlot;
import com.housekeeping.admin.entity.EmployeesContract;
import com.housekeeping.admin.entity.EmployeesContractDetails;
import com.housekeeping.admin.entity.SysIndex;
import com.housekeeping.admin.entity.SysIndexContent;
import com.housekeeping.admin.mapper.EmployeesContractMapper;
import com.housekeeping.admin.service.*;
import com.housekeeping.admin.vo.ContractJobVo;
import com.housekeeping.admin.vo.EmployeesContractJobVo;
import com.housekeeping.admin.vo.TimeSlot;
import com.housekeeping.common.entity.PeriodOfTime;
import com.housekeeping.common.utils.*;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @Author su
 * @Date 2021/1/30 17:22
 */
@Service("employeesContractService")
public class EmployeesContractServiceImpl
        extends ServiceImpl<EmployeesContractMapper, EmployeesContract>
        implements IEmployeesContractService {

    @Resource
    private IEmployeesContractDetailsService employeesContractDetailsService;
    @Resource
    private EmployeesDetailsService employeesDetailsService;
    @Resource
    private OSSClient ossClient;
    @Value("${oss.bucketName}")
    private String bucketName;
    @Value("${oss.urlPrefix}")
    private String urlPrefix;
    @Resource
    private ISysIndexContentService sysIndexContentService;
    @Resource
    private ISysIndexService sysIndexService;
    @Resource
    private ISysJobContendService sysJobContendService;

    @Override
    public R add(AddEmployeesContractDTO dto) {
        /* 時間段合理性判斷 */
        List<String> resCollections = rationalityJudgment(dto);//不合理性结果收集
        if (resCollections.size() != 0){
            return R.failed(resCollections, "時間段不合理");
        }

        /* 员工存在性判断 */
        String roleType = TokenUtils.getRoleType();
        if (roleType.equals(CommonConstants.REQUEST_ORIGIN_ADMIN) || roleType.equals(CommonConstants.REQUEST_ORIGIN_EMPLOYEES)){
            if (!employeesDetailsService.judgmentOfExistence(dto.getEmployeesId())) return R.failed(null, "該員工不存在");
        }
        if (roleType.equals(CommonConstants.REQUEST_ORIGIN_COMPANY)){
            if (!employeesDetailsService.judgmentOfExistenceFromCompany(dto.getEmployeesId())) return R.failed(null, "該員工不存在");
        }

        /* 添加包工業務 */
        EmployeesContract employeesContract = new EmployeesContract(dto);
        List<Integer> activityIds = dto.getActivityIds();
        StringBuilder activityIdsStr = new StringBuilder();
        activityIds.forEach(x->{
            activityIdsStr.append(x);
        });
        employeesContract.setActivityIds(activityIdsStr.toString());
        StringBuilder jobs = new StringBuilder();
        dto.getJobs().forEach(jobId -> {
            jobs.append(" " + jobId);
        });
        employeesContract.setJobs(jobs.toString().trim());
        Integer maxContractId = 0;
        synchronized (this){
            this.save(employeesContract);
            maxContractId = ((EmployeesContract) CommonUtils.getMaxId("employees_contract", this)).getId();
        }
        List<EmployeesContractDetails> employeesContractDetailsList = new ArrayList<>();
        Integer finalMaxContractId = maxContractId;
        dto.getWeekAndTimeSlotsList().forEach(weekAndTimeSlots -> {
            StringBuilder weekStr = new StringBuilder();
            weekAndTimeSlots.getWeek().forEach(week -> {
                weekStr.append(week);
            });
            weekAndTimeSlots.getTimeSlot().forEach(timeSlot -> {
                EmployeesContractDetails employeesContractDetails = new EmployeesContractDetails();
                employeesContractDetails.setContractId(finalMaxContractId);
                employeesContractDetails.setWeek(weekStr.toString());
                employeesContractDetails.setTimeSlotStart(timeSlot.getTimeSlotStart());
                employeesContractDetails.setTimeSlotLength(timeSlot.getTimeSlotLength());
                employeesContractDetailsList.add(employeesContractDetails);
            });
        });
        employeesContractDetailsService.saveBatch(employeesContractDetailsList);
        return R.ok(maxContractId, "添加成功");
    }

    @Override
    public R getByEmployeesId(Integer employeesId) {
        QueryWrapper<EmployeesContract> qw = new QueryWrapper<>();
        qw.eq("employees_id",employeesId);
        List<EmployeesContract> list = this.list(qw);
        return R.ok(list);
    }

    @Override
    public Map<LocalDate, List<TimeSlot>> getCalendarByContractId(DateSlot dateSlot, Integer contractId) {
        Map<LocalDate, List<TimeSlot>> res = new HashMap<>();
        SortListUtil<TimeSlot> sort = new SortListUtil<TimeSlot>();
        Map<Integer, List<TimeSlot>> week = new HashMap<>();
        QueryWrapper qw = new QueryWrapper();
        qw.eq("contract_id", contractId);
        List<EmployeesContractDetails> employeesContractDetails = employeesContractDetailsService.list(qw);
        employeesContractDetails.forEach(employeesContractDetail -> {
            String weekString = employeesContractDetail.getWeek();
            TimeSlot timeSlot = new TimeSlot();
            timeSlot.setTimeSlotStart(employeesContractDetail.getTimeSlotStart());
            timeSlot.setTimeSlotLength(employeesContractDetail.getTimeSlotLength());
            for (int i = 0; i < weekString.length(); i++) {
                Integer weekInteger = Integer.valueOf(String.valueOf(weekString.charAt(i)));
                List<TimeSlot> timeSlots = week.getOrDefault(weekInteger, new ArrayList<>());
                timeSlots.add(timeSlot);
                sort.Sort(timeSlots, "getTimeSlotStart", null);
                week.put(weekInteger, timeSlots);
            }
        });

        LocalDate start = dateSlot.getStart();
        LocalDate end = dateSlot.getEnd();

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)){
            res.put(date, week.getOrDefault(date.getDayOfWeek().getValue(), new ArrayList<>()));
        }
        return res;
    }

    @Override
    public Map<LocalDate, List<TimeSlot>> getFreeTimeByContractId(DateSlot dateSlot, Integer contractId) {
        /* 2021-2-4 暂时先这样写着，目前还没做派任务，所以空闲时间=时间表 */
        return this.getCalendarByContractId(dateSlot, contractId);
    }

    @Override
    public R add2(Integer employeesId, String name, MultipartFile[] image, Integer dateLength, Float timeLength, BigDecimal totalPrice, Integer[] jobs, String description, Integer[] actives) {
        /* 员工存在性判断 */
        String roleType = TokenUtils.getRoleType();
        if (roleType.equals(CommonConstants.REQUEST_ORIGIN_EMPLOYEES)){
            employeesId = employeesDetailsService.getEmployeesIdByExistToken();
            if (!employeesDetailsService.judgmentOfExistence(employeesId)) return R.failed(null, "該員工不存在");
        }
        if (roleType.equals(CommonConstants.REQUEST_ORIGIN_ADMIN)){
            if (!employeesDetailsService.judgmentOfExistence(employeesId)) return R.failed(null, "該員工不存在");
        }
        if (roleType.equals(CommonConstants.REQUEST_ORIGIN_COMPANY)){
            if (!employeesDetailsService.judgmentOfExistenceFromCompany(employeesId)) return R.failed(null, "該員工不存在");
        }
        if (roleType.equals(CommonConstants.REQUEST_ORIGIN_MANAGER)){
            if (employeesDetailsService.judgmentOfExistenceHaveJurisdictionOverManager(employeesId)) return R.failed(null, "该员工不存在或不受您管辖");
        }

        /* 先将image存入oss，返回链接然后将数据存入数据库 */
        AtomicReference<String> res = new AtomicReference<>("");

        LocalDateTime now = LocalDateTime.now();
        String nowString = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String catalogue = CommonConstants.HK_CONTRACT_PHOTOS_ABSTRACT_PATH_PREFIX_PROV;
        File mkdir = new File(catalogue);
        if (!mkdir.exists()){
            mkdir.mkdirs();
        }
        AtomicReference<Integer> count = new AtomicReference<>(0);
        Arrays.stream(image).forEach(file -> {
            String fileType = file.getOriginalFilename().split("\\.")[1];
            String fileName = nowString + "[" + count.toString() + "]."+ fileType;
            String fileAbstractPath = catalogue + fileName;
            try {
                ossClient.putObject(bucketName, fileAbstractPath, new ByteArrayInputStream(file.getBytes()));
                res.set(urlPrefix + fileAbstractPath + " " + res.get());
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                count.getAndSet(count.get() + 1);
            }
        });
        String photoUrls = res.get().trim();
        String jobsStr = CommonUtils.arrToString(jobs);
        String activesStr = CommonUtils.arrToString(actives);
        EmployeesContract ec = new EmployeesContract(null, employeesId, jobsStr, name, description, photoUrls, null, "TWD", activesStr, dateLength, timeLength, totalPrice);
        this.save(ec);
        return R.ok(null, "添加成功");
    }

    @Override
    public R update(Integer id, String name, MultipartFile[] image, Integer dateLength, Float timeLength, BigDecimal totalPrice, Integer[] jobs, String description, Integer[] actives) {
        EmployeesContract ec = this.getById(id);

        if (image.length != 0){
            /* 先将image存入oss，返回链接然后将数据存入数据库 */
            AtomicReference<String> res = new AtomicReference<>("");

            LocalDateTime now = LocalDateTime.now();
            String nowString = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String catalogue = CommonConstants.HK_CONTRACT_PHOTOS_ABSTRACT_PATH_PREFIX_PROV;
            File mkdir = new File(catalogue);
            if (!mkdir.exists()){
                mkdir.mkdirs();
            }
            AtomicReference<Integer> count = new AtomicReference<>(0);
            Arrays.stream(image).forEach(file -> {
                String fileType = file.getOriginalFilename().split("\\.")[1];
                String fileName = nowString + "[" + count.toString() + "]."+ fileType;
                String fileAbstractPath = catalogue + fileName;
                try {
                    ossClient.putObject(bucketName, fileAbstractPath, new ByteArrayInputStream(file.getBytes()));
                    res.set(urlPrefix + fileAbstractPath + " " + res.get());
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    count.getAndSet(count.get() + 1);
                }
            });
            String photoUrls = res.get().trim();
            ec.setPhotoUrls(photoUrls);
        }
        String jobsStr = CommonUtils.arrToString(jobs);
        String activesStr = CommonUtils.arrToString(actives);
        ec.setJobs(jobsStr);
        ec.setName(name);
        ec.setDescription(description);
        ec.setActivityIds(activesStr);
        ec.setDateLength(dateLength);
        ec.setTimeLength(timeLength);
        ec.setTotalPrice(totalPrice);
        this.updateById(ec);
        return R.ok(null, "修改成功");
    }

    @Override
    public R cusGetById(Integer id) {
        HashMap<String, Object> map = new HashMap<>();

        EmployeesContract byId = this.getById(id);
        if(CommonUtils.isEmpty(byId)){
            return R.ok(null);
        }

        map.put("id",byId.getId());
        map.put("employees_id",byId.getEmployeesId());
        map.put("name",byId.getName());
        map.put("description",byId.getDescription());
        map.put("photo_urls",byId.getPhotoUrls());
        map.put("day_wage",byId.getDayWage());
        map.put("code",byId.getCode());
        map.put("activity_ids",byId.getActivityIds());
        map.put("date_length",byId.getDateLength());
        map.put("time_length",byId.getTimeLength());
        map.put("total_price",byId.getTotalPrice());

        HashSet<Integer> index = new HashSet<>();
        String jobs = byId.getJobs();
        String[] jobIds = jobs.split(" ");
        for (int i = 0; i < jobIds.length; i++) {
            QueryWrapper<SysIndexContent> qw = new QueryWrapper<>();
            qw.eq("content_id",jobIds[i]);
            SysIndexContent one = sysIndexContentService.getOne(qw);
            index.add(one.getIndexId());
        }

        List<SysIndex> sysIndexList = sysIndexService.list();
        List<EmployeesContractJobVo> employeesContractJobVos = sysIndexList.stream().map(x -> {
            EmployeesContractJobVo employeesContractJobVo = new EmployeesContractJobVo();
            employeesContractJobVo.setId(x.getId());
            employeesContractJobVo.setName(x.getName());
            if (index.contains(x.getId())) {
                employeesContractJobVo.setStatus(true);
            } else {
                employeesContractJobVo.setStatus(false);
            }
            List<ContractJobVo> contractJobVos = new ArrayList<>();
            QueryWrapper qw = new QueryWrapper();
            qw.eq("index_id", x.getId());
            List<SysIndexContent> sysIndexContents = sysIndexContentService.list(qw);
            for (int i = 0; i < sysIndexContents.size(); i++) {
                ContractJobVo contractJobVo = new ContractJobVo();
                contractJobVo.setId(sysIndexContents.get(i).getId());
                contractJobVo.setName(sysJobContendService.getById(sysIndexContents.get(i).getContentId()).getContend());
                if (jobs.contains(sysIndexContents.get(i).getContentId().toString())) {
                    contractJobVo.setStatus(true);
                } else {
                    contractJobVo.setStatus(false);
                }
                contractJobVos.add(contractJobVo);
            }
            employeesContractJobVo.setContents(contractJobVos);
            return employeesContractJobVo;
        }).collect(Collectors.toList());

        map.put("content",employeesContractJobVos);

        return R.ok(map);
    }

    List<String> rationalityJudgment(AddEmployeesContractDTO dto){
        List<String> resCollections = new ArrayList<>();//不合理性结果收集
        SortListUtil<TimeSlot> sort = new SortListUtil<TimeSlot>();
        Map<Integer, List<TimeSlot>> map = new HashMap<>();
        dto.getWeekAndTimeSlotsList().forEach(weekAndTimeSlots -> {
            weekAndTimeSlots.getWeek().forEach(week -> {
                weekAndTimeSlots.getTimeSlot().forEach(timeSlot -> {
                    List<TimeSlot> existTimeSlot = map.getOrDefault(week, new ArrayList<>());
                    existTimeSlot.add(timeSlot);
                    map.put(week, existTimeSlot);
                });
            });
        });
        for (int i = 1; i < 8; i++) {
            List<TimeSlot> existSlot = map.get(i);
            sort.Sort(existSlot, "getTimeSlotStart", null);
            for (int j = 0; j < existSlot.size() - 1; j++) {
                PeriodOfTime period1 = new PeriodOfTime(existSlot.get(j).getTimeSlotStart(), existSlot.get(j).getTimeSlotLength());
                PeriodOfTime period2 = new PeriodOfTime(existSlot.get(j+1).getTimeSlotStart(), existSlot.get(j+1).getTimeSlotLength());
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
        }
        return resCollections;
    }
}
