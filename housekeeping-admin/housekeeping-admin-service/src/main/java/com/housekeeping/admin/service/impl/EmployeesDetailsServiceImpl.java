package com.housekeeping.admin.service.impl;


import com.aliyun.oss.OSSClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.*;
import com.housekeeping.admin.entity.*;
import com.housekeeping.admin.mapper.EmployeesDetailsMapper;
import com.housekeeping.admin.pojo.EmployeesDetailsPOJO;
import com.housekeeping.admin.service.*;
import com.housekeeping.admin.vo.*;
import com.housekeeping.common.sms.SendMessage;
import com.housekeeping.common.utils.*;
import com.sun.org.apache.xpath.internal.operations.Bool;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Transactional
@Service("employeesDetailsService")
public class EmployeesDetailsServiceImpl extends ServiceImpl<EmployeesDetailsMapper, EmployeesDetails> implements EmployeesDetailsService {

    @Resource
    private ICompanyDetailsService companyDetailsService;
    @Resource
    private ManagerDetailsService managerDetailsService;
    @Resource
    private IEmployeesPromotionService employeesPromotionService;
    @Resource
    private IUserService userService;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private IEmployeesWorkExperienceService employeesWorkExperienceService;
    @Resource
    private IAddressCodingService addressCodingService;
    @Resource
    private IEmployeesJobsService employeesJobsService;
    @Resource
    private IEmployeesCalendarService employeesCalendarService;
    @Resource
    private IEmployeesContractService employeesContractService;
    @Resource
    private IGroupEmployeesService groupEmployeesService;
    @Resource
    private IGroupManagerService groupManagerService;
    @Resource
    private EmployeesDetailsService employeesDetailsService;
    @Resource
    private IGroupDetailsService groupDetailsService;
    @Resource
    private ISysJobContendService sysJobContendService;
    @Resource
    private IDemandEmployeesService demandEmployService;
    @Resource
    private ICompanyCalendarService companyCalendarService;
    @Resource
    private IOrderDetailsService orderDetailsService;
    @Resource
    private OSSClient ossClient;
    @Value("${oss.bucketName}")
    private String bucketName;
    @Value("${oss.urlPrefix}")
    private String urlPrefix;

