package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.*;
import com.housekeeping.admin.entity.*;
import com.housekeeping.admin.mapper.SysIndexMapper;
import com.housekeeping.admin.service.*;
import com.housekeeping.admin.vo.EmployeesHandleVo;
import com.housekeeping.admin.vo.PriceSlotVo;
import com.housekeeping.admin.vo.SysIndexVo;
import com.housekeeping.admin.vo.TimeSlot;
import com.housekeeping.common.entity.EmployeesScope;
import com.housekeeping.common.entity.PeriodOfTime;
import com.housekeeping.common.entity.PeriodOfTimeWithHourlyWage;
import com.housekeeping.common.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @Author su
 * @Date 2021/1/12 14:48
 */
@Slf4j
@Service("sysIndexService")
public class SysIndexServiceImpl
        extends ServiceImpl<SysIndexMapper, SysIndex>
        implements ISysIndexService {

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
    @Resource
    private ICurrencyService currencyService;
    @Resource
    private IAddressCodingService addressCodingService;
    @Resource
    private ISysIndexContentService sysIndexContentService;
    @Resource
    private ICompanyDetailsService companyDetailsService;
    @Resource
    private IEmployeesContractService employeesContractService;
    @Resource
    private ISysConfigService sysConfigService;
    @Resource
    private ISysAddressAreaService sysAddressAreaService;
    @Resource
    private ISysJobContendService sysJobContendService;
    @Resource
    private IAsyncService asyncService;
    @Resource
    private RedisUtils redisUtils;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public R add(SysIndexAddDto sysIndexAddDto) {
        SysIndex sysIndex = new SysIndex();
        sysIndex.setName(sysIndexAddDto.getName());
        sysIndex.setOrderValue(sysIndexAddDto.getOrderValue());
        sysIndex.setSelectedLogo(sysIndexAddDto.getSelectedLogo());
        sysIndex.setUncheckedLogo(sysIndexAddDto.getUncheckedLogo());
        StringBuilder priceSlot = new StringBuilder("");
        sysIndexAddDto.getPriceSlotList().forEach(x->{
            priceSlot.append(x.getLowPrice());
            priceSlot.append(" ");
        });
        sysIndex.setPriceSlot(new String(priceSlot).trim().replace(" ", ","));
        Integer maxIndexId = 0;
        synchronized (this){
            this.save(sysIndex);
            maxIndexId = ((SysIndex) CommonUtils.getMaxId("sys_index", this)).getId();
        }

        Integer finalMaxIndexId = maxIndexId;
        sysIndexAddDto.getJobs().forEach(x->{
            SysIndexContent sysIndexContent = new SysIndexContent();
            sysIndexContent.setIndexId(finalMaxIndexId);
            sysIndexContent.setContentId(x);
            sysIndexContentService.save(sysIndexContent);
        });

        return R.ok("添加成功");
    }

    @Override
    public R update(SysIndexUpdateDTO dto) {
        SysIndex sysIndex = new SysIndex();
        sysIndex.setName(dto.getName());
        sysIndex.setOrderValue(dto.getOrderValue());
        sysIndex.setSelectedLogo(dto.getSelectedLogo());
        sysIndex.setUncheckedLogo(dto.getUncheckedLogo());
        StringBuilder priceSlot = new StringBuilder("");
        dto.getPriceSlotList().forEach(x->{
            priceSlot.append(x.getLowPrice());
            priceSlot.append(" ");
        });
        sysIndex.setPriceSlot(new String(priceSlot).trim().replace(" ", ","));
        this.updateById(sysIndex);

        List<SysIndexContent> contendIds = new ArrayList<>();
        dto.getJobs().forEach(x->{
            SysIndexContent sysIndexContent = new SysIndexContent();
            sysIndexContent.setIndexId(dto.getId());
            sysIndexContent.setContentId(x);
            contendIds.add(sysIndexContent);
        });
        QueryWrapper deleteQw = new QueryWrapper();
        deleteQw.eq("index_id", dto.getId());
        sysIndexContentService.remove(deleteQw);
        sysIndexContentService.saveBatch(contendIds);
        return R.ok("修改成功");
    }

    @Override
    public R delete(Integer indexId) {
        QueryWrapper qw = new QueryWrapper();
        qw.eq("index_id", indexId);
        sysIndexContentService.remove(qw);//删除依赖1
        this.removeById(indexId);//删除
        return R.ok("刪除成功");
    }

    @Override
    public R getAll() {
        List<SysIndex> sysIndexList = this.list();
        List<SysIndexVo> sysIndexVoList = sysIndexList.stream().map(x->{
            SysIndexVo sysIndexVo = new SysIndexVo();
            sysIndexVo.setId(x.getId());
            sysIndexVo.setName(x.getName());
            sysIndexVo.setOrderValue(x.getOrderValue());
            sysIndexVo.setSelectedLogo(x.getSelectedLogo());
            sysIndexVo.setUncheckedLogo(x.getUncheckedLogo());
            List<PriceSlotVo> priceSlotVoList = new ArrayList<>();
            String[] arr = x.getPriceSlot().split(",");
            for (int i = 0; i < arr.length - 1; i++) {
                // i和i+1
                String lowPrice = arr[i];
                String highPrice = arr[i+1];
                PriceSlotVo priceSlot = new PriceSlotVo(
                        new BigDecimal(lowPrice),
                        new BigDecimal(highPrice)
                );
                priceSlotVoList.add(priceSlot);
            }
            PriceSlotVo priceSlot = new PriceSlotVo(
                    new BigDecimal(arr[arr.length-1]),
                    null
            );
            priceSlotVoList.add(priceSlot);
            sysIndexVo.setPriceSlotList(priceSlotVoList);
            return sysIndexVo;
        }).collect(Collectors.toList());
        return R.ok(sysIndexVoList, "获取成功");
    }

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
    public R query(QueryIndexDTO dto) throws InterruptedException {

        /***
         * 判空
         */
        Integer indexId = OptionalBean.ofNullable(dto)
                .getBean(QueryIndexDTO::getIndexId).get();
        List<Integer> jobs = OptionalBean.ofNullable(dto)
                .getBean(QueryIndexDTO::getJobs).get();
        Integer type = OptionalBean.ofNullable(dto)
                .getBean(QueryIndexDTO::getType).get();
        LocalDate start = OptionalBean.ofNullable(dto)
                .getBean(QueryIndexDTO::getStart).get();
        LocalDate end = OptionalBean.ofNullable(dto)
                .getBean(QueryIndexDTO::getEnd).get();
        List<TimeSlot> timeSlots = OptionalBean.ofNullable(dto)
                .getBean(QueryIndexDTO::getTimeSlots).get();
        BigDecimal lowPrice = OptionalBean.ofNullable(dto)
                .getBean(QueryIndexDTO::getLowPrice).get();
        BigDecimal highPrice = OptionalBean.ofNullable(dto)
                .getBean(QueryIndexDTO::getHighPrice).get();
        AddressDetailsDTO addressDetailsDTO = OptionalBean.ofNullable(dto)
                .getBean(QueryIndexDTO::getAddressDetails).get();
        Integer priorityType = OptionalBean.ofNullable(dto)
                .getBean(QueryIndexDTO::getPriorityType).get();
        String toCode = "TWD";

        List<String> resFailed = new ArrayList<>();
        if (CommonUtils.isEmpty(indexId)) resFailed.add("元素_id為空");
        if (CommonUtils.isEmpty(jobs)) resFailed.add("元素_jobs為空");
        if (CommonUtils.isEmpty(type)) resFailed.add("元素_type為空");
        if (CommonUtils.isEmpty(start)) resFailed.add("元素_start為空");
        if (CommonUtils.isEmpty(end)) resFailed.add("元素_end為空");
        if (CommonUtils.isEmpty(timeSlots)) resFailed.add("元素_timeSlots為空");
        if (CommonUtils.isEmpty(lowPrice)) resFailed.add("元素_lowPrice為空");
        if (CommonUtils.isEmpty(highPrice)) resFailed.add("元素_highPrice為空");
        if (CommonUtils.isEmpty(addressDetailsDTO)) resFailed.add("元素_addressDetailsDTO為空");

        if (!resFailed.isEmpty()){
            return R.failed(resFailed, "存在空值");
        }

        //TODO 多段時間段的合理性判斷
        List<String> collections = rationalityJudgmentTimeSlotList(timeSlots);
        if (collections.size() != 0){
            return R.failed(collections, "時間段不合理，請核查");
        }

        /** contendIds元素内容加工: 根据主页元素id获取工作内容标签 */
//        List<Integer> contendId = this.indexIdHandleContendId(indexId);
        List<Integer> contendId = new ArrayList<>(jobs);

        /** matchingCompanyIds 匹配的公司的ids和员工信息 需要后续填入 */
        Map<Integer, List<IndexQueryResultEmployees>> matchingCompanyIdsAndEmployeesDetails = new ConcurrentHashMap<>();

        /** indexQueryResultEmployeesList 匹配的员工信息整合 */
        List<IndexQueryResultEmployees> matchingEmployeesDetails = Collections.synchronizedList(new ArrayList<>());

        /** employeesSearchPool 员工搜索池 */
        Map<Integer, List<Integer>> employeesSearchPool = this.getEmployeesSearchPool();

        /** score 匹配方案的各项权重，分值 */
        Map<String, String> weight = sysConfigService.getScopeConfig(priorityType);

        ExecutorService exr = Executors.newCachedThreadPool();
        Long startMill = System.currentTimeMillis();
        employeesSearchPool.get(type).forEach(existEmployeesId -> {
            EmployeesHandleVo vo = new EmployeesHandleVo(
                    existEmployeesId,
                    addressDetailsDTO,
                    start,
                    end,
                    indexId,
                    timeSlots,
                    type,
                    contendId
            );
            exr.submit(new Runnable() {
                @Override
                public void run() {
                    Long startMill = System.currentTimeMillis();
                    existEmployeesHandle(vo, matchingCompanyIdsAndEmployeesDetails, matchingEmployeesDetails, toCode, weight, lowPrice, highPrice);
                    Long endMill = System.currentTimeMillis();
                    Long length = endMill - startMill;
                    log.info("employeesId:" + existEmployeesId +"  use:"+ length+"ms");
                }
            });
        });
        exr.shutdown();
        exr.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        Long endMill = System.currentTimeMillis();
        Long length = endMill - startMill;
        log.info("all" +"  use:"+ length+"ms");
        /**
         * 搜索结果总共四个板块：
         * 【推荐公司】  1、公司推广列表里面的公司 2、公司手底下有保洁员被匹配（时间段，工作内容） 3、排序: 推荐值降序
         * 【推荐保洁员】1、员工推广列表里面的员工 2、员工可以被匹配（时间段，工作内容）3、排序: 推荐值降序
         * 【附近保洁员】1、匹配到的员工 2、排序: 距离升序
         * 【最佳保洁员】1、匹配到的员工 2、排序: 评分降序
         *
         * 推荐值：根据保洁员的匹配度，能匹配到的包工数量，能做的工作数量，出勤率等参数来计算
         * 匹配: 保洁员的空闲时间匹配（如果选择了钟点工），或者保洁员下面有包工产品的时间段与之匹配（如果选择了包工） 保洁员能出勤的时长超过80%，在时间方面就算匹配成功
         * 评分：所有订单评分的平均值
         */
        List<IndexQueryResultCompany> recommendedCompany = new ArrayList<>();//推荐公司

        //排序器
        SortListUtil<IndexQueryResultCompany> sort1 = new SortListUtil<>();
        SortListUtil<IndexQueryResultEmployees> sort2 = new SortListUtil<>();

        //1
        matchingCompanyIdsAndEmployeesDetails.forEach((x, y) -> {
            IndexQueryResultCompany indexQueryResultCompany = new IndexQueryResultCompany();
            indexQueryResultCompany.setCompanyDetail(companyDetailsService.getById(x));
            indexQueryResultCompany.setMatchingEmployeesDetails(y);
            AtomicReference<Double> recommendScope = new AtomicReference<>(new Double(0.0));
            y.forEach(z -> {
                recommendScope.set(recommendScope.get() + z.getRecommendedScope());
            });
            QueryWrapper qw2 = new QueryWrapper();
            qw2.gt("end_time", LocalDateTime.now());
            qw2.eq("company_id", x);
            CompanyPromotion res = companyPromotionService.getOne(qw2);
            if (CommonUtils.isNotEmpty(res)){
                recommendScope.set(recommendScope.get() + new Double(weight.get(ApplicationConfigConstants.extensionCompanyScopeDouble)));
            }
            indexQueryResultCompany.setRecommendedScope(recommendScope.get());
            recommendedCompany.add(indexQueryResultCompany);
        });
        sort1.SortByDouble(recommendedCompany, "getRecommendedScope", "desc");//需要根据推荐分降序排序

        List<IndexQueryResultEmployees> recommendedCleaner = new ArrayList<>(matchingEmployeesDetails);//推荐保洁员
        sort2.SortByDouble(recommendedCleaner, "getRecommendedScope", "desc");//需要根据推荐分降序排序

        Map<String, Integer> number = sysConfigService.getNumber();
        Integer a= number.get(ApplicationConfigConstants.numberOfConsecutiveEmployeesInteger);
        Integer b = number.get(ApplicationConfigConstants.numberOfConsecutiveCompanyInteger);
        Integer sum = a + b;
        Integer count = recommendedCleaner.size() + recommendedCompany.size();
        List<IndexResult> indexResults = new ArrayList<>();
        for (int i = 0; i < count ; i++) {
            Integer exist = i%sum;
            IndexResult indexResult = null;
//            if (recommendedCleaner.size() == 0 && recommendedCompany.size() == 0){
//                break;
//            }
            if (recommendedCleaner.size() == 0 && recommendedCompany.size() != 0){
                //公司
                indexResult = new IndexResult(recommendedCompany.remove(0));
                indexResults.add(indexResult);
                continue;
            }
            if (recommendedCompany.size() == 0 && recommendedCleaner.size() != 0){
                //保洁
                indexResult = new IndexResult(recommendedCleaner.remove(0));
                indexResults.add(indexResult);
                continue;
            }
            if (0 <= exist && exist < a){
                //保洁
                indexResult = new IndexResult(recommendedCleaner.remove(0));
                indexResults.add(indexResult);
                continue;
            }
            if (a <= exist && exist < sum){
                //公司
                indexResult = new IndexResult(recommendedCompany.remove(0));
                indexResults.add(indexResult);
                continue;
            }
        }
//        Map<String, List> map = new HashMap<>();
//        map.put("recommendedCompany", recommendedCompany);
//        map.put("recommendedCleaner", recommendedCleaner);

        return R.ok(indexResults, "搜索成功");
    }

    @Override
    public R tree() {
        List<SysIndex> sysIndexList = this.list();
        List<SysIndexVo> sysIndexVoList = sysIndexList.stream().map(x->{
            SysIndexVo sysIndexVo = new SysIndexVo();
            sysIndexVo.setId(x.getId());
            sysIndexVo.setName(x.getName());
            sysIndexVo.setOrderValue(x.getOrderValue());
            sysIndexVo.setSelectedLogo(x.getSelectedLogo());
            sysIndexVo.setUncheckedLogo(x.getUncheckedLogo());
            List<PriceSlotVo> priceSlotVoList = new ArrayList<>();
            String[] arr = x.getPriceSlot().split(",");
            for (int i = 0; i < arr.length - 1; i++) {
                // i和i+1
                String lowPrice = arr[i];
                String highPrice = arr[i+1];
                PriceSlotVo priceSlot = new PriceSlotVo(
                        new BigDecimal(lowPrice),
                        new BigDecimal(highPrice)
                );
                priceSlotVoList.add(priceSlot);
            }
            PriceSlotVo priceSlot = new PriceSlotVo(
                    new BigDecimal(arr[arr.length-1]),
                    null
            );
            priceSlotVoList.add(priceSlot);
            sysIndexVo.setPriceSlotList(priceSlotVoList);
            List<SysJobContend> sysJobContends = new ArrayList<>();
            QueryWrapper qw = new QueryWrapper();
            qw.eq("index_id", x.getId());
            List<SysIndexContent> sysIndexContents = sysIndexContentService.list(qw);
            sysIndexContents.forEach(sysIndexContent -> {
                SysJobContend sysJobContend = sysJobContendService.getById(sysIndexContent.getContentId());
                sysJobContends.add(sysJobContend);
            });
            sysIndexVo.setSysJobContends(sysJobContends);
            return sysIndexVo;
        }).collect(Collectors.toList());
        return R.ok(sysIndexVoList, "查詢成功");
    }

    @Override
    public R defaultRecommendation(AddressDTO dto) {
        Map<String, List> res = new HashMap<>();
        Map<String, Integer> map = sysConfigService.getDefaultRecommendationInteger();
        Integer defaultRecommendationCompanyInteger = map.get(ApplicationConfigConstants.defaultRecommendationCompanyInteger);
        Integer defaultRecommendationEmployeesInteger = map.get(ApplicationConfigConstants.defaultRecommendationEmployeesInteger);
        List<EmployeesDetails> employeesDetails = employeesDetailsService.list();
        Collections.shuffle(employeesDetails);//先随机打乱排序
        List<EmployeesInstanceDTO> dos = employeesDetails.stream().map(x -> {
            if (dto.getGetGPSSuccess()){
                String str = CommonUtils.getInstanceByPoint(x.getLat(), x.getLng(), dto.getLat().toString(), dto.getLng().toString());
                Double instance = new Double(str);
                EmployeesInstanceDTO employeesInstanceDTO = new EmployeesInstanceDTO(x, instance);
                return employeesInstanceDTO;
            }else {
                EmployeesInstanceDTO employeesInstanceDTO = new EmployeesInstanceDTO(x, new Double(0));
                return employeesInstanceDTO;
            }
        }).collect(Collectors.toList());
        if (dto.getGetGPSSuccess()){
            SortListUtil<EmployeesInstanceDTO> sort = new SortListUtil<>();
            sort.SortByDouble(dos, "getInstance", null);
        }

        /* 生成异步任务，将dos和dos的公司保存到redis */
        asyncService.setRedisDos(dos);

        List<Integer> companyIds = new ArrayList<>();
        for (int i = 0; i < defaultRecommendationCompanyInteger*5; i++) {
            EmployeesInstanceDTO emp = dos.get(i);
            Integer comId = emp.getEmployeesDetails().getCompanyId();
            if (!companyIds.contains(comId)){
                companyIds.add(comId);
            }
            if (companyIds.size() >= defaultRecommendationCompanyInteger) break;
        }
        List<CompanyDetails> companyDetails = companyDetailsService.listByIds(companyIds);

        dos = new ArrayList<>(dos.subList(0, defaultRecommendationEmployeesInteger));
        res.put("employees", dos);
        res.put("company", companyDetails);
        return R.ok(res, "獲取成功");
    }

    @Override
    public R more1(AddressDTO dto) {
        List<AddrDTO> list = new ArrayList<>();
        if (dto.getGetGPSSuccess()){
            List<Object> field = Arrays.asList("lat", "lng");
            list = (List<AddrDTO>) redisUtils.keys("employees:*:details").stream().map(x -> {
                List<String> position = redisUtils.hmget((String) x, field);
                String str = CommonUtils.getInstanceByPoint(position.get(0), position.get(1), dto.getLat().toString(), dto.getLng().toString());
                Double instance = new Double(str);
                return new AddrDTO((String)x,instance);
            }).collect(Collectors.toList());
            SortListUtil<AddrDTO> sortListUtil = new SortListUtil();
            sortListUtil.SortByDouble(list, "getInstance", ""); //按照距离升序排序
        }else {
            list = (List<AddrDTO>) redisUtils.keys("employees:*:details").stream().map(x -> {
                return new AddrDTO((String)x, new Double(0));
            }).collect(Collectors.toList());
            Collections.shuffle(list); //打乱
        }
        String code = CommonUtils.getMysteriousCode();
        String key = "employeesMoreList:"+code;
        list.forEach(x->{
            redisTemplate.opsForList().rightPush(key, x);
        });
        redisTemplate.expire(key, 12, TimeUnit.HOURS);//设置12小时的过期时间

        List<EmployeesInstanceDTO> employeesInstanceDTOArrayList = new ArrayList<>();
        Map<String, Object> res = new HashMap<>();
        //一次调用弹出十个
        for (int i = 0; i < 10; i++) {
            AddrDTO addrDTO = (AddrDTO) redisTemplate.opsForList().leftPop(key);
            EmployeesDetails employeesDetails = null;
            if (CommonUtils.isEmpty(addrDTO)){
                break;
            }
            String key2 = addrDTO.getKey();
            Map<Object, Object> maps = redisTemplate.opsForHash().entries(key2);
            try {
                employeesDetails = (EmployeesDetails) CommonUtils.mapToObject(maps, EmployeesDetails.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            EmployeesInstanceDTO employeesInstanceDTO = new EmployeesInstanceDTO(employeesDetails, addrDTO.getInstance());
            employeesInstanceDTOArrayList.add(employeesInstanceDTO);
        }
        res.put("list", employeesInstanceDTOArrayList);
        res.put("credential", code);
        return R.ok(res, "获取成功");
    }

    @Override
    public R more2(AddressDTO dto) {
        List<Integer> comIds = (List<Integer>) redisUtils.keys("company:*:details").stream().map(x -> {
            return Integer.valueOf(((String)x).split(":")[1]);
        }).collect(Collectors.toList());
        Collections.shuffle(comIds);
        String code = CommonUtils.getMysteriousCode();
        String key = "companyMoreList:"+code;
        comIds.forEach(x->{
            redisTemplate.opsForList().rightPush(key, x);
        });
        redisTemplate.expire(key, 12, TimeUnit.HOURS);//设置12小时的过期时间
        List<CompanyDetails> companyDetails = new ArrayList<>();
        Map<String, Object> res = new HashMap<>();
        //一次调用弹出十个
        for (int i = 0; i < 10; i++) {
            Integer comId = (Integer) redisTemplate.opsForList().leftPop(key);
            CompanyDetails companyDetail = null;
            if (CommonUtils.isEmpty(comId)){
                break;
            }
            String key2 = "company:"+comId+":details";
            Map<Object, Object> maps = redisTemplate.opsForHash().entries(key2);
            try {
                companyDetail = (CompanyDetails) CommonUtils.mapToObject(maps, CompanyDetails.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            companyDetails.add(companyDetail);
        }
        res.put("list", companyDetails);
        res.put("credential", code);
        return R.ok(res, "获取成功");
    }

    @Override
    public R goon1(String credential) {
        String key = "employeesMoreList:"+credential;
        List<EmployeesInstanceDTO> employeesInstanceDTOS = new ArrayList<>();
        //一次调用弹出十个
        for (int i = 0; i < 10; i++) {
            AddrDTO addrDTO = (AddrDTO) redisTemplate.opsForList().leftPop(key);
            EmployeesDetails employeesDetails = null;
            if (CommonUtils.isEmpty(addrDTO)){
                break;
            }
            try {
                employeesDetails = (EmployeesDetails) CommonUtils.mapToObject(redisTemplate.opsForHash().entries(addrDTO.getKey()), EmployeesDetails.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            EmployeesInstanceDTO employeesInstanceDTO = new EmployeesInstanceDTO(employeesDetails, addrDTO.getInstance());
            employeesInstanceDTOS.add(employeesInstanceDTO);
        }
        return R.ok(employeesInstanceDTOS, "获取成功");
    }

    @Override
    public R goon2(String credential) {
        String key = "companyMoreList:"+credential;
        List<CompanyDetails> companyDetails = new ArrayList<>();
        //一次调用弹出十个
        for (int i = 0; i < 10; i++) {
            Integer comId = (Integer) redisTemplate.opsForList().leftPop(key);
            CompanyDetails companyDetail = null;
            if (CommonUtils.isEmpty(comId)){
                break;
            }
            String key2 = "company:"+comId+":details";
            Map<Object, Object> maps = redisTemplate.opsForHash().entries(key2);
            try {
                companyDetail = (CompanyDetails) CommonUtils.mapToObject(maps, CompanyDetails.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            companyDetails.add(companyDetail);
        }
        return R.ok(companyDetails, "获取成功");
    }

    @Override
    public R flush1(String credential) {
        String key = "employeesMoreList:"+credential;
        redisTemplate.delete(key);
        return R.ok(null, "成功清除緩存");
    }

    @Override
    public R flush2(String credential) {
        String key = "companyMoreList:"+credential;
        redisTemplate.delete(key);
        return R.ok(null, "成功清除緩存");
    }


    /* 求交集,不改变原list */
    private List<Integer> getIntersection(List<Integer> a, List<Integer> b){
        List<Integer> a1 = new ArrayList<>(a);
        List<Integer> b1 = new ArrayList<>(b);
        a1.retainAll(b1);
        return a1;
    }

    /* 求並集,不改变原list */
    public List<Integer> getUnion(List<Integer> a, List<Integer> b){
        List<Integer> a1 = new ArrayList<>(a);
        List<Integer> b1 = new ArrayList<>(b);
        a1.forEach(x -> {
            if (b1.contains(x)){

            }else {
                b1.add(x);
            }
        });
        return b1;
    }

    private Map<LocalDate, List<PeriodOfTimeWithHourlyWage>> getMap1(Map<Object, List<EmployeesCalendar>> calendarMap, String toCode){
        Map<LocalDate, List<PeriodOfTimeWithHourlyWage>> map1 = new HashMap<>();
        calendarMap.get(false).forEach(dateRule -> {
            List list1 = map1.getOrDefault(dateRule.getDate(), new ArrayList<>());
//            BigDecimal hourlyWage = currencyService.exchangeRateToBigDecimal(dateRule.getCode(), toCode, dateRule.getHourlyWage());
//            list1.add(new PeriodOfTimeWithHourlyWage(dateRule.getTimeSlotStart(), dateRule.getTimeSlotLength(), hourlyWage));
            map1.put(dateRule.getDate(), list1);
        });
        return map1;
    }

    private Map<Integer, List<PeriodOfTimeWithHourlyWage>> getMap2(Map<Object, List<EmployeesCalendar>> calendarMap, String toCode){
        Map<Integer, List<PeriodOfTimeWithHourlyWage>> map2 = new HashMap<>();
        calendarMap.get(true).forEach(weekRule -> {
            String weekString = weekRule.getWeek();
            for (int i = 0; i < weekString.length(); i++) {
                Integer weekInteger = Integer.valueOf(String.valueOf(weekString.charAt(i)));
                List list = map2.getOrDefault(weekInteger, new ArrayList<>());
//                BigDecimal hourlyWage = currencyService.exchangeRateToBigDecimal(weekRule.getCode(), toCode, weekRule.getHourlyWage());
//                list.add(new PeriodOfTimeWithHourlyWage(weekRule.getTimeSlotStart(), weekRule.getTimeSlotLength(), hourlyWage));
                map2.put(weekInteger, list);
            }
        });
        return map2;
    }

    private Map<String, List<PeriodOfTimeWithHourlyWage>> getMap3(Map<Object, List<EmployeesCalendar>> calendarMap, String toCode){
        Map<String, List<PeriodOfTimeWithHourlyWage>> map3 = new HashMap<>();
        calendarMap.get(null).forEach(x -> {
            List list = map3.getOrDefault(null, new ArrayList<>());
//            BigDecimal hourlyWage = currencyService.exchangeRateToBigDecimal(x.getCode(), toCode, x.getHourlyWage());
//            list.add(new PeriodOfTimeWithHourlyWage(x.getTimeSlotStart(), x.getTimeSlotLength(), hourlyWage));
            map3.put(null, list);
        });
        return map3;
    }

    private Map<LocalDate, List<PeriodOfTime>> getMap11(Map<Object, List<EmployeesCalendar>> calendarMap){
        Map<LocalDate, List<PeriodOfTime>> map1 = new HashMap<>();
        Map<LocalDate, List<PeriodOfTime>> map11 = new HashMap<>();
        calendarMap.get(false).forEach(dateRule -> {
            List list1 = map1.getOrDefault(dateRule.getDate(), new ArrayList<>());
            list1.add(new PeriodOfTime(dateRule.getTimeSlotStart(), dateRule.getTimeSlotLength()));
            map1.put(dateRule.getDate(), list1);
        });
        map1.forEach((date, list) -> {
            List<PeriodOfTime> periodOfTimes = list;
            if (list.size() == 0 || list.size() == 1){
                //什么也不做
            }else {
                for (int i = list.size() - 1; i > 0; i--) {
                    LocalTime xStart = list.get(i).getTimeSlotStart();
                    LocalTime xEnd = list.get(i).getTimeSlotStart()
                            .plusHours((int) (list.get(i).getTimeSlotLength()/1))
                            .plusMinutes((long) ((list.get(i).getTimeSlotLength()%1)* 60));
                    LocalTime yStart = list.get(i-1).getTimeSlotStart();
                    LocalTime yEnd = list.get(i-1).getTimeSlotStart()
                            .plusHours((int) (list.get(i-1).getTimeSlotLength()/1))
                            .plusMinutes((long) ((list.get(i-1).getTimeSlotLength()%1)* 60));
                    if (xEnd.equals(yStart)){
                        PeriodOfTime z = new PeriodOfTime();
                        z.setTimeSlotStart(xStart);
                        z.setTimeSlotLength(list.get(i).getTimeSlotLength() + list.get(i-1).getTimeSlotLength());
                        //需要干点啥
                        periodOfTimes.add(z);
                    }else if (yEnd.equals(xStart)){
                        PeriodOfTime z = new PeriodOfTime();
                        z.setTimeSlotStart(yStart);
                        z.setTimeSlotLength(list.get(i).getTimeSlotLength() + list.get(i-1).getTimeSlotLength());
                        //需要干点啥
                        periodOfTimes.remove(i);
                        periodOfTimes.remove(i-1);
                        periodOfTimes.add(z);
                    }else {
                        //没检测到相邻的，啥也不干
                    }
                }
            }
            map11.put(date, periodOfTimes);
        });
        return map11;
    }

    private Map<Integer, List<PeriodOfTime>> getMap22(Map<Object, List<EmployeesCalendar>> calendarMap){
        Map<Integer, List<PeriodOfTime>> map2 = new HashMap<>();
        Map<Integer, List<PeriodOfTime>> map22 = new HashMap<>();
        calendarMap.get(true).forEach(weekRule -> {
            String weekString = weekRule.getWeek();
            for (int i = 0; i < weekString.length(); i++) {
                Integer weekInteger = Integer.valueOf(String.valueOf(weekString.charAt(i)));
                List list = map2.getOrDefault(weekInteger, new ArrayList<>());
                list.add(new PeriodOfTime(weekRule.getTimeSlotStart(), weekRule.getTimeSlotLength()));
                map2.put(weekInteger, list);
            }
        });
        map2.forEach((week, list) -> {
            List<PeriodOfTime> periodOfTimes = list;
            if (list.size() == 0 || list.size() == 1){
                //什么也不做
            }else {
                for (int i = list.size() - 1; i > 0; i--) {
                    LocalTime xStart = list.get(i).getTimeSlotStart();
                    LocalTime xEnd = list.get(i).getTimeSlotStart()
                            .plusHours((int) (list.get(i).getTimeSlotLength()/1))
                            .plusMinutes((long) ((list.get(i).getTimeSlotLength()%1)* 60));
                    LocalTime yStart = list.get(i-1).getTimeSlotStart();
                    LocalTime yEnd = list.get(i-1).getTimeSlotStart()
                            .plusHours((int) (list.get(i-1).getTimeSlotLength()/1))
                            .plusMinutes((long) ((list.get(i-1).getTimeSlotLength()%1)* 60));
                    if (xEnd.equals(yStart)){
                        PeriodOfTime z = new PeriodOfTime();
                        z.setTimeSlotStart(xStart);
                        z.setTimeSlotLength(list.get(i).getTimeSlotLength() + list.get(i-1).getTimeSlotLength());
                        //需要干点啥
                        periodOfTimes.remove(i);
                        periodOfTimes.remove(i-1);
                        periodOfTimes.add(z);
                    }else if (yEnd.equals(xStart)){
                        PeriodOfTime z = new PeriodOfTime();
                        z.setTimeSlotStart(yStart);
                        z.setTimeSlotLength(list.get(i).getTimeSlotLength() + list.get(i-1).getTimeSlotLength());
                        //需要干点啥
                        periodOfTimes.remove(i);
                        periodOfTimes.remove(i-1);
                        periodOfTimes.add(z);
                    }else {
                        //没检测到相邻的，啥也不干
                    }
                }
            }
            map22.put(week, periodOfTimes);
        });
        return map22;
    }

    private Map<String, List<PeriodOfTime>> getMap33(Map<Object, List<EmployeesCalendar>> calendarMap){
        Map<String, List<PeriodOfTime>> map3 = new HashMap<>();
        Map<String, List<PeriodOfTime>> map33 = new HashMap<>();
        calendarMap.get(null).forEach(x -> {
            List list = map3.getOrDefault(null, new ArrayList<>());
            list.add(new PeriodOfTime(x.getTimeSlotStart(), x.getTimeSlotLength()));
            map3.put(null, list);
        });
        map3.forEach((string, list) -> {
            List<PeriodOfTime> periodOfTimes = list;
            if (list.size() == 0 || list.size() == 1){
                //什么也不做
            }else {
                for (int i = list.size() - 1; i > 0; i--) {
                    LocalTime xStart = list.get(i).getTimeSlotStart();
                    LocalTime xEnd = list.get(i).getTimeSlotStart()
                            .plusHours((int) (list.get(i).getTimeSlotLength()/1))
                            .plusMinutes((long) ((list.get(i).getTimeSlotLength()%1)* 60));
                    LocalTime yStart = list.get(i-1).getTimeSlotStart();
                    LocalTime yEnd = list.get(i-1).getTimeSlotStart()
                            .plusHours((int) (list.get(i-1).getTimeSlotLength()/1))
                            .plusMinutes((long) ((list.get(i-1).getTimeSlotLength()%1)* 60));
                    if (xEnd.equals(yStart)){
                        PeriodOfTime z = new PeriodOfTime();
                        z.setTimeSlotStart(xStart);
                        z.setTimeSlotLength(list.get(i).getTimeSlotLength() + list.get(i-1).getTimeSlotLength());
                        //需要干点啥
                        periodOfTimes.remove(i);
                        periodOfTimes.remove(i-1);
                        periodOfTimes.add(z);
                    }else if (yEnd.equals(xStart)){
                        PeriodOfTime z = new PeriodOfTime();
                        z.setTimeSlotStart(yStart);
                        z.setTimeSlotLength(list.get(i).getTimeSlotLength() + list.get(i-1).getTimeSlotLength());
                        //需要干点啥
                        periodOfTimes.remove(i);
                        periodOfTimes.remove(i-1);
                        periodOfTimes.add(z);
                    }else {
                        //没检测到相邻的，啥也不干
                    }
                }
            }
            map33.put(string, periodOfTimes);
        });
        return map33;
    }

    /**
     * 18:00:00 代表18:00:00 - 18:30:00 的时间段
     *
     */
    public List<TimeAndPrice> periodSplittingA(List<TimeSlotDTO> slots){
        List<TimeAndPrice> res = new ArrayList<>();
        slots.forEach(slot -> {
            LocalTime time = slot.getTimeSlotStart();
            Float length = slot.getTimeSlotLength();
            Float total = length / 0.5f;
            for (Float i = 0f; i < total; i++) {
                TimeAndPrice timeAndPrice = new TimeAndPrice(time, slot.getJobAndPriceList());
                res.add(timeAndPrice);
                time = time.plusMinutes(30);
            }
        });
        return res;
    }

    public List<LocalTime> periodSplittingB(List<TimeSlot> slots){
        List<LocalTime> res = new ArrayList<>();
        slots.forEach(slot -> {
            LocalTime time = slot.getTimeSlotStart();
            Float length = slot.getTimeSlotLength();
            Float total = length / 0.5f;
            for (Float i = 0f; i < total; i++) {
                res.add(time);
                time = time.plusMinutes(30);
            }
        });
        return res;
    }

    private Map<LocalDate, List<TimeSlot>> periodMerging(Map<LocalDate, List<LocalTime>> periods){
        Map<LocalDate, List<TimeSlot>> res = new HashMap<>();

        for (Map.Entry<LocalDate, List<LocalTime>> period: periods.entrySet()) {
            List<TimeSlot> timeSlots = new ArrayList<>();
            AtomicReference<LocalTime> start = new AtomicReference<>(LocalTime.of(0, 0));
            AtomicReference<LocalTime> last = new AtomicReference<>(LocalTime.of(0, 0));

            AtomicReference<Float> length = new AtomicReference<>(0f);
            List<LocalTime> times = period.getValue();
            if (times.size() == 0){
                res.put(period.getKey(), timeSlots);
            }else if (times.size() == 1){
                TimeSlot timeSlot = new TimeSlot();
                timeSlot.setTimeSlotStart(times.get(0));
                timeSlot.setTimeSlotLength(0.5f);
                timeSlots.add(timeSlot);
            }else {
                LocalTime startTemp = times.get(0);
                Float lengthTemp = 0.5f;
                for (int i = 0; i < times.size() - 1; i++) {
                    if (times.get(i+1).equals(times.get(i).plusMinutes(30))){
                        lengthTemp += 0.5f;
                    }else {
                        TimeSlot timeSlot = new TimeSlot();
                        timeSlot.setTimeSlotStart(startTemp);
                        timeSlot.setTimeSlotLength(lengthTemp);
                        timeSlots.add(timeSlot);
                        startTemp = times.get(i+1);
                        lengthTemp = 0.5f;
                    }
                }

                TimeSlot timeSlot = new TimeSlot();
                timeSlot.setTimeSlotStart(startTemp);
                timeSlot.setTimeSlotLength(lengthTemp);
                timeSlots.add(timeSlot);

            }
            res.put(period.getKey(), timeSlots);
        }
        return res;
    }

    private List<JobAndPriceDetails> getService1(List<Integer> contendId,
                                         LocalDate start,
                                         LocalDate end,
                                         Map<LocalDate, List<TimeSlotDTO>> calendar,
                                         List<LocalTime> requireTime,
                                         Float totalTimeRequired){
        List<JobAndPriceDetails> service1 = new ArrayList<>();
        contendId.forEach(jobId -> {
            Attendance attendance = new Attendance(jobId, new Float(0), new BigDecimal(0));
            Map<LocalDate, List<LocalTime>> noAttendanceDetails = new HashMap<>(); //不能出勤的详细时间收集
            ExecutorService ex = Executors.newCachedThreadPool();
            for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)){
                LocalDate finalDate = date;
                ex.submit(new Runnable() {
                    @Override
                    public void run() {
                        judgeTheDay1(calendar, finalDate, requireTime, jobId, attendance, noAttendanceDetails);//判断date日期的出勤情况
                    }
                });
            }
            /***
             * shutdown调用后，不可以再submit新的task，已经submit的将继续执行。
             *
             * shutdownNow试图停止当前正执行的task，并返回尚未执行的task的list
             *
             * awaitTermination调用后等待线程，代线程执行完毕
             */
            ex.shutdown();
            try {
                ex.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Float attendanceValue = attendance.getEnableTotalHourly() / totalTimeRequired;
            if (attendanceValue >= CommonConstants.CONTRACT_COMPATIBILITY){
                //达到出勤率标准
                //添加到结果域
                JobAndPriceDetails jobAndPriceDetails = new JobAndPriceDetails(jobId, attendance.getTotalPrice(), attendanceValue, this.periodMerging(noAttendanceDetails));
                service1.add(jobAndPriceDetails);
            }else {
                //未达到标准
            }
        });
        return service1;
    }

    private void judgeTheDay1(Map<LocalDate, List<TimeSlotDTO>> calendar,
                             LocalDate date,
                             List<LocalTime> requireTime,
                             Integer jobId,
                             Attendance attendance,
                             Map<LocalDate, List<LocalTime>> noAttendanceDetails){
        List<TimeSlotDTO> todayTimeSlotDTO = calendar.get(date);
        List<LocalTime> noAttendanceTime = new ArrayList<>();
        List<TimeAndPrice> todayTime = this.periodSplittingA(todayTimeSlotDTO);
        requireTime.forEach(time -> {
            AtomicReference<Boolean> canBeOnDuty = new AtomicReference<>(false);
            //判断这半个小时能否出勤
            todayTime.forEach(today -> {
                if (today.getTime().equals(time)){
                    today.getJobAndPriceList().forEach(jobAndPriceDTO -> {
                        if (jobAndPriceDTO.getJobId().equals(jobId)){
                            //这个班可以出席
                            canBeOnDuty.set(true);
                            attendance.halfAnHourMore();
                            BigDecimal halfAnHourWage = new BigDecimal(jobAndPriceDTO.getPrice()).divide(new BigDecimal(2));
                            attendance.increaseTheTotalPrice(halfAnHourWage);
                        }
                    });
                }
            });

            if (canBeOnDuty.get()) {
                //能出勤
                //什么也不做
            }else {
                //不能出勤
                noAttendanceTime.add(time);
            }
        });
        if (noAttendanceTime.size() != 0){
            noAttendanceDetails.put(date, noAttendanceTime);
        }
    }

    private List<ContractAndPriceDetails> getService2(List<EmployeesContract> employeesContractList,
                                              LocalDate start,
                                              LocalDate end,
                                              List<LocalTime> requireTime,
                                              Float totalTimeRequired,
                                              DateSlot dateSlot,
                                              String toCode,
                                              List<Integer> contendId){
        List<ContractAndPriceDetails> service2 = new ArrayList<>();
        employeesContractList.forEach(employeesContract -> {
            /* 筛选工作内容不满足要求的包工服务 */
            String jobStr = employeesContract.getJobs();
            long num = (long) contendId.stream().filter(id -> jobStr.contains(id.toString())).count();
            if (num == 0){
                return;
            }

            Map<LocalDate, List<TimeSlot>> calendarContractFreeTime = employeesContractService.getFreeTimeByContractId(dateSlot, employeesContract.getId());
            long daysTotal = end.toEpochDay() - start.toEpochDay();
            float wage = daysTotal * employeesContract.getDayWage();
            Attendance attendance = new Attendance(employeesContract.getId(), new Float(0), new BigDecimal(0));
            Map<LocalDate, List<LocalTime>> noAttendanceDetails = new HashMap<>(); //不能出勤的详细时间收集
            ExecutorService ex = Executors.newCachedThreadPool();
            for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)){
                LocalDate finalDate = date;
                ex.submit(new Runnable() {
                    @Override
                    public void run() {
                        judgeTheDay2(calendarContractFreeTime, finalDate, requireTime, attendance, noAttendanceDetails);//判断date日期的出勤情况
                    }
                });
            }
            ex.shutdown();
            try {
                ex.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Float attendanceValue = attendance.getEnableTotalHourly() / totalTimeRequired;
            if (attendanceValue >= CommonConstants.CONTRACT_COMPATIBILITY){
                //达到出勤率标准
                BigDecimal totalWage;
                if (employeesContract.getCode().equals(toCode)){
                    totalWage = new BigDecimal(wage);
                }else {
                    totalWage = currencyService.exchangeRateToBigDecimalAfterOptimization(employeesContract.getCode(), toCode, new BigDecimal(wage));
                }
                attendance.setTotalPrice(totalWage);
                //添加到结果域
                ContractAndPriceDetails contractAndPriceDetails = new ContractAndPriceDetails(employeesContract, attendance.getTotalPrice(), attendanceValue, this.periodMerging(noAttendanceDetails));
                service2.add(contractAndPriceDetails);
            }else {
                //未达到标准
            }
        });
        return service2;
    }

    private void judgeTheDay2(Map<LocalDate, List<TimeSlot>> calendarContractFreeTime,
                              LocalDate date,
                              List<LocalTime> requireTime,
                              Attendance attendance,
                              Map<LocalDate, List<LocalTime>> noAttendanceDetails){
        List<TimeSlot> todayTimeSlot = calendarContractFreeTime.get(date);
        List<LocalTime> noAttendanceTime = new ArrayList<>();
        List<LocalTime> todayTime = this.periodSplittingB(todayTimeSlot);
        requireTime.forEach(time -> {
            AtomicReference<Boolean> canBeOnDuty = new AtomicReference<>(false);
            //判断这半个小时能否出勤
            todayTime.forEach(today -> {
                if (today.equals(time)){
                    //这个班可以出席
                    canBeOnDuty.set(true);
                    attendance.halfAnHourMore();
                }
            });

            if (canBeOnDuty.get()) {
                //能出勤
            }else {
                //不能出勤
                noAttendanceTime.add(time);
            }
        });
        if (noAttendanceTime.size() != 0){
            noAttendanceDetails.put(date, noAttendanceTime);
        }
    }

    List<Integer> indexIdHandleContendId(Integer indexId){
        QueryWrapper qw0 = new QueryWrapper();
        qw0.eq("index_id", indexId);
        List<SysIndexContent> sysIndexContentList = sysIndexContentService.list(qw0);
        List<Integer> contendId =  sysIndexContentList.stream().map(x -> {
            return x.getContentId();
        }).collect(Collectors.toList());
        return contendId;
    }

    List<Integer> getPromoteCompanyIds(){
        List<Integer> promoteCompanyIds = new ArrayList<>();
        QueryWrapper qw2 = new QueryWrapper();
        qw2.select("company_id").gt("end_time", LocalDateTime.now());
        promoteCompanyIds = companyPromotionService.listObjs(qw2);
        return promoteCompanyIds;
    }

    List<Integer> getPromoteEmployeeIds(){
        List<Integer> promoteEmployeeIds = new ArrayList<>();
        QueryWrapper qw3 = new QueryWrapper();
        qw3.select("employees_id").gt("end_time", LocalDateTime.now());
        promoteEmployeeIds = employeesPromotionService.listObjs(qw3);
        return promoteEmployeeIds;
    }

    Map<Integer, List<Integer>> getEmployeesSearchPool(){
        Map<Integer, List<Integer>> employeesSearchPool = new HashMap<>();
        QueryWrapper qw1 = new QueryWrapper();
        qw1.select("employees_id").groupBy("employees_id");
        List<Integer> employeeIdsFromCalendar = employeesCalendarService.listObjs(qw1);
        List<Integer> employeeIdsFromContract = employeesContractService.listObjs(qw1);
        List<Integer> list1 = new ArrayList<>(employeeIdsFromCalendar);    //能做钟点工的保洁员
        List<Integer> list2 = new ArrayList<>(employeeIdsFromContract);    //能做包工的保洁员
        List<Integer> list3 = this.getUnion(list1, list2);                 //能做任一工种的保洁员
        employeesSearchPool.put(1, list1);
        employeesSearchPool.put(2, list2);
        employeesSearchPool.put(3, list3);
        return employeesSearchPool;
    }

    public void existEmployeesHandle(EmployeesHandleVo vo,
                                     Map<Integer, List<IndexQueryResultEmployees>> matchingCompanyIdsAndEmployeesDetails,
                                     List<IndexQueryResultEmployees> matchingEmployeesDetails, String toCode, Map<String, String> weight, BigDecimal lowPrice, BigDecimal highPrice){
        /** indexQueryResultEmployees 保洁员返回信息 */
        IndexQueryResultEmployees indexQueryResultEmployees = new IndexQueryResultEmployees();

        /** price 保洁员价格准备 */
        AtomicReference<BigDecimal> minPrice  = new AtomicReference<>(lowPrice);

        /** existEmployee 当前保洁员信息 */
        EmployeesDetails existEmployee = employeesDetailsService.getById(vo.getExistEmployeesId());


        /** instance: 距离准备、筛选 */
        String str = CommonUtils.getInstanceByPoint(existEmployee.getLat(), existEmployee.getLng(), vo.getAddressDetailsDTO().getLat().toString(), vo.getAddressDetailsDTO().getLng().toString());
        Double instance = new Double(str);
        Double scopeOfOrder = new Double(existEmployee.getScopeOfOrder());//默认3000米接单范围
        indexQueryResultEmployees.setInstance(instance);

        /** calendar：该钟点工闲置时间准备 */
        DateSlot dateSlot = new DateSlot(vo.getStart(), vo.getEnd());
        Map<LocalDate, List<TimeSlotDTO>> calendar = employeesCalendarService.getFreeTimeByDateSlot(dateSlot, vo.getExistEmployeesId(), toCode);

        /** employeesContractList: 该保洁员的包工准备 */
        QueryWrapper contractQw = new QueryWrapper();
        contractQw.eq("employees_id", existEmployee.getId());
        List<EmployeesContract> employeesContractList = employeesContractService.list(contractQw);

        /** totalTimeRequired 客户需求总时长准备 */
        AtomicReference<Float> dayTimeRequired = new AtomicReference<>(0f);
        vo.getTimeSlots().forEach(timeSlot -> {
            dayTimeRequired.set(dayTimeRequired.get() + timeSlot.getTimeSlotLength());
        });
        Long daysCount = vo.getEnd().toEpochDay() - vo.getStart().toEpochDay() + 1;
        Float totalTimeRequired = daysCount * dayTimeRequired.get();

        /** requireTime 客户需求时段加工准备 */
        List<LocalTime> requireTime = this.periodSplittingB(vo.getTimeSlots());

        /** score: 评分(星级)准备 */
        Float score = existEmployee.getStarRating();
        indexQueryResultEmployees.setScore(score);

        /** extensionIsOk: 是否推广准备 */
        Boolean extensionIsOk = false;
        QueryWrapper qw2 = new QueryWrapper();
        qw2.gt("end_time", LocalDateTime.now());
        qw2.eq("employees_id", existEmployee.getId());
        EmployeesPromotion res = employeesPromotionService.getOne(qw2);
        if (CommonUtils.isNotEmpty(res)){
            extensionIsOk = true;
        }

        /** areaIsOk: 地區是否匹配 */
        Boolean areaIsOk = sysAddressAreaService.matchingArea(vo.getAddressDetailsDTO().getAddress(), existEmployee.getWorkingArea());

        EmployeesScope employeesScope = new EmployeesScope(scopeOfOrder, instance, areaIsOk, extensionIsOk, null, null, lowPrice, highPrice, score, weight, existEmployee.getNumberOfOrders());

        /**
         * 员工筛选 type=1   1、只有钟点工匹配ok  2、只有包工匹配ok  3、钟点工和包工都匹配ok
         */
        if (vo.getType() == 1){
            List<JobAndPriceDetails> service1 = this.getService1(vo.getContendId(), vo.getStart(), vo.getEnd(), calendar, requireTime, totalTimeRequired);
            if (service1.size() != 0){
                indexQueryResultEmployees.setEmployeesType(vo.getType());
            }else {
                indexQueryResultEmployees.setEmployeesType(4);
            }
            indexQueryResultEmployees.setEmployeesDetails(employeesDetailsService.getById(vo.getExistEmployeesId()));
            indexQueryResultEmployees.setService2(new ArrayList<>());
            indexQueryResultEmployees.setService1(service1);
            employeesScope.setService1(service1);
            employeesScope.setService2(new ArrayList<>());
        }else if (vo.getType() == 2){
            List<ContractAndPriceDetails> service2 = this.getService2(employeesContractList, vo.getStart(), vo.getEnd(), requireTime, totalTimeRequired, dateSlot, toCode, vo.getContendId());
            if (service2.size() != 0){
                indexQueryResultEmployees.setEmployeesType(vo.getType());
            }else {
                indexQueryResultEmployees.setEmployeesType(4);
            }
            indexQueryResultEmployees.setEmployeesDetails(employeesDetailsService.getById(vo.getExistEmployeesId()));
            indexQueryResultEmployees.setService2(service2);
            indexQueryResultEmployees.setService1(new ArrayList<>());
            employeesScope.setService1(new ArrayList<>());
            employeesScope.setService2(service2);
        }else if (vo.getType() == 3){
            List<JobAndPriceDetails> service1 = this.getService1(vo.getContendId(), vo.getStart(), vo.getEnd(), calendar, requireTime, totalTimeRequired);
            List<ContractAndPriceDetails> service2 = this.getService2(employeesContractList, vo.getStart(), vo.getEnd(), requireTime, totalTimeRequired, dateSlot, toCode, vo.getContendId());
            if (service1.size() == 0 && service2.size() == 0){
                indexQueryResultEmployees.setEmployeesType(4);
            }else if (service1.size() != 0 && service2.size() == 0){
                indexQueryResultEmployees.setEmployeesType(1);
            }else if (service1.size() == 0 && service2.size() != 0){
                indexQueryResultEmployees.setEmployeesType(2);
            }else if (service1.size() != 0 && service2.size() != 0){
                indexQueryResultEmployees.setEmployeesType(3);
            }
            indexQueryResultEmployees.setEmployeesDetails(employeesDetailsService.getById(vo.getExistEmployeesId()));
            indexQueryResultEmployees.setService2(service2);
            indexQueryResultEmployees.setService1(service1);
            employeesScope.setService1(service1);
            employeesScope.setService2(service2);
        }
        indexQueryResultEmployees.setRecommendedScope(employeesScope.getScopeTotal());

        /** 最低价格计算 */
        if (CommonUtils.isNotEmpty(indexQueryResultEmployees.getService1()) && CommonUtils.isNotEmpty(indexQueryResultEmployees.getService2())){
            indexQueryResultEmployees.getService1().forEach(x-> {
                minPrice.set(this.getLowPrice(lowPrice, highPrice, minPrice.get(), x.getTotalPrice()));
            });
            indexQueryResultEmployees.getService2().forEach(x -> {
                minPrice.set(this.getLowPrice(lowPrice, highPrice, minPrice.get(), x.getTotalPrice()));
            });
        }else if (CommonUtils.isNotEmpty(indexQueryResultEmployees.getService1()) && CommonUtils.isEmpty(indexQueryResultEmployees.getService2())){
            indexQueryResultEmployees.getService1().forEach(x-> {
                minPrice.set(this.getLowPrice(lowPrice, highPrice, minPrice.get(), x.getTotalPrice()));
            });
        }else if (CommonUtils.isEmpty(indexQueryResultEmployees.getService1()) && CommonUtils.isNotEmpty(indexQueryResultEmployees.getService2())){
            indexQueryResultEmployees.getService2().forEach(x -> {
                minPrice.set(this.getLowPrice(lowPrice, highPrice, minPrice.get(), x.getTotalPrice()));
            });
        }

        indexQueryResultEmployees.setPrice(minPrice.get());

        /** companyId: 所属公司准备 */
        Integer companyId = existEmployee.getCompanyId();

        /** 都走到这一步了，这个员工就相当于能被搜索到了,那直接添加到结果集,等下做数据返回 */
        List<IndexQueryResultEmployees> indexQueryResultEmployeesList = matchingCompanyIdsAndEmployeesDetails.getOrDefault(companyId, new ArrayList<>());
        indexQueryResultEmployeesList.add(indexQueryResultEmployees);
        matchingCompanyIdsAndEmployeesDetails.put(companyId, indexQueryResultEmployeesList);
        matchingEmployeesDetails.add(indexQueryResultEmployees);
    }

    List<String> rationalityJudgmentTimeSlotList(List<TimeSlot> timeSlotVos){
        List<String> resCollections = new ArrayList<>();//不合理性结果收集
        SortListUtil<TimeSlot> sort = new SortListUtil<>();
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

    /***
     *
     * @param low    范围
     * @param high   范围
     * @param min    目前最小值
     * @param exist  当前比较值
     * @return
     */
    private BigDecimal getLowPrice(BigDecimal low, BigDecimal high, BigDecimal min, BigDecimal exist){
        if (exist.compareTo(high) == -1 || exist.compareTo(low) == 1){
            if (exist.compareTo(min) == -1){
                return exist;
            }else {
                return min;
            }
        }else {
            return low;
        }
    }

    @Test
    public void test() {
        List<TimeSlot> slots = new ArrayList<>();
        TimeSlot timeSlot1 = new TimeSlot();
        timeSlot1.setTimeSlotStart(LocalTime.of(9, 0));
        timeSlot1.setTimeSlotLength(new Float(0f));
        slots.add(timeSlot1);
        List<LocalTime> res = this.periodSplittingB(slots);

        Map<LocalDate, List<LocalTime>> periods = new HashMap<>();
        periods.put(LocalDate.of(2020,1,1), res);
        periods.put(LocalDate.of(2020,1,2), res);
        periods.put(LocalDate.of(2020,1,3), res);
        periods.put(LocalDate.of(2020,1,4), res);

        Map<LocalDate, List<TimeSlot>> resA = this.periodMerging(periods);
        System.out.println("ssss");
    }
}
