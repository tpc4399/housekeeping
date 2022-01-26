package com.housekeeping.admin.service.impl;

import com.aliyun.oss.OSSClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.*;
import com.housekeeping.admin.entity.*;
import com.housekeeping.admin.mapper.EmployeesContractMapper;
import com.housekeeping.admin.pojo.ConfirmOrderPOJO;
import com.housekeeping.admin.pojo.OrderDetailsPOJO;
import com.housekeeping.admin.pojo.WorkDetailsPOJO;
import com.housekeeping.admin.service.*;
import com.housekeeping.admin.vo.ContractJobVo;
import com.housekeeping.admin.vo.EmployeesContractJobVo;
import com.housekeeping.admin.vo.TimeSlot;
import com.housekeeping.common.entity.PeriodOfTime;
import com.housekeeping.common.utils.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
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
import java.util.concurrent.TimeUnit;
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
    @Resource
    private IOrderIdService orderIdService;
    @Resource
    private ICustomerAddressService customerAddressService;
    @Resource
    private ICustomerDetailsService customerDetailsService;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private IEmployeesCalendarService employeesCalendarService;
    @Resource
    private IOrderDetailsService orderDetailsService;
    @Resource
    private ICompanyDetailsService companyDetailsService;
    @Resource
    private ISerialNumberService serialNumberService;
    @Resource
    private ISerialService serialService;
    @Resource
    private ISysConfigService configService;

    @Override
    public R getByEmployeesId(Integer employeesId) {
        QueryWrapper<EmployeesContract> qw = new QueryWrapper<>();
        qw.eq("employees_id",employeesId);
        List<EmployeesContractDTO> collect = this.list(qw).stream().map(x -> {
            EmployeesContractDTO employeesContractDTO = new EmployeesContractDTO(x);

            String presetJobIds = x.getJobs();
            List<Skill> skills = new ArrayList<>();
            List<String> strings = Arrays.asList(presetJobIds.split(" "));
            for (int i = 0; i < strings.size(); i++) {
                Skill skill = new Skill();
                skill.setJobId(Integer.parseInt(strings.get(i)));
                skill.setContend(sysJobContendService.getById(Integer.parseInt(strings.get(i))).getContend());
                skills.add(skill);
            }
            employeesContractDTO.setJobContends(skills);

            EmployeesDetails byId = employeesDetailsService.getById(x.getEmployeesId());

            employeesContractDTO.setEmployeesDetails(byId);
            return employeesContractDTO;
        }).collect(Collectors.toList());
        return R.ok(collect);
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
        map.put("employeesId",byId.getEmployeesId());
        map.put("name",byId.getName());
        map.put("description",byId.getDescription());
        map.put("photoUrls",byId.getPhotoUrls());
        map.put("dayWage",byId.getDayWage());
        map.put("code",byId.getCode());
        map.put("activityIds",byId.getActivityIds());
        map.put("dateLength",byId.getDateLength());
        map.put("timeLength",byId.getTimeLength());
        map.put("totalPrice",byId.getTotalPrice());

        HashSet<Integer> index = new HashSet<>();
        String jobs = byId.getJobs();
        String[] jobIds = jobs.split(" ");
        for (int i = 0; i < jobIds.length; i++) {
            QueryWrapper<SysIndexContent> qw = new QueryWrapper<>();
            qw.eq("content_id",jobIds[i]);
            List<SysIndexContent> one = sysIndexContentService.list(qw);
            for (int i1 = 0; i1 < one.size(); i1++) {
                index.add(one.get(i1).getIndexId());
            }
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

        map.put("jobs",employeesContractJobVos);

        /* 工作内容技能标签返回 */
        List<Integer> jobIdsInteger = CommonUtils.stringToList(jobs);
        List<SysJobContend> sysJobContends = new ArrayList<>();
        if (!jobIdsInteger.isEmpty()) sysJobContends = sysJobContendService.listByIds(Arrays.asList(jobIds));
        map.put("jobContends", sysJobContends);

        return R.ok(map);
    }

    @Override
    public R appointmentContract(AppointmentContractDTO dto) {
        LocalDateTime now = LocalDateTime.now();
        OrderDetailsPOJO odp = new OrderDetailsPOJO();
        EmployeesContract ec = this.getById(dto.getContractId());
        if (CommonUtils.isEmpty(ec)) return R.failed(null, "包工不存在");

        /*//订单来源
        odp.setOrderOrigin(CommonConstants.ORDER_ORIGIN_CONTRACT);

        // 订单编号
        Long number = orderIdService.generateId();
        odp.setNumber(number.toString());

        // 流水号
        String serialNumber = serialNumberService.generateSerialNumber(number);
        odp.setSerialNumber(serialNumber);

        // 消费项目
        odp.setConsumptionItems("2");*/

        odp.setContractImageUrl(ec.getPhotoUrls());
        odp.setContractName(ec.getName());

        /* 订单甲方 保洁员 */
        Integer employeesId = ec.getEmployeesId();
        Boolean exist = employeesDetailsService.judgmentOfExistence(employeesId);
        if (!exist) return R.failed(null, "保潔員不存在");
        EmployeesDetails ed = employeesDetailsService.getById(employeesId);
        odp.setEmployeesId(ed.getId());
        odp.setName1(ed.getName());
        odp.setPhPrefix1(ed.getPhonePrefix());
        odp.setPhone1(ed.getPhone());

        /* 甲方所属公司 */
        CompanyDetails cod = companyDetailsService.getById(ed.getCompanyId());
        odp.setCompanyId(cod.getId().toString());
        odp.setInvoiceName(cod.getInvoiceName());
        odp.setInvoiceNumber(cod.getInvoiceNumber());

        /* 订单乙方 客户 */
        CustomerDetails cd = customerDetailsService.getByUserId(TokenUtils.getCurrentUserId());
        CustomerAddress ca = customerAddressService.getById(dto.getAddressId());
        odp.setCustomerId(cd.getId());
        odp.setName2(cd.getName());
        odp.setPhPrefix2(ca.getPhonePrefix());
        odp.setPhone2(ca.getPhone());

        /* 订单工作内容 */
        String jobIds = ec.getJobs();
        odp.setJobIds(jobIds);

        /* 订单工作内容 */
        odp.setNoteIds(null);


        /* 地址 */
        odp.setAddress(ca.getAddress());
        odp.setLat(new Float(ca.getLat()));
        odp.setLng(new Float(ca.getLng()));


        /* 工作时间安排 */
        LocalDate start = dto.getStartDate();
        LocalDate end = dto.getStartDate().plusDays(ec.getDateLength()-1);
        List<Integer> weeks = Arrays.asList(1,2,3,4,5,6,7);
        List<Integer> jobIdsList = CommonUtils.stringToList(jobIds);
        List<TimeSlot> timeSlots = new ArrayList<>();
        TimeSlot timeSlot = new TimeSlot(dto.getStartTime(), ec.getTimeLength());
        timeSlots.add(timeSlot);
        MakeAnAppointmentDTO mapDTO = new MakeAnAppointmentDTO(employeesId, null, start, end, weeks, jobIdsList,null, timeSlots);
        List<WorkDetailsPOJO> wds = employeesCalendarService.makeAnAppointmentHandle2(mapDTO, false,ec.getTotalPrice());
        odp.setWorkDetails(wds);

        /* 可工作天数计算 */
        Integer days = employeesCalendarService.days(wds);
        odp.setDays(days);

        /* 每日工作时长计算 */
        Float h = ec.getTimeLength();
        odp.setHOfDay(h);

        /* 原价格计算 */
        odp.setPriceBeforeDiscount(ec.getTotalPrice());

        //媒合费
        QueryWrapper qw = new QueryWrapper();
        qw.eq("config_key", "matchmakingFeeFloat");
        SysConfig one = configService.getOne(qw);
        odp.setMatchmakingFee(ec.getTotalPrice().multiply(new BigDecimal(one.getConfigValue()).multiply(BigDecimal.valueOf(0.01))).setScale(0,BigDecimal.ROUND_DOWN));

        //系统服务费
        QueryWrapper qw2 = new QueryWrapper();
        qw2.eq("config_key", "systemServiceFeeFloat");
        SysConfig one2 = configService.getOne(qw2);
        odp.setSystemServiceFee(ec.getTotalPrice().multiply(new BigDecimal(one2.getConfigValue()).multiply(BigDecimal.valueOf(0.01))).setScale(0,BigDecimal.ROUND_DOWN));

        //刷卡手续费
        QueryWrapper qw3 = new QueryWrapper();
        qw3.eq("config_key", "servicesChargeForCreditCardFloat");
        SysConfig one3 = configService.getOne(qw3);
        odp.setCardSwipeFee(ec.getTotalPrice().multiply(new BigDecimal(one3.getConfigValue()).multiply(BigDecimal.valueOf(0.01))).setScale(0,BigDecimal.ROUND_DOWN));

        //平台費（客戶）
        QueryWrapper qw4 = new QueryWrapper();
        qw4.eq("config_key", "platformFeeCus");
        SysConfig one4 = configService.getOne(qw4);
        BigDecimal bigDecimal = new BigDecimal(one4.getConfigValue());
        odp.setPlatformFeeCus(bigDecimal);

        //平台費（員工）
        QueryWrapper qw5 = new QueryWrapper();
        qw5.eq("config_key", "platformFeeEmp");
        SysConfig one5 = configService.getOne(qw5);
        odp.setPlatformFeeEmp(new BigDecimal(one5.getConfigValue()));

        //服务费
        odp.setPriceAfterDiscount(ec.getTotalPrice().add(bigDecimal));


        return R.ok(new ConfirmOrderPOJO(odp));
    }

    @Override
    public R confirm(AppointmentContractDTO dto) {
        LocalDateTime now = LocalDateTime.now();
        OrderDetailsPOJO odp = new OrderDetailsPOJO();
        EmployeesContract ec = this.getById(dto.getContractId());
        if (CommonUtils.isEmpty(ec)) return R.failed(null, "包工不存在");

        /* 订单来源 */
        odp.setOrderOrigin(CommonConstants.ORDER_ORIGIN_CONTRACT);
        odp.setContractImageUrl(ec.getPhotoUrls());
        odp.setContractName(ec.getName());

        /* 订单编号 */
        Long number = orderIdService.generateId();
        odp.setNumber(number.toString());

        /* 流水号 */
        String serialNumber = serialNumberService.generateSerialNumber(number);
        odp.setSerialNumber(serialNumber);

        /* 消费项目 */
        odp.setConsumptionItems("2");

        /* 订单甲方 保洁员 */
        Integer employeesId = ec.getEmployeesId();
        Boolean exist = employeesDetailsService.judgmentOfExistence(employeesId);
        if (!exist) return R.failed(null, "保潔員不存在");
        EmployeesDetails ed = employeesDetailsService.getById(employeesId);
        odp.setEmployeesId(ed.getId());
        odp.setName1(ed.getName());
        odp.setPhPrefix1(ed.getPhonePrefix());
        odp.setPhone1(ed.getPhone());

        /* 甲方所属公司 */
        CompanyDetails cod = companyDetailsService.getById(ed.getCompanyId());
        odp.setCompanyId(cod.getId().toString());
        odp.setInvoiceName(cod.getInvoiceName());
        odp.setInvoiceNumber(cod.getInvoiceNumber());

        /* 订单乙方 客户 */
        CustomerDetails cd = customerDetailsService.getByUserId(TokenUtils.getCurrentUserId());
        CustomerAddress ca = customerAddressService.getById(dto.getAddressId());
        odp.setCustomerId(cd.getId());
        odp.setName2(cd.getName());
        odp.setPhPrefix2(ca.getPhonePrefix());
        odp.setPhone2(ca.getPhone());

        /* 订单工作内容 */
        String jobIds = ec.getJobs();
        odp.setJobIds(jobIds);

        /* 订单工作内容 */
        odp.setNoteIds(null);


        /* 地址 */
        odp.setAddress(ca.getAddress());
        odp.setLat(new Float(ca.getLat()));
        odp.setLng(new Float(ca.getLng()));


        /* 工作时间安排 */
        LocalDate start = dto.getStartDate();
        LocalDate end = dto.getStartDate().plusDays(ec.getDateLength()-1);
        List<Integer> weeks = Arrays.asList(1,2,3,4,5,6,7);
        List<Integer> jobIdsList = CommonUtils.stringToList(jobIds);
        List<TimeSlot> timeSlots = new ArrayList<>();
        TimeSlot timeSlot = new TimeSlot(dto.getStartTime(), ec.getTimeLength());
        timeSlots.add(timeSlot);
        MakeAnAppointmentDTO mapDTO = new MakeAnAppointmentDTO(employeesId, null, start, end, weeks, jobIdsList,null, timeSlots);
        List<WorkDetailsPOJO> wds = employeesCalendarService.makeAnAppointmentHandle2(mapDTO, false,ec.getTotalPrice());
        odp.setWorkDetails(wds);

        /* 可工作天数计算 */
        Integer days = employeesCalendarService.days(wds);
        odp.setDays(days);

        /* 每日工作时长计算 */
        Float h = ec.getTimeLength();
        odp.setHOfDay(h);

        /* 原价格计算 */
        odp.setPriceBeforeDiscount(ec.getTotalPrice());

        //媒合费
        QueryWrapper qw = new QueryWrapper();
        qw.eq("config_key", "matchmakingFeeFloat");
        SysConfig one = configService.getOne(qw);
        odp.setMatchmakingFee(ec.getTotalPrice().multiply(new BigDecimal(one.getConfigValue()).multiply(BigDecimal.valueOf(0.01))).setScale(0,BigDecimal.ROUND_DOWN));

        //系统服务费
        QueryWrapper qw2 = new QueryWrapper();
        qw2.eq("config_key", "systemServiceFeeFloat");
        SysConfig one2 = configService.getOne(qw2);
        odp.setSystemServiceFee(ec.getTotalPrice().multiply(new BigDecimal(one2.getConfigValue()).multiply(BigDecimal.valueOf(0.01))).setScale(0,BigDecimal.ROUND_DOWN));

        //刷卡手续费
        QueryWrapper qw3 = new QueryWrapper();
        qw3.eq("config_key", "servicesChargeForCreditCardFloat");
        SysConfig one3 = configService.getOne(qw3);
        odp.setCardSwipeFee(ec.getTotalPrice().multiply(new BigDecimal(one3.getConfigValue()).multiply(BigDecimal.valueOf(0.01))).setScale(0,BigDecimal.ROUND_DOWN));

        //平台費（客戶）
        QueryWrapper qw4 = new QueryWrapper();
        qw4.eq("config_key", "platformFeeCus");
        SysConfig one4 = configService.getOne(qw4);
        BigDecimal bigDecimal = new BigDecimal(one4.getConfigValue());
        odp.setPlatformFeeCus(bigDecimal);

        //平台費（員工）
        QueryWrapper qw5 = new QueryWrapper();
        qw5.eq("config_key", "platformFeeEmp");
        SysConfig one5 = configService.getOne(qw5);
        odp.setPlatformFeeEmp(new BigDecimal(one5.getConfigValue()));

        //服务费
        odp.setPriceAfterDiscount(ec.getTotalPrice().add(bigDecimal));

        /* 订单状态 */
        odp.setOrderState(CommonConstants.ORDER_STATE_TO_BE_PAID);//待支付状态

        /* 订单生成时间 */
        odp.setStartDateTime(now);
        odp.setUpdateDateTime(now);

        /* 订单截止付款时间 保留时间 */
        Integer hourly = orderDetailsService.orderRetentionTime(employeesId);
        LocalDateTime payDeadline = now.plusHours(hourly);
        odp.setPayDeadline(payDeadline);
        odp.setH(hourly);

        String key = "OrderToBePaid:employeesId"+employeesId+":" + number;
        Map<String, Object> map = new HashMap<>();
        try {
            map = CommonUtils.objectToMap(odp);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        redisTemplate.opsForHash().putAll(key, map);
        redisTemplate.expire(key, hourly, TimeUnit.HOURS);
        serialService.generatePipeline(odp);

        return R.ok(new ConfirmOrderPOJO(odp), "预约成功");
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