    @Transactional
    @Override
    public R saveEmp(EmployeesDetailsDTO employeesDetailsDTO,String type) throws ParseException {

        Integer maxEmployeesId = 0;

        if(CommonUtils.isNotEmpty(employeesDetailsDTO)){
            //先保存User
            User user = new User();
            String ss = String.valueOf(System.currentTimeMillis());
            user.setName(employeesDetailsDTO.getName());
            user.setNumber("c"+ss);
            user.setDeptId(5);
            user.setLastReviserId(TokenUtils.getCurrentUserId());
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());
            Integer maxUserId = 0;
            synchronized (this) {
                userService.save(user);
                maxUserId = ((User) CommonUtils.getMaxId("sys_user", userService)).getId();
            }

            EmployeesDetails employeesDetails = new EmployeesDetails();
            QueryWrapper<CompanyDetails> wrComp=new QueryWrapper<>();
            wrComp.inSql("id","select id from company_details where user_id=" + TokenUtils.getCurrentUserId());
            CompanyDetails one = companyDetailsService.getOne(wrComp);
            employeesDetails.setUserId(maxUserId);
            employeesDetails.setBankName(employeesDetailsDTO.getBankName());
            employeesDetails.setBankAccount(employeesDetailsDTO.getBankAccount());
            employeesDetails.setEmailAccount(employeesDetailsDTO.getEmailAccount());
            employeesDetails.setNumber(employeesDetailsDTO.getNumber());
            employeesDetails.setName(employeesDetailsDTO.getName());
            employeesDetails.setSex(employeesDetailsDTO.getSex());
            employeesDetails.setDateOfBirth(employeesDetailsDTO.getDateOfBirth());
            employeesDetails.setIdCard(employeesDetailsDTO.getIdCard());
            employeesDetails.setAddress1(employeesDetailsDTO.getAddress1());
            employeesDetails.setAddress2(employeesDetailsDTO.getAddress2());
            employeesDetails.setAddress3(employeesDetailsDTO.getAddress3());
            employeesDetails.setAddress4(employeesDetailsDTO.getAddress4());


            employeesDetails.setWorkingArea(employeesDetailsDTO.getWorkingArea());

            /** 2021/1/14 su 新增存放地址經緯度 **/
            if(CommonUtils.isNotEmpty(employeesDetailsDTO.getLng())){
                employeesDetails.setLng(employeesDetailsDTO.getLng().toString());
            }
            if(CommonUtils.isNotEmpty(employeesDetailsDTO.getLat())){
                employeesDetails.setLat(employeesDetailsDTO.getLat().toString());
            }
            /** 2021/1/14 su 新增存放地址經緯度 **/

            employeesDetails.setEducationBackground(employeesDetailsDTO.getEducationBackground());
            employeesDetails.setPhonePrefix(employeesDetailsDTO.getPhonePrefix());
            employeesDetails.setPhone(employeesDetailsDTO.getPhone());
            employeesDetails.setAccountLine(employeesDetailsDTO.getAccountLine());
            employeesDetails.setDescribes(employeesDetailsDTO.getDescribes());

            if(CommonUtils.isNotEmpty(employeesDetailsDTO.getWorkExperiencesDTO().get(0).getWorkYear())){
                List<EmployeesWorkExperienceDTO> workExperiencesDTO = employeesDetailsDTO.getWorkExperiencesDTO();
                if(CommonUtils.isNotEmpty(workExperiencesDTO)){
                    for (int i = 0; i < workExperiencesDTO.size(); i++) {
                        employeesDetails.setWorkYear(workExperiencesDTO.get(i).getWorkYear());
                    }
                }

            }

            employeesDetails.setStarRating(3.0f); //新增的员工默认为三星级，中等好评
            employeesDetails.setBlacklistFlag(false);
            employeesDetails.setNumberOfOrders(0); //默认接单次数为0

            employeesDetails.setUpdateTime(LocalDateTime.now());
            employeesDetails.setCreateTime(LocalDateTime.now());
            employeesDetails.setCompanyId(one.getId().toString());
            employeesDetails.setLastReviserId(TokenUtils.getCurrentUserId());

            /** 头像 */
            if(employeesDetailsDTO.getHeadUrl()!=null&&!employeesDetailsDTO.getHeadUrl().equals("")){
                employeesDetails.setHeadUrl(employeesDetailsDTO.getHeadUrl());
            }else {
                employeesDetails.setHeadUrl("https://test-live-video.oss-cn-shanghai.aliyuncs.com/HKFile/ImPhoto/userId=/20210508103930.png");
            }
            /** 头像 */


            Object savePoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
            try {
                synchronized (this){
                    this.save(employeesDetails);
                    maxEmployeesId = ((EmployeesDetails) CommonUtils.getMaxId("employees_details", this)).getId();
                }
                /**
                 * 順便建個員工推廣的表記錄
                 */
                EmployeesPromotion employeesPromotion = new EmployeesPromotion();
                employeesPromotion.setEmployeesId(maxEmployeesId);
                employeesPromotionService.save(employeesPromotion);
                /**
                 * 工作经验保存
                 */
                employeesWorkExperienceService.saveEmployeesWorkExperience(employeesDetailsDTO.getWorkExperiencesDTO(), maxEmployeesId);

               /* if (CollectionUtils.isNotEmpty(employeesDetailsDTO.getJobIds()))
                    employeesCalendarService.setJobs(new SetEmployeesJobsDTO(employeesDetailsDTO.getJobIds(), maxEmployeesId));*/

                /**
                 * 时间表初始化，时间表模板为空就不初始化
                 */
            } catch (Exception e){
                TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savePoint);
                return R.failed("添加失敗");
            }
        }
        return R.ok(maxEmployeesId,"添加員工成功");
    }

    @Override
    public R updateEmp(EmployeesDetailsDTO employeesDetailsDTO) throws ParseException {


        EmployeesDetails byId = this.getById(employeesDetailsDTO.getId());

        //修改员工名、关联修改聊天组名
        if(!byId.getName().equals(employeesDetailsDTO.getName())){
            User byId1 = userService.getById(byId.getUserId());
            byId1.setName(employeesDetailsDTO.getName());
            userService.updateById(byId1);
            List<Integer> chatIds = baseMapper.getAllChatGroup(byId.getUserId());
            for (int i = 0; i < chatIds.size(); i++) {
                baseMapper.updateChatName(chatIds.get(i),employeesDetailsDTO.getName());
            }
        }

        //修改员工头像、关联修改聊天组头像
        if(!byId.getHeadUrl().equals(employeesDetailsDTO.getHeadUrl())){
            List<Integer> chatIds = baseMapper.getAllChatGroup(byId.getUserId());
            for (int i = 0; i < chatIds.size(); i++) {
                baseMapper.updateChatPhoto(chatIds.get(i),employeesDetailsDTO.getHeadUrl());
            }
        }

        EmployeesDetails employeesDetails = new EmployeesDetails();
        employeesDetails.setBankName(employeesDetailsDTO.getBankName());
        employeesDetails.setBankAccount(employeesDetailsDTO.getBankAccount());
        employeesDetails.setEmailAccount(employeesDetailsDTO.getEmailAccount());
        employeesDetails.setId(employeesDetailsDTO.getId());
        employeesDetails.setRecordOfFormalSchooling(employeesDetailsDTO.getRecordOfFormalSchooling());
        employeesDetails.setStarRating(employeesDetailsDTO.getStarRating());
        employeesDetails.setNumber(employeesDetailsDTO.getNumber());
        employeesDetails.setName(employeesDetailsDTO.getName());
        employeesDetails.setSex(employeesDetailsDTO.getSex());
        employeesDetails.setDateOfBirth(employeesDetailsDTO.getDateOfBirth());
        employeesDetails.setIdCard(employeesDetailsDTO.getIdCard());
        employeesDetails.setAddress1(employeesDetailsDTO.getAddress1());
        employeesDetails.setAddress2(employeesDetailsDTO.getAddress2());
        employeesDetails.setAddress3(employeesDetailsDTO.getAddress3());
        employeesDetails.setAddress4(employeesDetailsDTO.getAddress4());

        /** 2021/1/14 su 新增存放地址經緯度 **/
        employeesDetails.setLng(employeesDetailsDTO.getLng().toString());
        employeesDetails.setLat(employeesDetailsDTO.getLat().toString());
        /** 2021/1/14 su 新增存放地址經緯度 **/

        employeesDetails.setEducationBackground(employeesDetailsDTO.getEducationBackground());
        employeesDetails.setPhonePrefix(employeesDetailsDTO.getPhonePrefix());
        employeesDetails.setPhone(employeesDetailsDTO.getPhone());
        employeesDetails.setAccountLine(employeesDetailsDTO.getAccountLine());
        employeesDetails.setDescribes(employeesDetailsDTO.getDescribes());

        employeesDetails.setWorkingArea(employeesDetailsDTO.getWorkingArea());

        if(CommonUtils.isNotEmpty(employeesDetailsDTO.getWorkExperiencesDTO().get(0).getWorkYear())){
            List<EmployeesWorkExperienceDTO> workExperiencesDTO = employeesDetailsDTO.getWorkExperiencesDTO();
            if(CommonUtils.isNotEmpty(workExperiencesDTO)){
                for (int i = 0; i < workExperiencesDTO.size(); i++) {
                    employeesDetails.setWorkYear(workExperiencesDTO.get(i).getWorkYear());
                }
            }

        }

        employeesDetails.setUpdateTime(LocalDateTime.now());
        employeesDetails.setLastReviserId(TokenUtils.getCurrentUserId());

        employeesDetails.setHeadUrl(employeesDetailsDTO.getHeadUrl());
        this.updateById(employeesDetails);
        /**
         * 工作经验修改
         */
        employeesWorkExperienceService.saveEmployeesWorkExperience(employeesDetailsDTO.getWorkExperiencesDTO(), employeesDetailsDTO.getId());

        /*employeesCalendarService.setJobs(new SetEmployeesJobsDTO(employeesDetailsDTO.getJobIds(), employeesDetailsDTO.getId()));*/

        return R.ok("修改成功");


    }

    @Override
    public R cusPage(Page page, PageOfEmployeesDetailsDTO pageOfEmployeesDetailsDTO, String type) {
        QueryWrapper  queryWrapper = new QueryWrapper();
        queryWrapper.orderByDesc("id");
//        if (CommonUtils.isNotEmpty(pageOfEmployeesDetailsDTO.getNumber())){
//            queryWrapper.like("number", pageOfEmployeesDetailsDTO.getNumber());
//        }
        if (CommonUtils.isNotEmpty(pageOfEmployeesDetailsDTO.getId())){
            queryWrapper.eq("id", pageOfEmployeesDetailsDTO.getId());
        }
        if (CommonUtils.isNotEmpty(pageOfEmployeesDetailsDTO.getName())){
            queryWrapper.like("name", pageOfEmployeesDetailsDTO.getName());
        }
//        if (CommonUtils.isNotEmpty(pageOfEmployeesDetailsDTO.getSex())){
//            queryWrapper.eq("sex", pageOfEmployeesDetailsDTO.getSex());
//        }
//        if (CommonUtils.isNotEmpty(pageOfEmployeesDetailsDTO.getDateOfBirth())){
//            queryWrapper.eq("date_of_birth", pageOfEmployeesDetailsDTO.getDateOfBirth());
//        }
//        if (CommonUtils.isNotEmpty(pageOfEmployeesDetailsDTO.getIdCard())){
//            queryWrapper.like("id_card", pageOfEmployeesDetailsDTO.getIdCard());
//        }
//        if (CommonUtils.isNotEmpty(pageOfEmployeesDetailsDTO.getAddress1())){
//            queryWrapper.like("address1", pageOfEmployeesDetailsDTO.getAddress1());
//        }
//        if (CommonUtils.isNotEmpty(pageOfEmployeesDetailsDTO.getAddress2())){
//            queryWrapper.like("address2", pageOfEmployeesDetailsDTO.getAddress2());
//        }
//        if (CommonUtils.isNotEmpty(pageOfEmployeesDetailsDTO.getAddress3())){
//            queryWrapper.like("address3", pageOfEmployeesDetailsDTO.getAddress3());
//        }
//        if (CommonUtils.isNotEmpty(pageOfEmployeesDetailsDTO.getAddress4())){
//            queryWrapper.like("address4", pageOfEmployeesDetailsDTO.getAddress4());
//        }
//        if (CommonUtils.isNotEmpty(pageOfEmployeesDetailsDTO.getRecordOfFormalSchooling())){
//            queryWrapper.like("record_of_formal_schooling", pageOfEmployeesDetailsDTO.getRecordOfFormalSchooling());
//        }
//        if (CommonUtils.isNotEmpty(pageOfEmployeesDetailsDTO.getPhone())){
//            queryWrapper.like("phone", pageOfEmployeesDetailsDTO.getPhone());
//        }
        if (CommonUtils.isNotEmpty(pageOfEmployeesDetailsDTO.getAccountLine())){
            queryWrapper.like("account_line", pageOfEmployeesDetailsDTO.getAccountLine());
        }
//        if (CommonUtils.isNotEmpty(pageOfEmployeesDetailsDTO.getDescribes())){
//            queryWrapper.like("describe", pageOfEmployeesDetailsDTO.getDescribes());
//        }

        if (type.equals(CommonConstants.REQUEST_ORIGIN_COMPANY)){
            Integer userId = TokenUtils.getCurrentUserId();
            Integer companyId = companyDetailsService.getCompanyIdByUserId(userId);

            /*CompanyDetails cd = companyDetailsService.getById(companyId);
            Boolean certified = cd.getIsValidate(); //是否已认证*/

            queryWrapper.eq("company_id", companyId);

            List<EmployeesDetails> eds = this.list(queryWrapper);
            List<EmployeesDetailsPOJO> edps = eds.stream().map(ed->{

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

                EmployeesDetailsPOJO edp = new EmployeesDetailsPOJO(ed);
                edp.setWorkingArea(ed.getWorkingArea());
                //技能标签和是否已认证
                edp.setCertified(certified);
                List<SysJobContend> jobs = new ArrayList<>();
                List<Integer> jobIds = CommonUtils.stringToList(ed.getPresetJobIds());
                if (!jobIds.isEmpty()) jobs = sysJobContendService.listByIds(jobIds);
                edp.setSkillTags(jobs);
                return edp;
            }).collect(Collectors.toList());
            Page pages = PageUtils.getPages((int)page.getCurrent(), (int)page.getSize(), edps);
            return R.ok(pages, "分頁查詢成功");
        }

        if (type.equals(CommonConstants.REQUEST_ORIGIN_MANAGER)){
            QueryWrapper queryWrapper2 = new QueryWrapper();
            queryWrapper2.eq("user_id", TokenUtils.getCurrentUserId());
            ManagerDetails managerDetails = managerDetailsService.getOne(queryWrapper2);
            Integer managerId = managerDetails.getId();
            Integer companyId = managerDetailsService.getCompanyIdByManagerId(managerId);
            CompanyDetails cd = companyDetailsService.getById(companyId);
            queryWrapper.eq("company_id", companyId);

           /* Boolean certified = cd.getIsValidate(); //是否已认证*/
            queryWrapper.eq("company_id", companyId);

            List<EmployeesDetails> eds = this.list(queryWrapper);
            List<EmployeesDetailsPOJO> edps = eds.stream().map(ed->{

                /** certified:員工認證準備 */
                Integer certified;
                //不属于公司就是个体户 1个体户 2工作室 3公司
                CompanyDetails cod = companyDetailsService.getById(ed.getCompanyId());
                if(cod.getIsPersonal()==true){
                    certified = 1;
                }else {
                    Boolean isCertified = cod.getIsValidate();
                    if(isCertified==false){
                        certified = 2;
                    }else {
                        certified = 3;
                    }
                }

                EmployeesDetailsPOJO edp = new EmployeesDetailsPOJO(ed);
                //技能标签和是否已认证
                edp.setCertified(certified);
                List<SysJobContend> jobs = new ArrayList<>();
                List<Integer> jobIds = CommonUtils.stringToList(ed.getPresetJobIds());
                if (!jobIds.isEmpty()) jobs = sysJobContendService.listByIds(jobIds);
                edp.setSkillTags(jobs);
                return edp;
            }).collect(Collectors.toList());
            Page pages = PageUtils.getPages((int)page.getCurrent(), (int)page.getSize(), edps);
            return R.ok(pages, "分頁查詢成功");
        }

        IPage<EmployeesDetails> employeesDetailsIPage = baseMapper.selectPage(page, queryWrapper);

        return R.ok(employeesDetailsIPage, "分頁查詢成功");
    }

    @Override
    public R getLinkToLogin(Integer id, Long h) throws UnknownHostException {
        /* 鉴权 */
        String roleType = TokenUtils.getRoleType();
        if (roleType.equals(CommonConstants.REQUEST_ORIGIN_COMPANY)){
            if (!companyDetailsService.thereIsACleaner(id)){
                return R.failed(null, "該員工不存在");
            }
        }else if (roleType.equals(CommonConstants.REQUEST_ORIGIN_MANAGER)){
            if (managerDetailsService.thereIsACleaner(id)){
                return R.failed(null, "該員工不受您管轄或者員工不存在");
            }
        }else {
            return R.failed(null, "鑒權失敗");
        }

        EmployeesDetails employeesDetails = baseMapper.selectById(id);
        if (CommonUtils.isNotEmpty(employeesDetails)){
            String mysteriousCode = CommonUtils.getMysteriousCode(); //神秘代码
            String key = CommonConstants.LOGIN_EMPLOYEES_PREFIX + mysteriousCode;
            Integer value = employeesDetails.getUserId();
            redisUtils.set(key, value, 60 * 60 * h);//有效期12小时
            //拼接url链接
            return R.ok(mysteriousCode);
        } else {
            return R.failed("員工不存在，請刷新頁面重試");
        }
    }

    @Override
    public R cusPage1(Page page, PageOfEmployeesDTO pageOfEmployeesDTO, String type) {
        QueryWrapper<EmployeesDetails>  queryWrapper = new QueryWrapper();
//        if (CommonUtils.isNotEmpty(pageOfEmployeesDetailsDTO.getNumber())){
//            queryWrapper.like("number", pageOfEmployeesDetailsDTO.getNumber());
//        }
        if (CommonUtils.isNotEmpty(pageOfEmployeesDTO.getId())){
            queryWrapper.eq("id", pageOfEmployeesDTO.getId());
        }
        if (CommonUtils.isNotEmpty(pageOfEmployeesDTO.getName())){
            queryWrapper.like("name", pageOfEmployeesDTO.getName());
        }
//        if (CommonUtils.isNotEmpty(pageOfEmployeesDetailsDTO.getSex())){
//            queryWrapper.eq("sex", pageOfEmployeesDetailsDTO.getSex());
//        }
//        if (CommonUtils.isNotEmpty(pageOfEmployeesDetailsDTO.getDateOfBirth())){
//            queryWrapper.eq("date_of_birth", pageOfEmployeesDetailsDTO.getDateOfBirth());
//        }
//        if (CommonUtils.isNotEmpty(pageOfEmployeesDetailsDTO.getIdCard())){
//            queryWrapper.like("id_card", pageOfEmployeesDetailsDTO.getIdCard());
//        }
//        if (CommonUtils.isNotEmpty(pageOfEmployeesDetailsDTO.getAddress1())){
//            queryWrapper.like("address1", pageOfEmployeesDetailsDTO.getAddress1());
//        }
//        if (CommonUtils.isNotEmpty(pageOfEmployeesDetailsDTO.getAddress2())){
//            queryWrapper.like("address2", pageOfEmployeesDetailsDTO.getAddress2());
//        }
//        if (CommonUtils.isNotEmpty(pageOfEmployeesDetailsDTO.getAddress3())){
//            queryWrapper.like("address3", pageOfEmployeesDetailsDTO.getAddress3());
//        }
//        if (CommonUtils.isNotEmpty(pageOfEmployeesDetailsDTO.getAddress4())){
//            queryWrapper.like("address4", pageOfEmployeesDetailsDTO.getAddress4());
//        }
//        if (CommonUtils.isNotEmpty(pageOfEmployeesDetailsDTO.getRecordOfFormalSchooling())){
//            queryWrapper.like("record_of_formal_schooling", pageOfEmployeesDetailsDTO.getRecordOfFormalSchooling());
//        }
//        if (CommonUtils.isNotEmpty(pageOfEmployeesDetailsDTO.getPhone())){
//            queryWrapper.like("phone", pageOfEmployeesDetailsDTO.getPhone());
//        }
        if (CommonUtils.isNotEmpty(pageOfEmployeesDTO.getAccountLine())){
            queryWrapper.like("account_line", pageOfEmployeesDTO.getAccountLine());
        }
//        if (CommonUtils.isNotEmpty(pageOfEmployeesDetailsDTO.getDescribes())){
//            queryWrapper.like("describe", pageOfEmployeesDetailsDTO.getDescribes());
//        }
        if (CommonUtils.isNotEmpty(pageOfEmployeesDTO.getCompanyId())){
            queryWrapper.like("company_id", pageOfEmployeesDTO.getCompanyId());
        }
        List<EmployeesDetails> employeesDetails = this.list(queryWrapper);
        List<EmployeesDetailsWorkVo> employeesDetailsWorkVos = new ArrayList<>();
        for (int i = 0; i < employeesDetails.size(); i++) {

            EmployeesDetailsWorkVo employeesDetailsWorkVo = new EmployeesDetailsWorkVo();

            employeesDetailsWorkVo.setEmployeesDetails(employeesDetails.get(i));

            QueryWrapper<EmployeesWorkExperience> qw2 = new QueryWrapper<>();
            qw2.eq("employees_id",employeesDetails.get(i).getId());
            List<EmployeesWorkExperience> list = employeesWorkExperienceService.list(qw2);
            employeesDetailsWorkVo.setEmployeesWorkExperiences(list);

            Integer collectionVolume =  baseMapper.getCollectionVolume(employeesDetails.get(i).getId());
            employeesDetailsWorkVo.setCollectionVolume(collectionVolume);

            CompanyDetails byId = companyDetailsService.getById(employeesDetails.get(i).getCompanyId());
            employeesDetailsWorkVo.setCompanyDetails(byId);

            employeesDetailsWorkVos.add(employeesDetailsWorkVo);
        }

        Page pages = PageUtils.getPages((int) page.getCurrent(), (int) page.getSize(), employeesDetailsWorkVos);
        return R.ok(pages);
    }

    @Override
    public String uploadHead(MultipartFile file, Integer id) throws IOException {
        String res = "";

        LocalDateTime now = LocalDateTime.now();
        String nowString = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String catalogue = CommonConstants.HK_EMPLOYEES_HEAD_ABSTRACT_PATH_PREFIX_PROV + id;
        String type = file.getOriginalFilename().split("\\.")[1];
        String fileAbstractPath = catalogue + "/" + nowString+"."+ type;

        try {
            ossClient.putObject(bucketName, fileAbstractPath, new ByteArrayInputStream(file.getBytes()));
            res = urlPrefix + fileAbstractPath;
        } catch (Exception e) {
            e.printStackTrace();
            return "error upload";
        }

        return res;
    }

    @Override
    public R updateHeadUrlByUserId(String headUrl, Integer id) {
        baseMapper.updateHeadUrlById(headUrl, id);
        return R.ok();
    }

    @Override
    public R canSheMakeAnWork(Integer employeesId) {
        Map<String, Boolean> map = new HashMap<>();
        QueryWrapper qw = new QueryWrapper();
        qw.eq("employees_id", employeesId);
        EmployeesDetails employeesDetails = this.getById(employeesId);
        if (CommonUtils.isEmpty(employeesDetails)){
            return R.failed("該員工不存在");
        }
        List<EmployeesCalendar> employeesCalendarList = employeesCalendarService.list(qw);
        List<EmployeesContract> employeesContractList = employeesContractService.list(qw);
        map.put("canSheMakeAnHour", employeesCalendarList.size() != 0);
        map.put("canSheWorkAsAContractor", employeesContractList.size() != 0);
        return R.ok(map, "获取成功");
    }

    @Override
    public R blacklist(Integer employeesId, Boolean action) {
        baseMapper.blacklist(employeesId, action);
        return R.ok(null, "操作成功");
    }

    @Override
    public Boolean isMe(Integer employeesId) {
        Integer userId = TokenUtils.getCurrentUserId();
        QueryWrapper qw = new QueryWrapper();
        qw.eq("user_id", userId);
        EmployeesDetails employeesDetails = this.getOne(qw);
        return employeesDetails.getId().equals(employeesId);
    }

    @Override
    public R cusRemove(Integer employeesId) {
        EmployeesDetails employeesDetails = this.getById(employeesId);
        Integer userId = OptionalBean.ofNullable(employeesDetails)
                .getBean(EmployeesDetails::getUserId).get();
        if (CommonUtils.isEmpty(userId)){
            return R.failed("该员工不存在！");
        }
        userService.removeById(userId); //刪除依賴1
        QueryWrapper qw = new QueryWrapper<>();
        qw.eq("employees_id", employeesId);
        groupEmployeesService.remove(qw); //刪除依賴2
        employeesWorkExperienceService.remove(qw); //刪除依賴3
        employeesJobsService.remove(qw); //删除依赖4
        employeesCalendarService.remove(qw); //删除依赖5
        employeesPromotionService.remove(qw); //删除依赖6
        employeesContractService.remove(qw);//刪除依賴7
        demandEmployService.remove(qw);


        //……
        this.removeById(employeesId);
        return R.ok("删除成功");
    }

    /**
     * 判斷公司是否可以新增員工
     * @return
     */
    public Boolean addEmployee(){
        Integer userId = TokenUtils.getCurrentUserId();
        QueryWrapper<CompanyDetails> wrComp=new QueryWrapper<>();
        wrComp.inSql("id","select id from company_details where user_id="+ userId);
        CompanyDetails one = companyDetailsService.getOne(wrComp);
        String scaleById = baseMapper.getScaleById(one.getCompanySizeId());
        String[] split = scaleById.split(" ");
        Integer companyMaxsize = Integer.parseInt(split[1]);
        QueryWrapper<EmployeesDetails> qw = new QueryWrapper<>();
        qw.eq("company_id",one.getId());
        Integer currentSize = baseMapper.selectCount(qw);
        if(companyMaxsize>currentSize){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 判斷经理是否可以新增員工
     * @return
     */
    public Boolean addEmployeeByMan(){
        Integer userId = TokenUtils.getCurrentUserId();
        QueryWrapper<ManagerDetails> qw = new QueryWrapper<>();
        qw.eq("user_id",userId);
        ManagerDetails one1 = managerDetailsService.getOne(qw);
        CompanyDetails one = companyDetailsService.getById(one1.getCompanyId());
        String scaleById = baseMapper.getScaleById(one.getCompanySizeId());
        String[] split = scaleById.split(" ");
        Integer companyMaxsize = Integer.parseInt(split[1]);
        QueryWrapper<EmployeesDetails> qw2 = new QueryWrapper<>();
        qw2.eq("company_id",one.getId());
        Integer currentSize = baseMapper.selectCount(qw2);
        if(companyMaxsize>currentSize){
            return true;
        }else {
            return false;
        }
    }


    public List<Integer> getAllIdsByCompanyId(Integer companyId){
        return baseMapper.getAllIdsByCompanyId(companyId);
    }

    @Override
    public Boolean judgmentOfExistence(Integer employeesId) {
        EmployeesDetails details = this.getById(employeesId);
        if (CommonUtils.isNotEmpty(details)){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public Boolean judgmentOfExistenceFromCompany(Integer employeesId) {
        EmployeesDetails details = this.getById(employeesId);
        if (CommonUtils.isEmpty(details)) return false;

        Integer userId = TokenUtils.getCurrentUserId();
        QueryWrapper qw = new QueryWrapper();
        qw.eq("user_id", userId);
        CompanyDetails companyDetails = companyDetailsService.getOne(qw);
        if (companyDetails.getId().toString().equals(details.getCompanyId())) return true;

        return false;
    }

    @Override
    public Boolean judgmentOfExistenceFromManager(Integer employeesId) {
        EmployeesDetails details = this.getById(employeesId);
        if (CommonUtils.isEmpty(details)) return false;

        Integer userId = TokenUtils.getCurrentUserId();
        QueryWrapper qw = new QueryWrapper();
        qw.eq("user_id", userId);
        ManagerDetails managerDetails = managerDetailsService.getOne(qw);
        if (managerDetails.getCompanyId().toString().equals(details.getCompanyId())) return true;

        return false;
    }

    @Override
    public Boolean judgmentOfExistenceHaveJurisdictionOverManager(Integer employeesId) {
        EmployeesDetails details = this.getById(employeesId);
        if (CommonUtils.isEmpty(details)) return false;

        Integer userId = TokenUtils.getCurrentUserId();
        QueryWrapper qw = new QueryWrapper();
        qw.eq("user_id", userId);
        ManagerDetails managerDetails = managerDetailsService.getOne(qw);
        Integer managerId = managerDetails.getId();
        QueryWrapper qw2 = new QueryWrapper();
        qw2.eq("employees_id", employeesId);
        List<GroupEmployees> groupEmployeesList = groupEmployeesService.list(qw2); //该员工所属的组
        Set<Integer> groupIds1 = groupEmployeesList.stream().map(x -> {
            return x.getId();
        }).collect(Collectors.toSet());
        if (CommonUtils.isEmpty(groupIds1)) return false;

        QueryWrapper qw3 = new QueryWrapper();
        qw3.eq("manager_id", managerId);
        List<GroupManager> groupManagerList = groupManagerService.list(qw3);   //该经理所属的组
        Set<Integer> groupIds2 = groupManagerList.stream().map(x -> {
            return x.getGroupId();
        }).collect(Collectors.toSet());
        if (CommonUtils.isEmpty(groupIds2)) return false;

        groupIds1.retainAll(groupIds2);
        if (CommonUtils.isNotEmpty(groupIds1)) return true;

        return false;
    }

    @Override
    public Boolean judgeEmployeesInCompany(Integer employeesId, Integer companyId) {
        EmployeesDetails ed = this.baseMapper.selectById(employeesId);
        if (CommonUtils.isEmpty(ed)) return false;
        if (!ed.getCompanyId().equals(companyId.toString())) return false;
        return true;
    }

    @Override
    public void setPresetJobIds(String presetJobIds, Integer employeesId) {
        baseMapper.setPresetJobIds(presetJobIds, employeesId);
    }

    @Override
    public R putWorkArea(List<Integer> areaIds) {
        Integer userId = TokenUtils.getCurrentUserId();
        QueryWrapper qw = new QueryWrapper();
        qw.eq("user_id", userId);
        EmployeesDetails employeesDetails = employeesDetailsService.getOne(qw);
        if (areaIds.size() > 3){
            return R.failed("最多隻能設置三個");
        }
        StringBuilder sb = new StringBuilder();
        areaIds.forEach(x -> {
            sb.append(x).append(" ");
        });
        baseMapper.setWorkingArea(employeesDetails.getId(), sb.toString().trim());
        return R.ok(null, "設置成功");
    }

    @Override
    public R getInfoById() {
        Integer currentUserId = TokenUtils.getCurrentUserId();
        QueryWrapper qw = new QueryWrapper();
        qw.eq("user_id", currentUserId);
        EmployeesDetails one = this.getOne(qw);
        return R.ok(one);
    }

    @Override
    public R getAllEmpByCompanyId(Integer companyId) {
        QueryWrapper<EmployeesDetails> qw = new QueryWrapper<>();
        qw.eq("company_id",companyId);
        List<EmployeesDetails> eds = this.list(qw);
        List<EmployeesDetailsPOJO> edps = eds.stream().map(ed->{
            EmployeesDetailsPOJO edp = new EmployeesDetailsPOJO(ed);
            //技能标签
            List<SysJobContend> jobs = new ArrayList<>();
            List<Integer> jobIds = CommonUtils.stringToList(ed.getPresetJobIds());
            if (!jobIds.isEmpty()) jobs = sysJobContendService.listByIds(jobIds);
            edp.setSkillTags(jobs);
            return edp;
        }).collect(Collectors.toList());
        return R.ok(edps);
    }

    @Override
    public R getDetailById(Integer empId) {
        EmployeesDetailsSkillVo byId = baseMapper.getCusById(empId);
        if(CommonUtils.isEmpty(byId)){
            return R.failed("沒有此員工");
        }

        /** certified:員工認證準備 */
        Integer certified;

        //不属于公司就是个体户 1个体户 2工作室 3公司
        if(byId.getCompanyId()==null){
            certified = 1;
            byId.setNoCertifiedCompany("");
        }else {
            CompanyDetails cd = companyDetailsService.getById(byId.getCompanyId());
            byId.setNoCertifiedCompany(cd.getNoCertifiedCompany()==null?"":cd.getNoCertifiedCompany());
            Boolean isCertified = cd.getIsValidate();
            if(isCertified==false){
                certified = 2;
            }else {
                certified = 3;
            }
        }

        if(CommonUtils.isEmpty(byId.getPresetJobIds())||byId.getPresetJobIds().equals("")){
            byId.setSkillTags(null);
        }else {
            String presetJobIds = byId.getPresetJobIds();
            List<Skill> skills = new ArrayList<>();
            List<String> strings = Arrays.asList(presetJobIds.split(" "));
            for (int i = 0; i < strings.size(); i++) {
                Skill skill = new Skill();
                skill.setJobId(Integer.parseInt(strings.get(i)));
                skill.setContend(sysJobContendService.getById(Integer.parseInt(strings.get(i))).getContend());
                skills.add(skill);
            }
            byId.setSkillTags(skills);
            byId.setCertified(certified);
        }

        //獲取收藏量
        Integer collectionVolume =  baseMapper.getCollectionVolume(empId);
        byId.setCollectionVolume(collectionVolume);
        return R.ok(byId);
    }

    @Override
    public R getAllEmployeesByAdmin(Page page, PageOfEmployeesDTO pageOfEmployeesDTO) {
        List<EmployeesVo> allEmployeesByAdmin = baseMapper.getAllEmployeesByAdmin(pageOfEmployeesDTO);
        if(CommonUtils.isEmpty(allEmployeesByAdmin)){
            return R.ok(null);
        }
        allEmployeesByAdmin.forEach(x ->{
            Integer collectionVolume =  baseMapper.getCollectionVolume(x.getId());
            x.setCollectionVolume(collectionVolume);
        });
        Page pages = PageUtils.getPages((int) page.getCurrent(), (int) page.getSize(), allEmployeesByAdmin);
        return R.ok(pages);
    }

    @Override
    public Integer getEmployeesIdByExistToken() {
        Integer userId = TokenUtils.getCurrentUserId();
        QueryWrapper qw = new QueryWrapper();
        qw.eq("user_id", userId);
        EmployeesDetails employeesDetails = employeesDetailsService.getOne(qw);
        return employeesDetails.getId();
    }

    @Override
    public Integer getEmployeesIdByUserId(Integer userId) {
        QueryWrapper qw = new QueryWrapper();
        qw.eq("user_id", userId);
        EmployeesDetails employeesDetails = employeesDetailsService.getOne(qw);
        return employeesDetails.getId();
    }

    @Override
    public R cusPage5(Page page, PageOfEmployeesDetailsDTO pageOfEmployeesDetailsDTO) {
        QueryWrapper<EmployeesDetails> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        if (CommonUtils.isNotEmpty(pageOfEmployeesDetailsDTO.getId())){
            queryWrapper.eq("id", pageOfEmployeesDetailsDTO.getId());
        }
        if (CommonUtils.isNotEmpty(pageOfEmployeesDetailsDTO.getName())){
            queryWrapper.like("name", pageOfEmployeesDetailsDTO.getName());
        }

        if (CommonUtils.isNotEmpty(pageOfEmployeesDetailsDTO.getAccountLine())){
            queryWrapper.like("account_line", pageOfEmployeesDetailsDTO.getAccountLine());
        }

        Integer userId = TokenUtils.getCurrentUserId();
        Integer companyId = companyDetailsService.getCompanyIdByUserId(userId);
        queryWrapper.eq("company_id", companyId);

        List<EmployeesDetails> employeesDetails = this.list(queryWrapper);

        List<EmployeesDetailsWorkVo> employeesDetailsWorkVos = new ArrayList<>();
        for (int i = 0; i < employeesDetails.size(); i++) {

            EmployeesDetailsWorkVo employeesDetailsWorkVo = new EmployeesDetailsWorkVo();

            employeesDetailsWorkVo.setEmployeesDetails(employeesDetails.get(i));

            QueryWrapper<EmployeesWorkExperience> qw2 = new QueryWrapper<>();
            qw2.eq("employees_id",employeesDetails.get(i).getId());
            List<EmployeesWorkExperience> list = employeesWorkExperienceService.list(qw2);
            employeesDetailsWorkVo.setEmployeesWorkExperiences(list);

            employeesDetailsWorkVos.add(employeesDetailsWorkVo);
        }

        Page pages = PageUtils.getPages((int) page.getCurrent(), (int) page.getSize(), employeesDetailsWorkVos);
        return R.ok(pages);
    }

    @Override
    public R cusPage7(Page page, PageOfEmployeesDetailsDTO pageOfEmployeesDetailsDTO) {
        QueryWrapper<EmployeesDetails> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        if (CommonUtils.isNotEmpty(pageOfEmployeesDetailsDTO.getId())){
            queryWrapper.eq("id", pageOfEmployeesDetailsDTO.getId());
        }
        if (CommonUtils.isNotEmpty(pageOfEmployeesDetailsDTO.getName())){
            queryWrapper.like("name", pageOfEmployeesDetailsDTO.getName());
        }

        if (CommonUtils.isNotEmpty(pageOfEmployeesDetailsDTO.getAccountLine())){
            queryWrapper.like("account_line", pageOfEmployeesDetailsDTO.getAccountLine());
        }

        Integer userId = TokenUtils.getCurrentUserId();
        Integer companyId = managerDetailsService.getManagerDetailsByUserId(userId).getCompanyId();
        queryWrapper.eq("company_id", companyId);

        List<EmployeesDetails> employeesDetails = this.list(queryWrapper);

        List<EmployeesDetailsWorkVo> employeesDetailsWorkVos = new ArrayList<>();
        for (int i = 0; i < employeesDetails.size(); i++) {

            EmployeesDetailsWorkVo employeesDetailsWorkVo = new EmployeesDetailsWorkVo();

            employeesDetailsWorkVo.setEmployeesDetails(employeesDetails.get(i));

            QueryWrapper<EmployeesWorkExperience> qw2 = new QueryWrapper<>();
            qw2.eq("employees_id",employeesDetails.get(i).getId());
            List<EmployeesWorkExperience> list = employeesWorkExperienceService.list(qw2);
            employeesDetailsWorkVo.setEmployeesWorkExperiences(list);

            employeesDetailsWorkVos.add(employeesDetailsWorkVo);
        }

        Page pages = PageUtils.getPages((int) page.getCurrent(), (int) page.getSize(), employeesDetailsWorkVos);
        return R.ok(pages);
    }

    @Override
    public R search(SearchEmployees searchEmployees) {
        QueryWrapper<EmployeesDetails> qw = new QueryWrapper<>();
        if(CommonUtils.isNotEmpty(searchEmployees.getNumber())){
            qw.like("number",searchEmployees.getNumber());
        }
        if(CommonUtils.isNotEmpty(searchEmployees.getName())){
            qw.like("name",searchEmployees.getName());
        }
        if(CommonUtils.isNotEmpty(searchEmployees.getPhone())){
            qw.like("phone",searchEmployees.getPhone());
        }
        if(CommonUtils.isNotEmpty(searchEmployees.getEmail())){
            qw.like("email",searchEmployees.getEmail());
        }
        if(CommonUtils.isNotEmpty(searchEmployees.getPresetJobIds())){
            qw.like("preset_job_ids",searchEmployees.getPresetJobIds());
        }
        if(CommonUtils.isNotEmpty(searchEmployees.getCompanyId())){
            qw.eq("company_id",searchEmployees.getCompanyId());
        }
        if(CommonUtils.isNotEmpty(searchEmployees.getAddress2())){
            qw.like("address2",searchEmployees.getAddress2());
        }
        if(CommonUtils.isNotEmpty(searchEmployees.getAddress3())){
            qw.like("address3",searchEmployees.getAddress3());
        }
        if(CommonUtils.isNotEmpty(searchEmployees.getAccountLine())){
            qw.like("account_line",searchEmployees.getAccountLine());
        }
        if(CommonUtils.isNotEmpty(searchEmployees.getEducationBackground())){
            qw.like("education_background",searchEmployees.getEducationBackground());
        }
        if(CommonUtils.isNotEmpty(searchEmployees.getSex())){
            qw.eq("sex",searchEmployees.getSex());
        }

        List<EmployeesDetails> list = this.list(qw);

        List<EmployeesDetailsSkillVo> collect = list.stream().map(x -> {

            EmployeesDetailsSkillVo cusById = baseMapper.getCusById(x.getId());
            /** certified:員工認證準備 */
            Integer certified;
            //不属于公司就是个体户 1个体户 2工作室 3公司
            if (x.getCompanyId() == null) {
                certified = 1;
            } else {
                CompanyDetails cd = companyDetailsService.getById(x.getCompanyId());
                Boolean isCertified = cd.getIsValidate();
                if (isCertified == false) {
                    certified = 2;
                } else {
                    certified = 3;
                }
            }
            cusById.setCertified(certified);

            if (CommonUtils.isEmpty(x.getPresetJobIds()) || x.getPresetJobIds().equals("")) {
                cusById.setSkillTags(null);
            } else {
                String presetJobIds = x.getPresetJobIds();
                List<Skill> skills = new ArrayList<>();
                List<String> strings = Arrays.asList(presetJobIds.split(" "));
                for (int i = 0; i < strings.size(); i++) {
                    Skill skill = new Skill();
                    skill.setJobId(Integer.parseInt(strings.get(i)));
                    skill.setContend(sysJobContendService.getById(Integer.parseInt(strings.get(i))).getContend());
                    skills.add(skill);
                }
                cusById.setSkillTags(skills);
            }
            return cusById;
        }).collect(Collectors.toList());
        return R.ok(collect);
    }

    @Override
    public R getPersonal(Page page,PageOfEmployeesDTO pageOfEmployeesDTO) {
        List<EmployeesDetails> list =  baseMapper.getPersonal(pageOfEmployeesDTO);
        List<EmployeesDetailsSkillVo> collect = list.stream().map(x -> {

            EmployeesDetailsSkillVo cusById = baseMapper.getCusById(x.getId());
            /** certified:員工認證準備 */
            Integer certified;
            //不属于公司就是个体户 1个体户 2工作室 3公司
            if (x.getCompanyId() == null) {
                certified = 1;
            } else {
                CompanyDetails cd = companyDetailsService.getById(x.getCompanyId());
                Boolean isCertified = cd.getIsValidate();
                if (isCertified == false) {
                    certified = 2;
                } else {
                    certified = 3;
                }
            }
            cusById.setCertified(certified);

            if (CommonUtils.isEmpty(x.getPresetJobIds()) || x.getPresetJobIds().equals("")) {
                cusById.setSkillTags(null);
            } else {
                String presetJobIds = x.getPresetJobIds();
                List<Skill> skills = new ArrayList<>();
                List<String> strings = Arrays.asList(presetJobIds.split(" "));
                for (int i = 0; i < strings.size(); i++) {
                    Skill skill = new Skill();
                    skill.setJobId(Integer.parseInt(strings.get(i)));
                    skill.setContend(sysJobContendService.getById(Integer.parseInt(strings.get(i))).getContend());
                    skills.add(skill);
                }
                cusById.setSkillTags(skills);
            }
            return cusById;
        }).collect(Collectors.toList());
        Page pages = PageUtils.getPages((int) page.getCurrent(), (int) page.getSize(), collect);
        return R.ok(pages);
    }

    @Override
    public R getCollect(Integer empId) {
        Integer collectionVolume = baseMapper.getCollectionVolume(empId);
        return R.ok(collectionVolume);
    }

    @Override
    public R getGroupByEmpId(Integer employeesId) {
        QueryWrapper<GroupEmployees> qw = new QueryWrapper<>();
        qw.eq("employees_id",employeesId);
        List<GroupEmployees> list = groupEmployeesService.list(qw);
        List<GroupDetails> collect = list.stream().map(x -> {
            GroupDetails byId = groupDetailsService.getById(x.getGroupId());
            return byId;
        }).collect(Collectors.toList());
        return R.ok(collect);
    }

    @Override
    public EmployeesDetails getByUserId(Integer userId) {
        QueryWrapper<EmployeesDetails> qw = new QueryWrapper<>();
        qw.eq("user_id",userId);
        EmployeesDetails one = this.getOne(qw);
        return one;
    }

    @Override
    public String setHeader(MultipartFile image) {
        String res = "";

        LocalDateTime now = LocalDateTime.now();
        String nowString = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String catalogue = CommonConstants.HK_EMPLOYEES_HEAD_ABSTRACT_PATH_PREFIX_PROV;
        String type = image.getOriginalFilename().split("\\.")[1];
        String fileAbstractPath = catalogue + "/" + nowString+"."+ type;

        try {
            ossClient.putObject(bucketName, fileAbstractPath, new ByteArrayInputStream(image.getBytes()));
            res = urlPrefix + fileAbstractPath;
        } catch (Exception e) {
            e.printStackTrace();
            return "error upload";
        }

        return res;
    }

    @Override
    public R addEmp(String name,
                    Boolean sex,
                    LocalDate dateOfBirth,
                    String idCard,
                    String address1,
                    String address2,
                    String address3,
                    String address4,
                    Float lng,
                    Float lat,
                    String educationBackground,
                    String phonePrefix,
                    String phone,
                    String accountLine,
                    String describes,
                    String workYear,
                    List<EmployeesWorkExperienceDTO> workExperiencesDTO,
                    List<Integer> jobIds,
                    MultipartFile image) {
        //先保存User
        User user = new User();
        String ss = String.valueOf(System.currentTimeMillis());
        user.setName(name);
        user.setNumber("c"+ss);
        user.setDeptId(5);
        user.setLastReviserId(TokenUtils.getCurrentUserId());
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        Integer maxUserId = 0;
        synchronized (this) {
            userService.save(user);
            maxUserId = ((User) CommonUtils.getMaxId("sys_user", userService)).getId();
        }

        EmployeesDetails employeesDetails = new EmployeesDetails();
        QueryWrapper<CompanyDetails> wrComp=new QueryWrapper<>();
        wrComp.inSql("id","select id from company_details where user_id=" + TokenUtils.getCurrentUserId());
        CompanyDetails one = companyDetailsService.getOne(wrComp);
        employeesDetails.setUserId(maxUserId);
        employeesDetails.setNumber(null);
        employeesDetails.setName(name);
        employeesDetails.setSex(sex);
        employeesDetails.setDateOfBirth(dateOfBirth);
        employeesDetails.setIdCard(idCard);
        employeesDetails.setAddress1(address1);
        employeesDetails.setAddress2(address2);
        employeesDetails.setAddress3(address3);
        employeesDetails.setAddress4(address4);

        /** 2021/1/14 su 新增存放地址經緯度 **/
        employeesDetails.setLng(lng.toString());
        employeesDetails.setLat(lat.toString());
        /** 2021/1/14 su 新增存放地址經緯度 **/

        employeesDetails.setEducationBackground(educationBackground);
        employeesDetails.setPhonePrefix(phonePrefix);
        employeesDetails.setPhone(phone);
        employeesDetails.setAccountLine(accountLine);
        employeesDetails.setDescribes(describes);
        employeesDetails.setWorkYear(workYear);
        employeesDetails.setStarRating(3.0f); //新增的员工默认为三星级，中等好评
        employeesDetails.setBlacklistFlag(false);

        employeesDetails.setUpdateTime(LocalDateTime.now());
        employeesDetails.setCreateTime(LocalDateTime.now());
        employeesDetails.setCompanyId(one.getId().toString());
        employeesDetails.setLastReviserId(TokenUtils.getCurrentUserId());
        Integer maxEmployeesId = 0;
        Object savePoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
        try {
            synchronized (this){
                this.save(employeesDetails);
                maxEmployeesId = ((EmployeesDetails) CommonUtils.getMaxId("employees_details", this)).getId();
            }
            /**
             * 順便建個員工推廣的表記錄
             */
            EmployeesPromotion employeesPromotion = new EmployeesPromotion();
            employeesPromotion.setEmployeesId(maxEmployeesId);
            employeesPromotionService.save(employeesPromotion);
            /**
             * 工作经验保存
             */
            employeesWorkExperienceService.saveEmployeesWorkExperience(workExperiencesDTO, maxEmployeesId);
            /**
             * 可工作内容设置,工作内容不为空就可以
             */
            if (CommonUtils.isNotEmpty(jobIds))
                employeesCalendarService.setJobs(new SetEmployeesJobsDTO(jobIds, maxEmployeesId));
//                    employeesJobsService.setJobIdsByEmployeesId(employeesDetailsDTO.getJobIds(), maxEmployeesId);
        } catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savePoint);
            return R.failed("添加失敗");
        }
        return R.ok(null, "添加成功");
    }

    @Override
    public R getEmployeesByIds(List<Integer> ids) {
        List<EmployeesDetailsSkillVo> collect = ids.stream().map(x -> {
            EmployeesDetailsSkillVo byId = baseMapper.getCusById(x);
            /** certified:員工認證準備 */
            Integer certified;
            //不属于公司就是个体户 1个体户 2工作室 3公司
            if(byId.getCompanyId()==null){
                certified = 1;
            }else {
                CompanyDetails cd = companyDetailsService.getById(byId.getCompanyId());
                Boolean isCertified = cd.getIsValidate();
                if(isCertified==false){
                    certified = 2;
                }else {
                    certified = 3;
                }
            }

            if(CommonUtils.isEmpty(byId.getPresetJobIds())||byId.getPresetJobIds().equals("")){
                byId.setSkillTags(null);
                byId.setCertified(certified);
            }else {
                String presetJobIds = byId.getPresetJobIds();
                List<Skill> skills = new ArrayList<>();
                List<String> strings = Arrays.asList(presetJobIds.split(" "));
                for (int i = 0; i < strings.size(); i++) {
                    Skill skill = new Skill();
                    skill.setJobId(Integer.parseInt(strings.get(i)));
                    skill.setContend(sysJobContendService.getById(Integer.parseInt(strings.get(i))).getContend());
                    skills.add(skill);
                }
                byId.setSkillTags(skills);
                byId.setCertified(certified);
            }
            return byId;
        }).collect(Collectors.toList());
        return R.ok(collect);
    }

    @Override
    public R updateEmpSkill(EmployeesSkillDTO dto) {
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
        StringBuilder sb2 = new StringBuilder();
        dto.getSkills().forEach(job->{
            sb.append(job.getJobId());
            sb.append(" ");
            sb2.append(job.getPrice());
            sb2.append(" ");
        });
        String jobIdsString = new String(sb).trim();
        String jobPrice = new String(sb2).trim();
        employeesDetailsService.setPresetJobIds(jobIdsString, dto.getEmployeesId());
        employeesDetailsService.setPresetJobPrice(jobPrice,dto.getEmployeesId());
        return R.ok(null, "已設置預設鐘點工工作內容");
    }

    @Override
    public void setPresetJobPrice(String price, Integer empId) {
        baseMapper.setPresetJobPrice(price, empId);
    }

    @Override
    public R getEmpSkill(Integer empId) {
        EmployeesDetails byId = this.getById(empId);
        ArrayList<SkillPriceDTO> skillPriceDTOS = new ArrayList<>();
        if(byId.getPresetJobIds()==null){
            return R.ok(null);
        }
        List<String> jobs = Arrays.asList(byId.getPresetJobIds().split(" "));
        List<String> price = Arrays.asList(byId.getJobPrice().split(" "));
        for (int i = 0; i < jobs.size(); i++) {
            SkillPriceDTO skillPrice = new SkillPriceDTO();
            skillPrice.setJobId(Integer.parseInt(jobs.get(i)));
            skillPrice.setJob(sysJobContendService.getById(Integer.parseInt(jobs.get(i))).getContend());
            skillPrice.setPrice(price.get(i));
            skillPriceDTOS.add(skillPrice);
        }
        return R.ok(skillPriceDTOS);
    }

    @Override
    public R cusPage6(Page page, PageOfEmployeesDetailsDTO pageOfEmployeesDetailsDTO, Integer manId) {
        List<Integer> employeesIdsByManagerId = groupEmployeesService.getEmployeesIdsByManagerId(manId);
        List<EmployeesDetails> collect = employeesIdsByManagerId.stream().map(x -> {
            return this.getById(x);
        }).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(collect)){
            return R.ok(null);
        }
        List<EmployeesDetails> employeesDetails = null;
        if(StringUtils.isNotBlank(pageOfEmployeesDetailsDTO.getName())){
            employeesDetails  = this.search(pageOfEmployeesDetailsDTO.getName(), collect);
        }else{
            employeesDetails = collect;
        }

        List<EmployeesDetailsPOJO> edps = employeesDetails.stream().map(ed->{

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

            EmployeesDetailsPOJO edp = new EmployeesDetailsPOJO(ed);
            //技能标签和是否已认证
            edp.setCertified(certified);
            List<SysJobContend> jobs = new ArrayList<>();
            List<Integer> jobIds = CommonUtils.stringToList(ed.getPresetJobIds());
            if (!jobIds.isEmpty()) jobs = sysJobContendService.listByIds(jobIds);
            edp.setSkillTags(jobs);
            return edp;
        }).collect(Collectors.toList());

        Page pages = PageUtils.getPages((int) page.getCurrent(), (int) page.getSize(), edps);
        return R.ok(pages);
    }

    public List<EmployeesDetails> search(String name,List<EmployeesDetails> list){
        List<EmployeesDetails> results = new ArrayList();
        Pattern pattern = Pattern.compile(name);
        for(int i=0; i < list.size(); i++){
            Matcher matcher = pattern.matcher(((EmployeesDetails)list.get(i)).getName());
            if(matcher.find()){
                results.add(list.get(i));
            }
        }
        return results;
    }

}
