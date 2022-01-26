package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.*;
import com.housekeeping.admin.entity.*;
import com.housekeeping.admin.mapper.SysIndexMapper;
import com.housekeeping.admin.mapper.SysJobContendMapper;
import com.housekeeping.admin.pojo.QueryEmployeesInfo;
import com.housekeeping.admin.service.*;
import com.housekeeping.admin.vo.*;
import com.housekeeping.common.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
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
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private SysJobContendMapper sysJobContendMapper;
    @Resource
    private ISysJobNoteService sysJobNoteService;
    @Resource
    private SysContendPriceService sysContendPriceService;

    @Override
    public R add(SysIndexAddDto sysIndexAddDto) {
        SysIndex sysIndex = new SysIndex();
        sysIndex.setName(sysIndexAddDto.getName());
        sysIndex.setOrderValue(sysIndexAddDto.getOrderValue());
        sysIndex.setSelectedLogo(sysIndexAddDto.getSelectedLogo());
        sysIndex.setUncheckedLogo(sysIndexAddDto.getUncheckedLogo());
        sysIndex.setNewSelectedLogo(sysIndexAddDto.getSelectedLogo());
        sysIndex.setNewUncheckedLogo(sysIndexAddDto.getNewUncheckedLogo());
        StringBuilder priceSlot = new StringBuilder("");
        List<PriceSlotVo> priceSlotList = sysIndexAddDto.getPriceSlotList();
        for (int i = 0; i < priceSlotList.size(); i++) {
            if (i==0) priceSlot.append(priceSlotList.get(i).getLowPrice());
            else priceSlot.append(priceSlotList.get(i).getLowPrice().subtract(new BigDecimal(1)));
            priceSlot.append(" ");
        }

        sysIndex.setPriceSlot(new String(priceSlot).trim());
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
        sysIndex.setNewSelectedLogo(dto.getSelectedLogo());
        sysIndex.setNewUncheckedLogo(dto.getNewUncheckedLogo());
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
            sysIndexVo.setNewSelectedLogo(x.getNewSelectedLogo());
            sysIndexVo.setNewUncheckedLogo(x.getNewUncheckedLogo());
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
    public R tree() {
        List<SysIndex> sysIndexList = this.list();
        List<SysIndexVo> sysIndexVoList = sysIndexList.stream().map(x->{
            SysIndexVo sysIndexVo = new SysIndexVo();
            sysIndexVo.setId(x.getId());
            sysIndexVo.setName(x.getName());
            sysIndexVo.setOrderValue(x.getOrderValue());
            sysIndexVo.setSelectedLogo(x.getSelectedLogo());
            sysIndexVo.setUncheckedLogo(x.getUncheckedLogo());
            sysIndexVo.setNewSelectedLogo(x.getNewSelectedLogo());
            sysIndexVo.setNewUncheckedLogo(x.getNewUncheckedLogo());
            List<PriceSlotVo> priceSlotVoList = new ArrayList<>();
            String[] arr = x.getPriceSlot().split(" ");
            for (int i = 0; i < arr.length - 1; i++) {
                // i和i+1
                String lowPrice = arr[i];
                if (i!=0){
                    BigDecimal low = new BigDecimal(arr[i]).add(new BigDecimal(1));
                    lowPrice = low.toString();
                }
                String highPrice = arr[i+1];
                PriceSlotVo priceSlot = new PriceSlotVo(
                        new BigDecimal(lowPrice),
                        new BigDecimal(highPrice)
                );
                priceSlotVoList.add(priceSlot);
            }
            PriceSlotVo priceSlot = new PriceSlotVo(
                    new BigDecimal(arr[arr.length-1]).add(new BigDecimal(1)),
                    null
            );
            priceSlotVoList.add(priceSlot);
            sysIndexVo.setPriceSlotList(priceSlotVoList);
            List<JobContendVO> sysJobContends = new ArrayList<>();
            QueryWrapper qw = new QueryWrapper();
            qw.eq("index_id", x.getId());
            List<SysIndexContent> sysIndexContents = sysIndexContentService.list(qw);
            sysIndexContents.forEach(sysIndexContent -> {
                SysJobContend sysJobContend = sysJobContendService.getById(sysIndexContent.getContentId());
                JobContendVO jobContendVO = new JobContendVO();
                jobContendVO.setId(sysJobContend.getId());
                jobContendVO.setContend(sysJobContend.getContend());
                jobContendVO.setServicePlace(sysJobContend.getServicePlace());
                jobContendVO.setArea(sysJobContend.getArea());
                jobContendVO.setHome(sysJobContend.getHome());

                QueryWrapper<SysContendPrice> qw5 = new QueryWrapper<>();
                qw5.eq("contend_id",sysJobContend.getId());
                SysContendPrice one = sysContendPriceService.getOne(qw5);
                jobContendVO.setStatus(one.getStatus());
                jobContendVO.setPersonalPrice(one.getPersonalPrice());
                jobContendVO.setHour(one.getHour());
                jobContendVO.setFlat(one.getFlat());
                jobContendVO.setCompanyPrice(one.getCompanyPrice());

                List<SysJobNote> sysJobNotes = new ArrayList<>();
                List<Integer> noteIds = sysJobContendMapper.getAll(sysJobContend.getId());
                for (int i = 0; i < noteIds.size(); i++) {
                    SysJobNote byId = sysJobNoteService.getById(noteIds.get(i));
                    sysJobNotes.add(byId);
                }
                jobContendVO.setNotes(sysJobNotes);
                sysJobContends.add(jobContendVO);
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
            /** certified:員工認證準備 */
            Integer certified;
            //不属于公司就是个体户 1个体户 2工作室 3公司
            if(x.getCompanyId()==null){
                certified = 1;
            }else {
                CompanyDetails cd = companyDetailsService.getById(x.getCompanyId());
                Boolean isCertified = cd.getIsValidate();
                if(isCertified==false){
                    certified = 2;
                }else {
                    certified = 3;
                }
            }
            List<Integer> jobIds = CommonUtils.stringToList(x.getPresetJobIds());
            List<SysJobContend> jobs = new ArrayList<>();
            if (!jobIds.isEmpty()) jobs = sysJobContendService.listByIds(jobIds);
            if (dto.getGetGPSSuccess()){
                String str = CommonUtils.getInstanceByPoint(x.getLat(), x.getLng(), dto.getLat().toString(), dto.getLng().toString());
                Double instance = new Double(str);
                EmployeesInstanceDTO employeesInstanceDTO = new EmployeesInstanceDTO(x, instance, certified, jobs);
                return employeesInstanceDTO;
            }else {
                EmployeesInstanceDTO employeesInstanceDTO = new EmployeesInstanceDTO(x, new Double(0), certified, jobs);
                return employeesInstanceDTO;
            }
        }).collect(Collectors.toList());
        if (dto.getGetGPSSuccess()){
            SortListUtil<EmployeesInstanceDTO> sort = new SortListUtil<>();
            sort.SortByDouble(dos, "getInstance", null);
        }

        /* 生成异步任务，将dos和dos的公司保存到redis */
        asyncService.setRedisDos(dos);

        /*List<Integer> companyIds = new ArrayList<>();
        for (int i = 0; i < defaultRecommendationCompanyInteger*5; i++) {
            EmployeesInstanceDTO emp = dos.get(i);
            Integer comId = emp.getEmployeesDetails().getCompanyId();
            if (!companyIds.contains(comId)){
                companyIds.add(comId);
            }
            if (companyIds.size() >= defaultRecommendationCompanyInteger) break;
        }*/

        QueryWrapper<CompanyDetails> qw = new QueryWrapper<>();
        qw.eq("is_validate",1);
        qw.last("limit "+defaultRecommendationCompanyInteger);
        List<CompanyDetails> companyDetails = companyDetailsService.list();
        List<CompanyDetails> cds = companyDetails.stream().filter(cd -> {
            if (cd.getIsValidate()) return true;
            else return false;
        }).collect(Collectors.toList());
        dos = new ArrayList<>(dos.subList(0, defaultRecommendationEmployeesInteger));
        res.put("employees", dos);
        res.put("company", cds);
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
            /** certified:員工認證準備 */
            List<Integer> jobIds = CommonUtils.stringToList(employeesDetails.getPresetJobIds());
            List<SysJobContend> jobs = new ArrayList<>();
            if (!jobIds.isEmpty()) jobs = sysJobContendService.listByIds(jobIds);

            Integer certified;
            //不属于公司就是个体户 1个体户 2工作室 3公司
            if(employeesDetails.getCompanyId()==null||employeesDetails.getCompanyId().equals("")){
                certified = 1;
            }else {
                CompanyDetails cd = companyDetailsService.getById(employeesDetails.getCompanyId());
                Boolean isCertified = cd.getIsValidate();
                if(isCertified==false){
                    certified = 2;
                }else {
                    certified = 3;
                }
            }

            EmployeesInstanceDTO employeesInstanceDTO = new EmployeesInstanceDTO(employeesDetails, addrDTO.getInstance(), certified, jobs);
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
            /** certified:員工認證準備 */
            Integer certified;
            //不属于公司就是个体户 1个体户 2工作室 3公司
            CompanyDetails cd = companyDetailsService.getById(employeesDetails.getCompanyId());
            if(cd.getIsPersonal()==true){
                certified = 1;
            }else {
                Boolean isCertified = cd.getIsValidate();
                if(isCertified==false){
                    certified = 2;
                }else {
                    certified = 3;
                }
            }

            List<Integer> jobIds = CommonUtils.stringToList(employeesDetails.getPresetJobIds());
            List<SysJobContend> jobs = new ArrayList<>();
            if (!jobIds.isEmpty()) jobs = sysJobContendService.listByIds(jobIds);
            EmployeesInstanceDTO employeesInstanceDTO = new EmployeesInstanceDTO(employeesDetails, addrDTO.getInstance(), certified, jobs);
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

    @Override
    public R query(QueryParamsDTO dto) {
        QueryWrapper qw1 = new QueryWrapper();
        qw1.like("name", dto.getName());
        QueryWrapper qw2 = new QueryWrapper();
        qw2.like("no_certified_company", dto.getName());
        List<EmployeesDetails> eds = employeesDetailsService.list(qw1);
        List<CompanyDetails> companyDetailsList = companyDetailsService.list(qw2);

        List<CompanyDetails> cds = companyDetailsList.stream().filter(cd -> {
            if (cd.getIsPersonal() == false) return true;
            else return false;
        }).collect(Collectors.toList());

        List<QueryEmployeesInfo> qes = new ArrayList<>();
        eds.forEach(ed -> {
            String instance = CommonUtils.getInstanceByPoint(
                    dto.getAddress().getLat().toString(),
                    dto.getAddress().getLng().toString(),
                    ed.getLat(),
                    ed.getLng());
            BigDecimal insBig = new BigDecimal(instance).setScale(1, BigDecimal.ROUND_DOWN);
            List<Integer> jobIds = CommonUtils.stringToList(ed.getPresetJobIds());
            List<SysJobContend> jobs = new ArrayList<>();
            if (!jobIds.isEmpty()){
                jobs = sysJobContendService.listByIds(jobIds);
            }
            QueryEmployeesInfo qei = new QueryEmployeesInfo(ed, jobs, insBig.toString());
            /** certified:員工認證準備 */
            Integer certified;
            //不属于公司就是个体户 1个体户 2工作室 3公司
            CompanyDetails cd = companyDetailsService.getById(ed.getCompanyId());
            if(cd.getIsPersonal()==true){
                certified = 1;
            }else {
                Boolean isCertified = cd.getIsValidate();
                if(isCertified==false){
                    certified = 2;
                }else {
                    certified = 3;
                }
            }
            qei.setCertified(certified);
            qes.add(qei);
        });

        Map<String, List> res = new HashMap<>();
        res.put("eds", qes);
        res.put("cds", cds);
        return R.ok(res);
    }

    @Override
    public synchronized R add2(SysIndexAdd2DTO sysIndexAddDto) {
        SysIndex index = (SysIndex) CommonUtils.getMaxId("sys_index", this);

        SysIndex sysIndex = new SysIndex();
        sysIndex.setName(sysIndexAddDto.getName());
        sysIndex.setOrderValue(index.getOrderValue() + 1000);
        sysIndex.setSelectedLogo(sysIndexAddDto.getSelectedLogo());
        sysIndex.setUncheckedLogo(sysIndexAddDto.getUncheckedLogo());
        sysIndex.setNewSelectedLogo(sysIndexAddDto.getSelectedLogo());
        sysIndex.setNewUncheckedLogo(sysIndexAddDto.getNewUncheckedLogo());
        StringBuilder priceSlot = new StringBuilder("");
        List<PriceSlotVo> priceSlotList = sysIndexAddDto.getPriceSlotList();
        for (int i = 0; i < priceSlotList.size(); i++) {
            if(i==0){
                priceSlot.append(priceSlotList.get(i).getLowPrice()).append(" ").append(priceSlotList.get(i).getHighPrice());
            }
            else {
                priceSlot.append(" ");
                priceSlot.append(priceSlotList.get(i).getHighPrice());
            }
        }

        sysIndex.setPriceSlot(new String(priceSlot).trim());
        Integer maxIndexId = 0;
        synchronized (this) {
            this.save(sysIndex);
            maxIndexId = ((SysIndex) CommonUtils.getMaxId("sys_index", this)).getId();
        }

        Integer finalMaxIndexId = maxIndexId;
        List<JobDTO> jobs = sysIndexAddDto.getJobs();
        if (CollectionUtils.isNotEmpty(jobs)) {
            for (int i = 0; i < jobs.size(); i++) {
                {
                    JobDTO x = jobs.get(i);
                    Integer id = x.getId();
                    SysJobContend sysJobContend = new SysJobContend();
                    if (id == null) {
                        String contend = x.getContend();
                        if (contend!=null) {
                            SysJobContend sysJobContent = (SysJobContend) CommonUtils.getMaxId("sys_job_contend", sysJobContendService);
                            sysJobContend.setId(sysJobContent.getId() + 1);
                            sysJobContend.setContend(x.getContend());
                            sysJobContend.setServicePlace(x.getServicePlace());
                            sysJobContend.setArea(x.getArea());
                            sysJobContend.setHome(x.getHome());
                            sysJobContendService.save(sysJobContend);

                            SysJobContend maxId = (SysJobContend) CommonUtils.getMaxId("sys_job_contend", sysJobContendService);

                            SysContendPrice sysContendPrice = new SysContendPrice();
                            sysContendPrice.setContendId(maxId.getId());
                            sysContendPrice.setCompanyPrice(x.getCompanyPrice());
                            sysContendPrice.setFlat(x.getFlat());
                            sysContendPrice.setHour(x.getHour());
                            sysContendPrice.setPersonalPrice(x.getPersonalPrice());
                            sysContendPrice.setStatus(x.getStatus());
                            sysContendPriceService.save(sysContendPrice);

                            SysIndexContent sysIndexContent = new SysIndexContent();
                            sysIndexContent.setIndexId(finalMaxIndexId);
                            sysIndexContent.setContentId(maxId.getId());
                            sysIndexContentService.save(sysIndexContent);

                            List<SysJobNote> noteIds = x.getNoteIds();
                            for (int i1 = 0; i1 < noteIds.size(); i1++) {
                                SysJobNote sysJobNote = noteIds.get(i1);
                                SysJobNote sysJobNote1 = new SysJobNote();
                                if (sysJobNote.getId() == null) {
                                    if (sysJobNote.getNote()!=null && !sysJobNote.getNote().equals("")) {
                                        sysJobNote1.setNote(sysJobNote.getNote());
                                        sysJobNoteService.save(sysJobNote1);
                                        SysJobNote note = (SysJobNote) CommonUtils.getMaxId("sys_job_note", sysJobNoteService);
                                        sysJobContendMapper.insertNote(maxId.getId(), note.getId());
                                    }
                                } else {
                                    sysJobNote1.setId(sysJobNote.getId());
                                    sysJobNote1.setNote(sysJobNote.getNote());
                                    sysJobNoteService.updateById(sysJobNote1);
                                    sysJobContendMapper.insertNote(maxId.getId(), sysJobNote.getId());
                                }
                            }
                        }
                    } else {
                        sysJobContend.setId(id);
                        sysJobContend.setContend(x.getContend());
                        sysJobContend.setServicePlace(x.getServicePlace());
                        sysJobContend.setArea(x.getArea());
                        sysJobContend.setHome(x.getHome());
                        sysJobContendService.updateById(sysJobContend);

                        QueryWrapper<SysContendPrice> qw5 = new QueryWrapper<>();
                        qw5.eq("contend_id",id);
                        SysContendPrice one = sysContendPriceService.getOne(qw5);
                        one.setStatus(x.getStatus());
                        one.setPersonalPrice(x.getPersonalPrice());
                        one.setHour(x.getHour());
                        one.setFlat(x.getFlat());
                        one.setCompanyPrice(x.getCompanyPrice());
                        sysContendPriceService.updateById(one);

                        List<SysJobNote> noteIds = x.getNoteIds();
                        for (int y = 0; y < noteIds.size(); y++) {
                            SysJobNote sysJobNote = noteIds.get(y);
                            SysJobNote sysJobNote1 = new SysJobNote();
                            if (sysJobNote.getId() == null) {
                                if (!sysJobNote.getNote().equals(null) && !sysJobNote.getNote().equals("")) {
                                    sysJobNote1.setNote(sysJobNote.getNote());
                                    sysJobNoteService.save(sysJobNote1);
                                    SysJobNote note = (SysJobNote) CommonUtils.getMaxId("sys_job_note", sysJobNoteService);
                                    sysJobContendMapper.insertNote(id, note.getId());
                                }
                            } else {
                                sysJobNote1.setId(sysJobNote.getId());
                                sysJobNote1.setNote(sysJobNote.getNote());
                                sysJobNoteService.updateById(sysJobNote1);
                                sysJobContendMapper.insertNote(id, sysJobNote.getId());
                            }
                        }
                    }
                }
            }
        }
        return R.ok("添加成功");
    }

    @Override
    public R update2(SysIndexUpdate2DTO dto) {

        //更新一级分类信息
        SysIndex sysIndex = new SysIndex();
        sysIndex.setId(dto.getId());
        sysIndex.setName(dto.getName());
        sysIndex.setOrderValue(dto.getOrderValue());
        sysIndex.setSelectedLogo(dto.getSelectedLogo());
        sysIndex.setUncheckedLogo(dto.getUncheckedLogo());
        sysIndex.setNewSelectedLogo(dto.getSelectedLogo());
        sysIndex.setNewUncheckedLogo(dto.getNewUncheckedLogo());
        StringBuilder priceSlot = new StringBuilder("");
        List<PriceSlotVo> priceSlotList = dto.getPriceSlotList();
        for (int i = 0; i < priceSlotList.size(); i++) {
            if(i==0){
                priceSlot.append(priceSlotList.get(i).getLowPrice()).append(" ").append(priceSlotList.get(i).getHighPrice());
            }
            else {
                    priceSlot.append(" ");
                    priceSlot.append(priceSlotList.get(i).getHighPrice());
            }
        }

        sysIndex.setPriceSlot(new String(priceSlot).trim());
        this.updateById(sysIndex);

        //删除关联二级分类
        QueryWrapper deleteQw = new QueryWrapper();
        deleteQw.eq("index_id", dto.getId());
        sysIndexContentService.remove(deleteQw);

        Integer finalMaxIndexId = dto.getId();
        List<JobDTO> jobs = dto.getJobs();
            jobs.forEach(x -> {
                        Integer id = x.getId();
                        SysJobContend sysJobContend = new SysJobContend();
                        if (id == null) {
                            if (x.getContend()!=null) {
                                SysJobContend sysJobContent = (SysJobContend) CommonUtils.getMaxId("sys_job_contend", sysJobContendService);
                                sysJobContend.setId(sysJobContent.getId() + 1);
                                sysJobContend.setContend(x.getContend());
                                sysJobContend.setServicePlace(x.getServicePlace());
                                sysJobContend.setArea(x.getArea());
                                sysJobContend.setHome(x.getHome());
                                sysJobContendService.save(sysJobContend);

                                SysJobContend maxId = (SysJobContend) CommonUtils.getMaxId("sys_job_contend", sysJobContendService);

                                SysContendPrice sysContendPrice = new SysContendPrice();
                                sysContendPrice.setContendId(maxId.getId());
                                sysContendPrice.setCompanyPrice(x.getCompanyPrice());
                                sysContendPrice.setFlat(x.getFlat());
                                sysContendPrice.setHour(x.getHour());
                                sysContendPrice.setPersonalPrice(x.getPersonalPrice());
                                sysContendPrice.setStatus(x.getStatus());
                                sysContendPriceService.save(sysContendPrice);

                                SysIndexContent sysIndexContent = new SysIndexContent();
                                sysIndexContent.setIndexId(finalMaxIndexId);
                                sysIndexContent.setContentId(maxId.getId());
                                sysIndexContentService.save(sysIndexContent);

                                List<SysJobNote> noteIds = x.getNoteIds();
                                for (int i = 0; i < noteIds.size(); i++) {
                                    SysJobNote sysJobNote = noteIds.get(i);
                                    SysJobNote sysJobNote1 = new SysJobNote();
                                    if (sysJobNote.getId() == null) {
                                        if (sysJobNote.getNote()!=null && !sysJobNote.getNote().equals("")) {
                                            sysJobNote1.setNote(sysJobNote.getNote());
                                            sysJobNoteService.save(sysJobNote1);
                                            SysJobNote note = (SysJobNote) CommonUtils.getMaxId("sys_job_note", sysJobNoteService);
                                            sysJobContendMapper.insertNote(maxId.getId(), note.getId());
                                        }
                                    } else {
                                        sysJobNote1.setId(sysJobNote.getId());
                                        sysJobNote1.setNote(sysJobNote.getNote());
                                        sysJobNoteService.updateById(sysJobNote1);
                                        sysJobContendMapper.insertNote(maxId.getId(), sysJobNote.getId());
                                    }
                                }
                            }
                        } else {

                            //关联二级分类
                            SysIndexContent sysIndexContent = new SysIndexContent();
                            sysIndexContent.setIndexId(dto.getId());
                            sysIndexContent.setContentId(id);
                            sysIndexContentService.save(sysIndexContent);

                            //删除关联三级分类
                            sysJobContendMapper.cusRemoveNote(id);

                            sysJobContend.setId(id);
                            sysJobContend.setContend(x.getContend());
                            sysJobContend.setServicePlace(x.getServicePlace());
                            sysJobContend.setArea(x.getArea());
                            sysJobContend.setHome(x.getHome());
                            sysJobContendService.updateById(sysJobContend);


                            QueryWrapper<SysContendPrice> qw5 = new QueryWrapper<>();
                            qw5.eq("contend_id",id);
                            SysContendPrice one = sysContendPriceService.getOne(qw5);
                            one.setStatus(x.getStatus());
                            one.setPersonalPrice(x.getPersonalPrice());
                            one.setHour(x.getHour());
                            one.setFlat(x.getFlat());
                            one.setCompanyPrice(x.getCompanyPrice());
                            sysContendPriceService.updateById(one);

                            List<SysJobNote> noteIds = x.getNoteIds();
                            for (int i = 0; i < noteIds.size(); i++) {
                                SysJobNote sysJobNote = noteIds.get(i);
                                SysJobNote sysJobNote1 = new SysJobNote();
                                if (sysJobNote.getId()==null) {
                                    if (sysJobNote.getNote()!=null) {
                                        sysJobNote1.setNote(sysJobNote.getNote());
                                        sysJobNoteService.save(sysJobNote1);
                                        SysJobNote note = (SysJobNote) CommonUtils.getMaxId("sys_job_note", sysJobNoteService);
                                        sysJobContendMapper.insertNote(id, note.getId());
                                    }
                                } else {
                                    sysJobNote1.setId(sysJobNote.getId());
                                    sysJobNote1.setNote(sysJobNote.getNote());
                                    sysJobNoteService.updateById(sysJobNote1);
                                    sysJobContendMapper.insertNote(id, sysJobNote.getId());
                                }
                            }
                        }
                    }
            );
        return R.ok("修改成功");
    }

    @Override
    public R getCal(Integer year, Integer month) {
        LocalDate thisMonthFirstDay = LocalDate.of(year, month, 1);//這個月第一天
        LocalDate thisMonthLastDay = thisMonthFirstDay.plusMonths(1).plusDays(-1);//這個月最後一天 第一天加一個月然後減去一天

        Integer startWeek = thisMonthFirstDay.getDayOfWeek().getValue();
        Integer startMakeUp = startWeek == 7 ? 0 : startWeek;                 //   星期几 7 1 2 3 4 5 6
        //   需要補 0 1 2 3 4 5 6 天
        Integer endWeek = thisMonthLastDay.getDayOfWeek().getValue();
        Integer endMakeUp = endWeek == 7 ? 6 : 6-endWeek;              //   星期几 7 1 2 3 4 5 6
        //   需要補 6 5 4 3 2 1 0 天
        LocalDate startDate = thisMonthFirstDay.plusDays(-startMakeUp);
        LocalDate endDate = thisMonthLastDay.plusDays(endMakeUp);
        List<CalVO> dates = new ArrayList();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)){
            CalVO calVO = new CalVO();
            calVO.setDate(date);
            calVO.setIsThisMonth(date.getMonth().getValue()==(month));
            dates.add(calVO);
        }
        return R.ok(dates);
    }
}
