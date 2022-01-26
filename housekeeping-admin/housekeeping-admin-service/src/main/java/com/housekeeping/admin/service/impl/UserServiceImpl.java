package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.*;
import com.housekeeping.admin.entity.*;
import com.housekeeping.admin.mapper.UserMapper;
import com.housekeeping.admin.service.*;
import com.housekeeping.admin.vo.SmsDTO;
import com.housekeeping.common.entity.HkUser;
import com.housekeeping.common.sms.SendMessage;
import com.housekeeping.common.utils.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private ICompanyDetailsService companyDetailsService;
    @Resource
    private ICustomerDetailsService customerDetailsService;
    @Resource
    private CustomerAddressServiceImpl customerAddressService;
    @Resource
    private CompanyPromotionServiceImpl companyPromotionService;
    @Resource
    private IAddressCodingService addressCodingService;
    @Resource
    private ManagerDetailsService managerDetailsService;
    @Resource
    private EmployeesDetailsService employeesDetailsService;
    @Resource
    private IManagerMenuService managerMenuService;
    @Resource
    private ISysMenuService sysMenuService;
    @Resource
    private IEmployeesPromotionService employeesPromotionService;
    @Resource
    private IGroupDetailsService groupDetailsService;
    @Resource
    private ICompanyAdvertisingService companyAdvertisingService;
    @Resource
    private IUserService userService;
    @Resource
    private IEmployeesWorkExperienceService employeesWorkExperienceService;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private TokenService tokenService;
    @Resource
    private InvitationService invitationService;

    @Override
    public User getUserByPhone(String phonePrefix, String phone, Integer deptId) {
        QueryWrapper qr = new QueryWrapper();
        qr.eq("phone_prefix", phonePrefix);
        qr.eq("dept_id", deptId);
        qr.eq("phone", phone);
        User res = baseMapper.selectOne(qr);

        return res;
    }

    public User getUserByPhoneAndPrefix(String phonePrefix, String phone) {
        QueryWrapper qr = new QueryWrapper();
        qr.eq("phone_prefix", phonePrefix);
        qr.eq("phone", phone);
        qr.in("dept_id",2,6);
        User res = baseMapper.selectOne(qr);
        return res;
    }

    public User getCusByPhoneAndPrefix(String phonePrefix, String phone) {
        QueryWrapper qr = new QueryWrapper();
        qr.eq("phone_prefix", phonePrefix);
        qr.eq("phone", phone);
        qr.in("dept_id",3);
        User res = baseMapper.selectOne(qr);
        return res;
    }

    @Override
    public R checkData(String data, String type,Integer deptId) {
        //886手机号处理加0
        if(type.equals("886")){
            if(!data.startsWith("0")){
                data = "0"+data;
            }
        }

        QueryWrapper qr = new QueryWrapper();
        qr.eq("phone_prefix",type);
        qr.eq("phone", data);
        qr.eq("dept_id",deptId);
        if (this.userMapper.selectCount(qr) == 0) {
            return R.ok("手机号或者邮箱不存在,可以注册");
        } else {
            return R.failed("手機號已注冊");
        }
    }

    @Override
    public R sendRegisterMSMessage(String phonePrefix, String phone, Integer deptId) throws Exception {
        //886手机号处理加0
        if(phonePrefix.equals("886")){
            if(!phone.startsWith("0")){
                phone = "0"+phone;
            }
        }

        User hkUser = this.getUserByPhone(phonePrefix, phone, deptId);
        if (CommonUtils.isEmpty(hkUser)) {
            //生成随即验证码
            String code = CommonUtils.getRandomSixCode();
            String key = CommonConstants.REGISTER_KEY_BY_PHONE + "_" + deptId + "_+" + phonePrefix + "_" + phone;
            //存入redis
            redisUtils.set(key, code);
            redisUtils.expire(key, CommonConstants.VALID_TIME_MINUTES * 60);//三十分鐘
            //发送短信
            String[] params = new String[]{code, CommonConstants.VALID_TIME_MINUTES.toString()};
            SendMessage.sendMessage(phonePrefix, phone, params);
            return R.ok("成功發送短信");
        } else {
            return R.failed("該手機號已註冊");
        }
    }

    @Override
    @Transactional
    public R saveEmp(RegisterCompanyDTO dto) {
        //886手机号处理加0
        if(dto.getPhonePrefix().equals("886")){
            if(!dto.getPhone().startsWith("0")){
                dto.setPhone("0"+dto.getPhone());
            }
        }
        if (CommonUtils.isNotEmpty(dto)) {
            if (CommonUtils.isNotEmpty(dto.getCode())) {
                //判斷redis中的驗證碼是否正確
                if (dto.getCode().equals(redisUtils.get(CommonConstants.REGISTER_KEY_BY_PHONE + "_" + 2 + "_+" + dto.getPhonePrefix() + "_" + dto.getPhone()))) {
                    if (dto.getPassword().equals(dto.getRePassword())) {
                        //先保存User
                        User user = new User();
                        String s = String.valueOf(System.currentTimeMillis());
                        user.setNumber("c"+s);
                        user.setDeptId(2);
                        user.setName(dto.getName());
                        user.setPhonePrefix(dto.getPhonePrefix());
                        user.setPhone(dto.getPhone());
                        user.setPassword(DESEncryption.getEncryptString(dto.getPassword()));
                        user.setLastReviserId(TokenUtils.getCurrentUserId());
                        user.setCreateTime(LocalDateTime.now());
                        user.setUpdateTime(LocalDateTime.now());
                        Integer maxUserId = 0;
                        synchronized (this) {
                            this.save(user);
                            maxUserId = ((User) CommonUtils.getMaxId("sys_user", this)).getId();
                        }
                        //在保存後台管理員詳情信息
                        CompanyDetails companyDetails = new CompanyDetails();
                        companyDetails.setNoCertifiedCompany(dto.getName());
                        companyDetails.setUserId(maxUserId);
                        companyDetails.setIsValidate(false);
                        companyDetails.setLastReviserId(TokenUtils.getCurrentUserId());
                        companyDetails.setCreateTime(LocalDateTime.now());
                        companyDetails.setUpdateTime(LocalDateTime.now());
                        companyDetails.setServiceHotline(dto.getPhonePrefix()+dto.getPhone());
                        if(StringUtils.isBlank(dto.getHeadUrl())){
                            companyDetails.setLogoUrl("https://test-live-video.oss-cn-shanghai.aliyuncs.com/HKFile/ImPhoto/userId=/20210508104224.png");
                        }else{
                            companyDetails.setLogoUrl(dto.getHeadUrl());
                        }
                        companyDetails.setIsValidate(dto.getIsValidate());
                        companyDetails.setServiceHotlinePrefix(dto.getPhonePrefix());
                        companyDetails.setServiceHotline(dto.getPhone());
                        companyDetailsService.save(companyDetails);
                        /*
                        公司推廣
                        * */
                        Integer maxCompanyId = ((CompanyDetails) CommonUtils.getMaxId("company_details", companyDetailsService)).getId();
                        CompanyPromotion companyPromotion = new CompanyPromotion();
                        companyPromotion.setCompanyId(maxCompanyId);
                        companyPromotionService.save(companyPromotion);

                        //保存邀请推广信息
                        if(CommonUtils.isNotEmpty(dto.getInvitee())){
                            Invitation invitation = new Invitation();
                            invitation.setInvitee(dto.getInvitee());
                            invitation.setInvitees(maxUserId);
                            invitationService.save(invitation);
                        }
                    } else {
                        return R.failed("两次密码不一致");
                    }
                } else {
                    return R.failed("验证码错误");
                }
            } else {
                return R.failed("验证码为空");
            }
        }
        HkUser hkUser = baseMapper.byPhoneLogin(dto.getPhone(), 2);
        hkUser.setAuthType(1);
        String token = tokenService.getToken(hkUser);
        return R.ok(token,"创建公司账户成功");
    }

    @Override
    public R saveCus(RegisterCustomerDTO dto) {

        //886手机号处理加0
        if(dto.getPhonePrefix().equals("886")){
            if(!dto.getPhone().startsWith("0")){
                dto.setPhone("0"+dto.getPhone());
            }
        }
        if (CommonUtils.isNotEmpty(dto)) {
            if (CommonUtils.isNotEmpty(dto.getCode())) {
                //判斷redis中的驗證碼是否正確
                if (dto.getCode().equals(redisUtils.get(CommonConstants.REGISTER_KEY_BY_PHONE + "_" + 3 + "_+" + dto.getPhonePrefix() + "_" + dto.getPhone()))) {
                    if (dto.getPassword().equals(dto.getRePassword())) {
                        User user = new User();
                        user.setNumber(String.valueOf(System.currentTimeMillis()));
                        user.setDeptId(3);
                        user.setName(dto.getName());
                        user.setPhonePrefix(dto.getPhonePrefix());
                        user.setPhone(dto.getPhone());
                        user.setPassword(DESEncryption.getEncryptString(dto.getPassword()));
                        user.setLastReviserId(TokenUtils.getCurrentUserId());
                        user.setCreateTime(LocalDateTime.now());
                        user.setUpdateTime(LocalDateTime.now());
                        Integer maxUserId = 0;
                        synchronized (this) {
                            this.save(user);
                            maxUserId = ((User) CommonUtils.getMaxId("sys_user", this)).getId();
                        }
                        //在保存後台管理員詳情信息
                        CustomerDetails customerDetails = new CustomerDetails();
                        customerDetails.setName(dto.getName());
                        customerDetails.setPhonePrefix(dto.getPhonePrefix());
                        customerDetails.setPhone(dto.getPhone());
                        customerDetails.setUserId(maxUserId);
                        customerDetails.setBlacklistFlag(false);//默认值false
                        customerDetails.setLastReviserId(TokenUtils.getCurrentUserId());
                        customerDetails.setCreateTime(LocalDateTime.now());
                        customerDetails.setUpdateTime(LocalDateTime.now());
                        if(StringUtils.isBlank(dto.getHeadUrl())){
                            customerDetails.setHeadUrl("https://test-live-video.oss-cn-shanghai.aliyuncs.com/HKFile/ImPhoto/userId=/20210705152934.png");
                        }else{
                            customerDetails.setHeadUrl(dto.getHeadUrl());
                        }
                        customerDetailsService.save(customerDetails);

                        //把地址存為經緯度
                        CustomerAddress customerAddress = new CustomerAddress();

                        QueryWrapper queryWrapper = new QueryWrapper();
                        queryWrapper.eq("user_id", maxUserId);
                        CustomerDetails newCustomerDetails = customerDetailsService.getOne(queryWrapper);

                        customerAddress.setCustomerId(newCustomerDetails.getId());
                        customerAddress.setIsDefault(true);
                        customerAddress.setName(dto.getName());
                        customerAddress.setAddress(dto.getAddress());
                        customerAddress.setLng(dto.getLng());
                        customerAddress.setLat(dto.getLat());
                        customerAddress.setPhone(dto.getPhone());
                        customerAddress.setPhonePrefix(dto.getPhonePrefix());
                        customerAddressService.save(customerAddress);

                        //保存邀请推广信息
                        if(CommonUtils.isNotEmpty(dto.getInvitee())){
                            Invitation invitation = new Invitation();
                            invitation.setInvitee(dto.getInvitee());
                            invitation.setInvitees(maxUserId);
                            invitationService.save(invitation);
                        }
                    } else {
                        return R.failed("兩次密碼不一致");
                    }
                } else {
                    return R.failed("驗證碼錯誤");
                }
            } else {
                return R.failed("驗證碼為空");
            }
        }
        HkUser hkUser = baseMapper.byPhoneLogin(dto.getPhone(), 3);
        hkUser.setAuthType(1);
        String token = tokenService.getToken(hkUser);
        return R.ok(token,"創建客戶成功");
    }

    @Override
    public R saveAdmin(RegisterAdminDTO dto) {
        if (CommonUtils.isNotEmpty(dto)) {
            if (CommonUtils.isNotEmpty(dto.getCode())) {
                //判斷redis中的驗證碼是否正確
                if (dto.getCode().equals(redisUtils.get(CommonConstants.REGISTER_KEY_BY_PHONE + "_" + 1 + "_+" + dto.getPhonePrefix() + "_" + dto.getPhone()))) {
                    if (dto.getPassword().equals(dto.getRePassword())) {
                        User user = new User();
                        user.setNumber(String.valueOf(System.currentTimeMillis()));
                        user.setDeptId(1);
                        user.setName(dto.getName());
                        user.setPhonePrefix(dto.getPhonePrefix());
                        user.setPhone(dto.getPhone());
                        user.setPassword(DESEncryption.getEncryptString(dto.getPassword()));
                        user.setLastReviserId(TokenUtils.getCurrentUserId());
                        user.setCreateTime(LocalDateTime.now());
                        user.setUpdateTime(LocalDateTime.now());
                        Integer maxUserId = 0;
                        synchronized (this) {
                            this.save(user);
                            maxUserId = ((User) CommonUtils.getMaxId("sys_user", this)).getId();
                        }
                        //再保存後台管理員詳情信息

                    } else {
                        return R.failed("两次密码不一致");
                    }
                } else {
                    return R.failed("验证码错误");
                }
            } else {
                return R.failed("验证码为空");
            }
        }
        return R.ok("创建平台管理员成功");
    }


    @Override
    public R getAllUser(IPage<User> page, AdminPageDTO adminPageDTO,Integer deptId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("dept_id",deptId);
        queryWrapper.orderByDesc("id");
        if (CommonUtils.isNotEmpty(adminPageDTO.getId())){
            queryWrapper.eq("id", adminPageDTO.getId());
        }
        if (CommonUtils.isNotEmpty(adminPageDTO.getNumber())){
            queryWrapper.like("number", adminPageDTO.getNumber());
        }
        if (CommonUtils.isNotEmpty(adminPageDTO.getNickname())){
            queryWrapper.like("nickname", adminPageDTO.getNickname());
        }
        if (CommonUtils.isNotEmpty(adminPageDTO.getName())){
            queryWrapper.like("name", adminPageDTO.getName());
        }
        if (CommonUtils.isNotEmpty(adminPageDTO.getEmail())){
            queryWrapper.like("email", adminPageDTO.getEmail());
        }
        if (CommonUtils.isNotEmpty(adminPageDTO.getPhone())){
            queryWrapper.like("phone", adminPageDTO.getPhone());
        }
        IPage<User> companyDetailsIPage = baseMapper.selectPage(page, queryWrapper);
        List<User> records = companyDetailsIPage.getRecords();
        records.forEach(x ->{
            String decryptString = DESEncryption.getDecryptString(x.getPassword());
            x.setPassword(decryptString);
        });
        return R.ok(companyDetailsIPage, "查詢成功");
    }

    @Override
    public User getUserByIdAndDept(Integer id, int i) {
        return baseMapper.getUserByIdAndDept(id,i);
    }

    @Override
    public R page(Page page, PageOfUserDTO dto) {
        QueryWrapper qw = new QueryWrapper();
        if (CommonUtils.isNotEmpty(dto.getNumber())){
            qw.eq("number", dto.getNumber());
        }
        if (CommonUtils.isNotEmpty(dto.getNickname())){
            qw.like("nikename", dto.getNickname());
        }
        if (CommonUtils.isNotEmpty(dto.getName())){
            qw.like("name", dto.getName());
        }
        if (CommonUtils.isNotEmpty(dto.getDateOfBirthStart())){
            qw.ge("date_of_birth", dto.getDateOfBirthStart());
        }
        if (CommonUtils.isNotEmpty(dto.getDateOfBirthEnd())){
            qw.le("date_of_birth", dto.getDateOfBirthEnd());
        }
        if (CommonUtils.isNotEmpty(dto.getPhonePrefix()) && CommonUtils.isNotEmpty(dto.getPhone())){
            qw.eq("phone_prefix", dto.getPhonePrefix());
            qw.eq("phone", dto.getPhone());
        }
        if (CommonUtils.isNotEmpty(dto.getEmail())){
            qw.eq("email", dto.getEmail());
        }
        if (CommonUtils.isNotEmpty(dto.getDeptId())){
            qw.eq("dept_id", dto.getDeptId());
        }
        if (CommonUtils.isNotEmpty(dto.getCreateTimeStart())){
            qw.ge("create_time", dto.getCreateTimeStart());
        }
        if (CommonUtils.isNotEmpty(dto.getCreateTimeEnd())){
            qw.le("create_time", dto.getCreateTimeEnd());
        }
        if (CommonUtils.isNotEmpty(dto.getUpdateTimeStart())){
            qw.ge("update_time", dto.getUpdateTimeStart());
        }
        if (CommonUtils.isNotEmpty(dto.getUpdateTimeEnd())){
            qw.le("update_time", dto.getUpdateTimeEnd());
        }
        if (CommonUtils.isNotEmpty(dto.getLastReviserId())){
            qw.eq("last_reviser_id", dto.getLastReviserId());
        }
        return R.ok(this.page(page, qw), "獲取成功！");
    }

    @Override
    public R add1(AdminAdd1DTO dto) {
        //886手机号处理加0
        if(dto.getPhonePrefix().equals("886")){
            if(!dto.getPhone().startsWith("0")){
                dto.setPhone("0"+dto.getPhone());
            }
        }
        Boolean existPhone = false;
        Integer deptId = dto.getDeptId();
        if(deptId.equals(2)){
            existPhone = this.isExistPhone2(dto.getPhonePrefix(), dto.getPhone());
        }
        if(deptId.equals(6)){
            existPhone = this.isExistPhone2(dto.getPhonePrefix(), dto.getPhone());
        }
        if(deptId.equals(1)||deptId.equals(3)){
            existPhone = this.isExistPhone(dto.getPhonePrefix(), dto.getPhone(),dto.getDeptId());
        }
        if (existPhone){
            return R.failed(null, "該手機號已存在，請勿重新註冊");
        }
        LocalDateTime now = LocalDateTime.now();
        Integer mineUserId = TokenUtils.getCurrentUserId();
        User user = new User();
        user.setDeptId(dto.getDeptId());
        user.setName(dto.getName());
        user.setNickname(dto.getNickName());
        user.setPhonePrefix(dto.getPhonePrefix());
        user.setPhone(dto.getPhone());
        if (CommonUtils.isNotEmpty(dto.getEmail())){
            user.setEmail(dto.getEmail());
        }
        user.setPassword(DESEncryption.getEncryptString(dto.getPassword()));
        user.setCreateTime(now);
        user.setUpdateTime(now);
        user.setLastReviserId(mineUserId);
        Integer maxUserId = 0;
        synchronized (this) {
            this.save(user);
            maxUserId = ((User) CommonUtils.getMaxId("sys_user", this)).getId();
        }
        if (dto.getDeptId() == 1){
            //管理员，不存储任何详细信息
        }else if (dto.getDeptId() == 2){
            //公司账户
            CompanyDetails company = new CompanyDetails();
            company.setLogoUrl("https://test-live-video.oss-cn-shanghai.aliyuncs.com/HKFile/ImPhoto/userId=/20210508104224.png");
            company.setUserId(maxUserId);
            company.setIsValidate(false);
            company.setCreateTime(now);
            company.setUpdateTime(now);
            company.setLastReviserId(mineUserId);
            Integer maxCompanyId = 0;
            synchronized (this){
                companyDetailsService.save(company);
                maxCompanyId = ((CompanyDetails) CommonUtils.getMaxId("company_details", companyDetailsService)).getId();
            }
            //公司推广
            CompanyPromotion companyPromotion = new CompanyPromotion();
            companyPromotion.setCompanyId(maxCompanyId);
            companyPromotionService.save(companyPromotion);

        }else if (dto.getDeptId() == 3){
            //家庭账户
            CustomerDetails customer = new CustomerDetails();
            customer.setName(dto.getName());
            customer.setUserId(maxUserId);
            customer.setPhonePrefix(dto.getPhonePrefix());
            customer.setPhone(dto.getPhone());
            if (CommonUtils.isNotEmpty(dto.getEmail())){
                customer.setEmail(dto.getEmail());
            }
            customer.setCreateTime(now);
            customer.setUpdateTime(now);
            customer.setLastReviserId(mineUserId);
            customer.setHeadUrl("https://test-live-video.oss-cn-shanghai.aliyuncs.com/HKFile/ImPhoto/userId=/20210705152934.png");
            customerDetailsService.save(customer);
        }else if(dto.getDeptId() == 6){
            //在保存公司信息
            CompanyDetails companyDetails = new CompanyDetails();
            companyDetails.setNoCertifiedCompany(dto.getName());
            companyDetails.setUserId(maxUserId);
            companyDetails.setIsPersonal(true);
            companyDetails.setIsValidate(false);
            companyDetails.setLastReviserId(TokenUtils.getCurrentUserId());
            companyDetails.setCreateTime(LocalDateTime.now());
            companyDetails.setUpdateTime(LocalDateTime.now());
            companyDetails.setLogoUrl("https://test-live-video.oss-cn-shanghai.aliyuncs.com/HKFile/ImPhoto/userId=/20210508104224.png");
            companyDetailsService.save(companyDetails);

            //公司推廣
            Integer maxCompanyId = ((CompanyDetails) CommonUtils.getMaxId("company_details", companyDetailsService)).getId();
            CompanyPromotion companyPromotion = new CompanyPromotion();
            companyPromotion.setCompanyId(maxCompanyId);
            companyPromotionService.save(companyPromotion);

            //保存员工信息
            EmployeesDetails employeesDetails = new EmployeesDetails();
            employeesDetails.setCompanyId(maxCompanyId.toString());
            employeesDetails.setPhonePrefix(dto.getPhonePrefix());
            employeesDetails.setPhone(dto.getPhone());
            employeesDetails.setUserId(maxUserId);
            employeesDetails.setName(dto.getName());

            employeesDetails.setLat("25.148998");
            employeesDetails.setLng("121.77368");

            employeesDetails.setStarRating(3.0f); //新增的员工默认为三星级，中等好评
            employeesDetails.setBlacklistFlag(false);
            employeesDetails.setNumberOfOrders(0); //默认接单次数为0

            employeesDetails.setUpdateTime(LocalDateTime.now());
            employeesDetails.setCreateTime(LocalDateTime.now());
            employeesDetails.setLastReviserId(TokenUtils.getCurrentUserId());

            //头像
            employeesDetails.setHeadUrl("https://test-live-video.oss-cn-shanghai.aliyuncs.com/HKFile/ImPhoto/userId=/20210508103930.png");

            Integer maxEmployeesId;
            synchronized (this){
                employeesDetailsService.save(employeesDetails);
                maxEmployeesId = ((EmployeesDetails) CommonUtils.getMaxId("employees_details", employeesDetailsService)).getId();
            }

            // 順便建個員工推廣的表記錄
            EmployeesPromotion employeesPromotion = new EmployeesPromotion();
            employeesPromotion.setEmployeesId(maxEmployeesId);
            employeesPromotionService.save(employeesPromotion);
        }
        return R.ok("賬戶添加成功");
    }

    @Override
    public R add2(AdminAdd2DTO dto) {
        LocalDateTime now = LocalDateTime.now();
        Integer mineUserId = TokenUtils.getCurrentUserId();
        User user = new User();
        user.setDeptId(dto.getDeptId());
        user.setName(dto.getName());
        user.setNickname(dto.getNickName());
        if (CommonUtils.isNotEmpty(dto.getPhonePrefix()) && CommonUtils.isNotEmpty(dto.getPhone())){
            user.setPhonePrefix(dto.getPhonePrefix());
            user.setPhone(dto.getPhone());
        }
        if (CommonUtils.isNotEmpty(dto.getEmail())){
            user.setEmail(dto.getEmail());
        }
        user.setCreateTime(now);
        user.setUpdateTime(now);
        user.setLastReviserId(mineUserId);
        Integer maxUserId = 0;
        synchronized (this) {
            this.save(user);
            maxUserId = ((User) CommonUtils.getMaxId("sys_user", this)).getId();
        }
        if (dto.getDeptId() == 4){
            //經理賬戶
            ManagerDetails manager = new ManagerDetails();
            manager.setUserId(maxUserId);
            manager.setCompanyId(dto.getCompanyId());
            manager.setName(dto.getName());
            if (CommonUtils.isNotEmpty(dto.getPhonePrefix()) && CommonUtils.isNotEmpty(dto.getPhone())){
                manager.setPhonePrefix(dto.getPhonePrefix());
                manager.setPhone(dto.getPhone());
            }
            if (CommonUtils.isNotEmpty(dto.getEmail())){
                manager.setEmail(dto.getEmail());
            }
            manager.setCreateTime(now);
            manager.setUpdateTime(now);
            manager.setLastReviserId(mineUserId);
            Integer maxManagerId = 0;
            synchronized (this){
                managerDetailsService.save(manager);
                maxManagerId = ((ManagerDetails) CommonUtils.getMaxId("manager_details", managerDetailsService)).getId();
            }
            /** 2021-02-07 su新增 增加经理的同时，给予所有菜单权限 */
            List<SysMenu> sysMenuList = sysMenuService.list();
            Integer finalMaxManagerId = maxManagerId;
            List<ManagerMenu> managerMenuList = sysMenuList.stream().map(x -> {
                ManagerMenu managerMenu = new ManagerMenu();
                managerMenu.setManagerId(finalMaxManagerId);
                managerMenu.setMenuId(x.getId());
                return managerMenu;
            }).collect(Collectors.toList());
            managerMenuService.saveBatch(managerMenuList);

        }else if (dto.getDeptId() == 5){
            //保潔員賬戶
            EmployeesDetails employees = new EmployeesDetails();

            employees.setLat("25.148998");
            employees.setLng("121.77368");

            employees.setUserId(maxUserId);
            employees.setCompanyId(dto.getCompanyId().toString());
            employees.setName(dto.getName());
            if (CommonUtils.isNotEmpty(dto.getPhonePrefix()) && CommonUtils.isNotEmpty(dto.getPhone())){
                employees.setPhonePrefix(dto.getPhonePrefix());
                employees.setPhone(dto.getPhone());
            }
            if (CommonUtils.isNotEmpty(dto.getEmail())){
                employees.setEmail(dto.getEmail());
            }
            employees.setCreateTime(now);
            employees.setUpdateTime(now);
            employees.setLastReviserId(mineUserId);
            Integer maxEmployeesId = 0;
            synchronized (this){
                employeesDetailsService.save(employees);
                maxEmployeesId = ((EmployeesDetails) CommonUtils.getMaxId("employees_details", employeesDetailsService)).getId();
            }
            /** 順便建個員工推廣的表記錄 */
            EmployeesPromotion employeesPromotion = new EmployeesPromotion();
            employeesPromotion.setEmployeesId(maxEmployeesId);
            employeesPromotionService.save(employeesPromotion);
        }
        return R.ok("賬戶添加成功");
    }

    @Override
    public R update1(AdminUpdate1DTO dto) {
        //886手机号处理加0
        if(dto.getPhonePrefix().equals("886")){
            if(!dto.getPhone().startsWith("0")){
                dto.setPhone("0"+dto.getPhone());
            }
        }
        Boolean existPhone = false;
        Integer deptId = dto.getDeptId();
        if(deptId.equals(2)){
            existPhone = this.isExistPhone3(dto.getId(),dto.getPhonePrefix(), dto.getPhone());
        }
        if(deptId.equals(6)){
            existPhone = this.isExistPhone3(dto.getId(),dto.getPhonePrefix(), dto.getPhone());
        }
        if(deptId.equals(1)||deptId.equals(3)){
            existPhone = this.isExistPhone4(dto.getId(),dto.getPhonePrefix(), dto.getPhone(),dto.getDeptId());
        }
        if (existPhone){
            return R.failed(null, "該手機號已存在，請重新填寫手機號");
        }
        LocalDateTime now = LocalDateTime.now();
        Integer mineUserId = TokenUtils.getCurrentUserId();
        User user = new User();
        user.setId(dto.getId());
        user.setDeptId(dto.getDeptId());
        user.setName(dto.getName());
        user.setNickname(dto.getNickName());
        user.setPhonePrefix(dto.getPhonePrefix());
        user.setPhone(dto.getPhone());
        if (CommonUtils.isNotEmpty(dto.getEmail())){
            user.setEmail(dto.getEmail());
        }
        user.setPassword(DESEncryption.getEncryptString(dto.getPassword()));
        user.setUpdateTime(now);
        user.setLastReviserId(mineUserId);
        Integer maxUserId = 0;
        this.updateById(user);

        if (dto.getDeptId() == 1){
            //管理员，不修改任何详细信息
        }else if (dto.getDeptId() == 2){
            //公司账户，不修改任何信息
        }else if (dto.getDeptId() == 3){
            //家庭账户，修改手机号和手机号前缀
            QueryWrapper qw = new QueryWrapper();
            qw.eq("user_id", dto.getId());
            CustomerDetails customerDetails = customerDetailsService.getOne(qw);
            if (dto.getPhone().equals(customerDetails.getPhone()) && dto.getPhonePrefix().equals(customerDetails.getPhonePrefix())){

            }else {
                if (CommonUtils.isNotEmpty(dto.getPhone()) && CommonUtils.isNotEmpty(dto.getPhonePrefix())){
                    customerDetails.setPhone(dto.getPhone());
                    customerDetails.setPhonePrefix(dto.getPhonePrefix());
                }else {
                    customerDetails.setPhone(null);
                    customerDetails.setPhonePrefix(null);
                }
                customerDetails.setUpdateTime(now);
                customerDetailsService.updateById(customerDetails);
            }

        }
        else if (dto.getDeptId() == 6){
            //个体户账户，修改手机号和手机号前缀
            QueryWrapper qw = new QueryWrapper();
            qw.eq("user_id", dto.getId());
            EmployeesDetails employeesDetails = employeesDetailsService.getOne(qw);
            if (dto.getPhone().equals(employeesDetails.getPhone()) && dto.getPhonePrefix().equals(employeesDetails.getPhonePrefix())){

            }else {
                if (CommonUtils.isNotEmpty(dto.getPhone()) && CommonUtils.isNotEmpty(dto.getPhonePrefix())){
                    employeesDetails.setPhone(dto.getPhone());
                    employeesDetails.setPhonePrefix(dto.getPhonePrefix());
                }else {
                    employeesDetails.setPhone(null);
                    employeesDetails.setPhonePrefix(null);
                }
                employeesDetails.setUpdateTime(now);
                employeesDetailsService.updateById(employeesDetails);
            }

        }
        return R.ok("賬戶修改成功");
    }

    private Boolean isExistPhone3(Integer id, String phonePrefix, String phone) {
        QueryWrapper qw = new QueryWrapper();
        qw.eq("phone_prefix", phonePrefix);
        qw.eq("phone", phone);
        qw.in("dept_id", 2,6);
        User one = this.getOne(qw);
        if(CommonUtils.isNotEmpty(one)&&!one.getId().equals(id)){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public R update2(AdminUpdate2DTO dto) {
        LocalDateTime now = LocalDateTime.now();
        Integer mineUserId = TokenUtils.getCurrentUserId();
        User user = new User();
        user.setId(dto.getId());
        user.setDeptId(dto.getDeptId());
        user.setName(dto.getName());
        user.setNickname(dto.getNickName());
        if (CommonUtils.isNotEmpty(dto.getPhonePrefix()) && CommonUtils.isNotEmpty(dto.getPhone())){
            user.setPhonePrefix(dto.getPhonePrefix());
            user.setPhone(dto.getPhone());
        }
        if (CommonUtils.isNotEmpty(dto.getEmail())){
            user.setEmail(dto.getEmail());
        }
        user.setUpdateTime(now);
        user.setLastReviserId(mineUserId);
        this.updateById(user);
        if (dto.getDeptId() == 4){
            //經理賬戶
            QueryWrapper qw = new QueryWrapper();
            qw.eq("user_id", dto.getId());
            ManagerDetails managerDetails = managerDetailsService.getOne(qw);
            if (dto.getPhone().equals(managerDetails.getPhone()) &&
                    dto.getPhonePrefix().equals(managerDetails.getPhonePrefix()) &&
                    dto.getEmail().equals(managerDetails.getEmail())){

            }else {
                if (CommonUtils.isNotEmpty(dto.getPhone()) && CommonUtils.isNotEmpty(dto.getPhonePrefix())){
                    managerDetails.setPhone(dto.getPhone());
                    managerDetails.setPhonePrefix(dto.getPhonePrefix());
                }else {
                    managerDetails.setPhone(null);
                    managerDetails.setPhonePrefix(null);
                }
                managerDetails.setEmail(dto.getEmail());
                managerDetails.setUpdateTime(now);
                managerDetailsService.updateById(managerDetails);
            }

        }else if (dto.getDeptId() == 5){
            //保潔員賬戶
            QueryWrapper qw = new QueryWrapper();
            qw.eq("user_id", dto.getId());
            EmployeesDetails employeesDetails = employeesDetailsService.getOne(qw);
            if (dto.getPhone().equals(employeesDetails.getPhone()) &&
                    dto.getPhonePrefix().equals(employeesDetails.getPhonePrefix()) &&
                    dto.getName().equals(employeesDetails.getName())){

            }else {
                if (CommonUtils.isNotEmpty(dto.getPhone()) && CommonUtils.isNotEmpty(dto.getPhonePrefix())){
                    employeesDetails.setPhone(dto.getPhone());
                    employeesDetails.setPhonePrefix(dto.getPhonePrefix());
                }else {
                    employeesDetails.setPhone(null);
                    employeesDetails.setPhonePrefix(null);
                }
                employeesDetails.setName(dto.getName());
                employeesDetails.setUpdateTime(now);
                employeesDetailsService.updateById(employeesDetails);
            }
        }
        return R.ok("賬戶修改成功");
    }

    @Override
    public R removeAdmin(Integer userId) {
        return R.ok(this.removeById(userId));
    }

    @Override
    public R removeCus(Integer userId) {
        QueryWrapper<CustomerDetails> qw = new QueryWrapper<>();
        qw.eq("user_id",userId);
        CustomerDetails one = customerDetailsService.getOne(qw);
        if(CommonUtils.isEmpty(one)){
            return R.failed("查无此人");
        }
        QueryWrapper<CustomerAddress> qw2 = new QueryWrapper<>();
        qw2.eq("customer_id",one.getId());
        customerAddressService.remove(qw2);//依赖1：删除通信地址
        userService.removeById(one.getUserId());//依赖2：sysUser
        this.removeById(userId);
        return R.ok("删除成功");
    }

    @Override
    public R removeComp(Integer userId) {
        QueryWrapper<CompanyDetails> qw = new QueryWrapper<>();
        qw.eq("user_id", userId);
        CompanyDetails one = companyDetailsService.getOne(qw);
        if(CommonUtils.isEmpty(one)){
            return R.failed("該公司不存在");
        }
        List<Integer> empIds = baseMapper.getAllEmps(one.getId());
        List<Integer> manIds = baseMapper.getAllMans(one.getId());
        for (int i = 0; i < empIds.size(); i++) {
            employeesDetailsService.cusRemove(empIds.get(i));
        }
        for (int i = 0; i < manIds.size(); i++) {
            managerDetailsService.cusRemove(manIds.get(i));
        }
        QueryWrapper qw2 = new QueryWrapper<GroupDetails>();
        qw2.eq("company_id",one.getId());
        groupDetailsService.remove(qw2);
        QueryWrapper qw3 = new QueryWrapper<CompanyPromotion>();
        qw3.eq("company_id",one.getId());
        companyPromotionService.remove(qw3);
        QueryWrapper qw4 = new QueryWrapper<CompanyAdvertising>();
        qw4.eq("company_id",one.getId());
        companyAdvertisingService.remove(qw4);
        this.removeById(userId);
        QueryWrapper qw5 = new QueryWrapper<CompanyDetails>();
        qw5.eq("id",one.getId());
        companyDetailsService.remove(qw5);
        this.removeById(userId);
        return R.ok("删除成功");
    }

    @Override
    public Boolean isExistPhone(String phonePrefix, String phone,Integer deptId) {
        QueryWrapper qw = new QueryWrapper();
        qw.eq("phone_prefix", phonePrefix);
        qw.eq("phone", phone);
        qw.eq("dept_id", deptId);
        List<User> users = this.list(qw);
        if (users.size() == 0){
            return false;
        }else {
            return true;
        }
    }

    public Boolean isExistPhone4(Integer id,String phonePrefix, String phone,Integer deptId) {
        QueryWrapper qw = new QueryWrapper();
        qw.eq("phone_prefix", phonePrefix);
        qw.eq("phone", phone);
        qw.in("dept_id", 2,6);
        User one = this.getOne(qw);
        if(CommonUtils.isNotEmpty(one)&&!one.getId().equals(id)){
            return true;
        }else {
            return false;
        }
    }

    public Boolean isExistPhone2(String phonePrefix, String phone) {
        QueryWrapper qw = new QueryWrapper();
        qw.eq("phone_prefix", phonePrefix);
        qw.eq("phone", phone);
        qw.in("dept_id", 2,6);
        List<User> users = this.list(qw);
        if (users.size() == 0){
            return false;
        }else {
            return true;
        }
    }

    @Override
    public R removePersonal(Integer userId) {

        //删除员工详情
        Integer employeesId = employeesDetailsService.getEmployeesIdByUserId(userId);
        employeesDetailsService.cusRemove(employeesId);

        //删除账户
        this.removeById(userId);
        return R.ok("删除成功");
    }

    @Transactional
    @Override
    public R  savePersonal(RegisterCompanyDTO dto) {
        //886手机号处理加0
        if(dto.getPhonePrefix().equals("886")){
            if(!dto.getPhone().startsWith("0")){
                dto.setPhone("0"+dto.getPhone());
            }
        }
        if (CommonUtils.isNotEmpty(dto)) {
            if (CommonUtils.isNotEmpty(dto.getCode())) {
                //判斷redis中的驗證碼是否正確
                if (dto.getCode().equals(redisUtils.get(CommonConstants.REGISTER_KEY_BY_PHONE + "_" + 6 + "_+" + dto.getPhonePrefix() + "_" + dto.getPhone()))) {
                    if (dto.getPassword().equals(dto.getRePassword())) {
                        //先保存User
                        User user = new User();
                        String s = String.valueOf(System.currentTimeMillis());
                        user.setNumber("c"+s);
                        user.setDeptId(6);
                        user.setName(dto.getName());
                        user.setPhonePrefix(dto.getPhonePrefix());
                        user.setPhone(dto.getPhone());
                        user.setPassword(DESEncryption.getEncryptString(dto.getPassword()));
                        user.setLastReviserId(TokenUtils.getCurrentUserId());
                        user.setCreateTime(LocalDateTime.now());
                        user.setUpdateTime(LocalDateTime.now());
                        Integer maxUserId = 0;
                        synchronized (this) {
                            this.save(user);
                            maxUserId = ((User) CommonUtils.getMaxId("sys_user", this)).getId();
                        }

                        //在保存公司信息
                        CompanyDetails companyDetails = new CompanyDetails();
                        companyDetails.setNoCertifiedCompany(dto.getName());
                        companyDetails.setUserId(maxUserId);
                        companyDetails.setIsPersonal(true);
                        companyDetails.setIsValidate(false);
                        companyDetails.setLastReviserId(TokenUtils.getCurrentUserId());
                        companyDetails.setCreateTime(LocalDateTime.now());
                        companyDetails.setUpdateTime(LocalDateTime.now());
                        companyDetails.setLogoUrl("https://test-live-video.oss-cn-shanghai.aliyuncs.com/HKFile/ImPhoto/userId=/20210508104224.png");
                        companyDetailsService.save(companyDetails);

                        //公司推廣
                        Integer maxCompanyId = ((CompanyDetails) CommonUtils.getMaxId("company_details", companyDetailsService)).getId();
                        CompanyPromotion companyPromotion = new CompanyPromotion();
                        companyPromotion.setCompanyId(maxCompanyId);
                        companyPromotionService.save(companyPromotion);

                        //保存员工信息
                        EmployeesDetails employeesDetails = new EmployeesDetails();
                        employeesDetails.setCompanyId(maxCompanyId.toString());
                        employeesDetails.setPhonePrefix(dto.getPhonePrefix());
                        employeesDetails.setPhone(dto.getPhone());
                        employeesDetails.setUserId(maxUserId);
                        employeesDetails.setName(dto.getName());


                        employeesDetails.setLat("25.148998");
                        employeesDetails.setLng("121.77368");

                        employeesDetails.setStarRating(3.0f); //新增的员工默认为三星级，中等好评
                        employeesDetails.setBlacklistFlag(false);
                        employeesDetails.setNumberOfOrders(0); //默认接单次数为0

                        employeesDetails.setUpdateTime(LocalDateTime.now());
                        employeesDetails.setCreateTime(LocalDateTime.now());
                        employeesDetails.setLastReviserId(TokenUtils.getCurrentUserId());

                        //头像
                        if(StringUtils.isBlank(dto.getHeadUrl())){
                            employeesDetails.setHeadUrl("https://test-live-video.oss-cn-shanghai.aliyuncs.com/HKFile/ImPhoto/userId=/20210508103930.png");
                        }else{
                            employeesDetails.setHeadUrl(dto.getHeadUrl());
                        }

                        Object savePoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
                        try {
                            Integer maxEmployeesId;
                            synchronized (this){
                                employeesDetailsService.save(employeesDetails);
                                maxEmployeesId = ((EmployeesDetails) CommonUtils.getMaxId("employees_details", employeesDetailsService)).getId();
                            }

                            // 順便建個員工推廣的表記錄
                            EmployeesPromotion employeesPromotion = new EmployeesPromotion();
                            employeesPromotion.setEmployeesId(maxEmployeesId);
                            employeesPromotionService.save(employeesPromotion);

                        } catch (Exception e){
                            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savePoint);
                    }
                        //保存邀请推广信息
                        if(CommonUtils.isNotEmpty(dto.getInvitee())){
                            Invitation invitation = new Invitation();
                            invitation.setInvitee(dto.getInvitee());
                            invitation.setInvitees(maxUserId);
                            invitationService.save(invitation);
                        }

                    } else {
                        return R.failed("两次密码不一致");
                    }
                } else {
                    return R.failed("验证码错误");
                }
            } else {
                return R.failed("验证码为空");
            }
        }
        HkUser hkUser = baseMapper.byPhoneLogin(dto.getPhone(), 6);
        hkUser.setAuthType(1);
        String token = tokenService.getToken(hkUser);
        return R.ok(token,"創建個體戶成功");
    }

    @Override
    public R savePersonalByAdmin(RegisterPersonalDTO dto) {
        //先保存User
        User user = new User();
        String s = String.valueOf(System.currentTimeMillis());
        user.setNumber("c"+s);
        user.setDeptId(6);
        user.setName(dto.getName());
        user.setPhonePrefix(dto.getPhonePrefix());
        user.setPhone(dto.getPhone());
        user.setPassword(DESEncryption.getEncryptString(dto.getPassword()));
        user.setLastReviserId(TokenUtils.getCurrentUserId());
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        Integer maxUserId = 0;
        synchronized (this) {
            this.save(user);
            maxUserId = ((User) CommonUtils.getMaxId("sys_user", this)).getId();
        }

        //在保存公司信息
        CompanyDetails companyDetails = new CompanyDetails();
        companyDetails.setNoCertifiedCompany(dto.getName());
        companyDetails.setUserId(maxUserId);
        companyDetails.setIsPersonal(true);
        companyDetails.setIsValidate(false);
        companyDetails.setLastReviserId(TokenUtils.getCurrentUserId());
        companyDetails.setCreateTime(LocalDateTime.now());
        companyDetails.setUpdateTime(LocalDateTime.now());
        companyDetails.setLogoUrl("https://test-live-video.oss-cn-shanghai.aliyuncs.com/HKFile/ImPhoto/userId=/20210508104224.png");
        companyDetailsService.save(companyDetails);

        //公司推廣
        Integer maxCompanyId = ((CompanyDetails) CommonUtils.getMaxId("company_details", companyDetailsService)).getId();
        CompanyPromotion companyPromotion = new CompanyPromotion();
        companyPromotion.setCompanyId(maxCompanyId);
        companyPromotionService.save(companyPromotion);

        //保存员工信息
        EmployeesDetails employeesDetails = new EmployeesDetails();
        employeesDetails.setCompanyId(maxCompanyId.toString());
        employeesDetails.setPhonePrefix(dto.getPhonePrefix());
        employeesDetails.setPhone(dto.getPhone());
        employeesDetails.setUserId(maxUserId);
        employeesDetails.setName(dto.getName());

        employeesDetails.setStarRating(3.0f); //新增的员工默认为三星级，中等好评
        employeesDetails.setBlacklistFlag(false);
        employeesDetails.setNumberOfOrders(0); //默认接单次数为0

        employeesDetails.setUpdateTime(LocalDateTime.now());
        employeesDetails.setCreateTime(LocalDateTime.now());
        employeesDetails.setLastReviserId(TokenUtils.getCurrentUserId());

        //头像
        employeesDetails.setHeadUrl("https://test-live-video.oss-cn-shanghai.aliyuncs.com/HKFile/ImPhoto/userId=/20210508103930.png");

        Integer maxEmployeesId;
        synchronized (this){
            employeesDetailsService.save(employeesDetails);
            maxEmployeesId = ((EmployeesDetails) CommonUtils.getMaxId("employees_details", employeesDetailsService)).getId();
        }

        // 順便建個員工推廣的表記錄
        EmployeesPromotion employeesPromotion = new EmployeesPromotion();
        employeesPromotion.setEmployeesId(maxEmployeesId);
        employeesPromotionService.save(employeesPromotion);
        return R.ok("新增成功");
    }

    @Override
    public R getSms(Integer type) {
        Set<String> registerSms = redisTemplate.keys("HK_REGISTER_KEY_BY_PHONE*");

        Set<String> loginSms = redisTemplate.keys("HK_LOGIN_KEY_BY_PHONE*");

        ArrayList<SmsDTO> registerSmsList = new ArrayList<>();
        ArrayList<SmsDTO> loginSmsList = new ArrayList<>();
        ArrayList<SmsDTO> list = new ArrayList<>();
        if(!registerSms.isEmpty()){
            Object[] keysArr = registerSms.toArray();
            for (int i = 0; i < keysArr.length; i++) {
                String key = keysArr[i].toString();
                String byPhone = key.replaceAll("HK_REGISTER_KEY_BY_PHONE_", "");
                String[] s = byPhone.split("_");
                Object o = redisTemplate.opsForValue().get(key);
                SmsDTO smsDTO = new SmsDTO();
                smsDTO.setDept(s[0]);
                smsDTO.setPhone_prefix(s[1]);
                smsDTO.setPhone(s[2]);
                smsDTO.setCode(o.toString());
                registerSmsList.add(smsDTO);
            }
        }
        if(!loginSms.isEmpty()){
            Object[] keysArr = loginSms.toArray();
            for (int i = 0; i < keysArr.length; i++) {
                String key = keysArr[i].toString();
                String byPhone = key.replaceAll("HK_LOGIN_KEY_BY_PHONE_", "");
                String[] s = byPhone.split("_");
                Object o = redisTemplate.opsForValue().get(key);
                SmsDTO smsDTO = new SmsDTO();
                smsDTO.setDept(s[0]);
                smsDTO.setPhone_prefix(s[1]);
                smsDTO.setPhone(s[2]);
                smsDTO.setCode(o.toString());
                loginSmsList.add(smsDTO);
            }
        }

        if(type.equals(0)){
            list.addAll(registerSmsList);
            list.addAll(loginSmsList);
        }
        if(type.equals(1)){
            list.addAll(registerSmsList);
        }
        if(type.equals(2)){
            list.addAll(loginSmsList);
        }

        return R.ok(list);
    }

    @Override
    public R sendForgetSms(String phonePrefix, String phone, Integer deptId) throws Exception {
        //886手机号处理加0
        if(phonePrefix.equals("886")){
            if(!phone.startsWith("0")){
                phone = "0"+phone;
            }
        }

       if(deptId.equals(0)){
           User hkUser = this.getCusByPhoneAndPrefix(phonePrefix, phone);
           if (CommonUtils.isNotEmpty(hkUser)) {
               //生成随即验证码
               String code = CommonUtils.getRandomSixCode();
               String key = CommonConstants.FORGET_KEY_BY_PHONE + "_+" + phonePrefix + "_" + phone;
               //存入redis
               redisUtils.set(key, code);
               redisUtils.expire(key, CommonConstants.VALID_TIME_MINUTES * 60);//三分鐘
               //发送短信
               String[] params = new String[]{code, CommonConstants.VALID_TIME_MINUTES.toString()};
               SendMessage.sendMessage(phonePrefix, phone, params);
               return R.ok("成功發送短信");
           } else {
               return R.failed("該手機號未註冊");
           }
       }else{
           User hkUser = this.getUserByPhoneAndPrefix(phonePrefix, phone);
           if (CommonUtils.isNotEmpty(hkUser)) {
               //生成随即验证码
               String code = CommonUtils.getRandomSixCode();
               String key = CommonConstants.FORGET_KEY_BY_PHONE + "_+" + phonePrefix + "_" + phone;
               //存入redis
               redisUtils.set(key, code);
               redisUtils.expire(key, CommonConstants.VALID_TIME_MINUTES * 60);//三分鐘
               //发送短信
               String[] params = new String[]{code, CommonConstants.VALID_TIME_MINUTES.toString()};
               SendMessage.sendMessage(phonePrefix, phone, params);
               return R.ok("成功發送短信");
           } else {
               return R.failed("該手機號未註冊");
           }
       }
    }

    @Override
    public R updatePwdByPhone(ForgetDTO forgetDTO) {
        if(!forgetDTO.getPassword().equals(forgetDTO.getRePassword())){
            return R.failed("两次密码不一致");
        }
        String phonePrefix = forgetDTO.getPhonePrefix();
        String phone = forgetDTO.getPhone();
        //886手机号处理加0
        if(phonePrefix.equals("886")){
            if(!phone.startsWith("0")){
                phone = "0"+phone;
            }
        }
        User user;
        if(forgetDTO.getDeptId().equals(0)){
            user = this.getCusByPhoneAndPrefix(phonePrefix, phone);
        }else{
            user = this.getUserByPhoneAndPrefix(phonePrefix, phone);
        }
        user.setPassword(DESEncryption.getEncryptString(forgetDTO.getPassword()));
        this.updateById(user);
        return R.ok("密碼修改成功");
    }

    @Override
    public R verfifyCode2(String phonePrefix, String phone, String code) {
        //886手机号处理加0
        if(phonePrefix.equals("886")){
            if(!phone.startsWith("0")){
                phone = "0"+phone;
            }
        }

        if (code.equals(redisUtils.get(CommonConstants.FORGET_KEY_BY_PHONE  + "_+" + phonePrefix + "_" + phone))){
            return R.ok("验证码通过");
        }
        return R.failed("验证码错误");
    }

    @Override
    public R checkData2(String data,String type) {
        //886手机号处理加0
        if(type.equals("886")){
            if(!data.startsWith("0")){
                data = "0"+data;
            }
        }

        QueryWrapper qr = new QueryWrapper();
        qr.eq("phone_prefix",type);
        qr.eq("phone", data);
        qr.in("dept_id", 2,6);
        if (this.userMapper.selectCount(qr) == 0) {
            return R.ok("手机号或者邮箱不存在,可以注册");
        } else {
            return R.failed("手機號已注冊");
        }
    }

    @Override
    public R getAllCompany(Page page, AdminPageDTO adminPageDTO) {

        //所有部门为2的账户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dept_id",2);
        queryWrapper.orderByDesc("id");
        if (CommonUtils.isNotEmpty(adminPageDTO.getId())){
            queryWrapper.eq("id", adminPageDTO.getId());
        }
        if (CommonUtils.isNotEmpty(adminPageDTO.getNumber())){
            queryWrapper.like("number", adminPageDTO.getNumber());
        }
        if (CommonUtils.isNotEmpty(adminPageDTO.getNickname())){
            queryWrapper.like("nickname", adminPageDTO.getNickname());
        }
        if (CommonUtils.isNotEmpty(adminPageDTO.getName())){
            queryWrapper.like("name", adminPageDTO.getName());
        }
        if (CommonUtils.isNotEmpty(adminPageDTO.getEmail())){
            queryWrapper.like("email", adminPageDTO.getEmail());
        }
        if (CommonUtils.isNotEmpty(adminPageDTO.getPhone())){
            queryWrapper.like("phone", adminPageDTO.getPhone());
        }
        List<User> list = this.list(queryWrapper);

        //所有满足is_personal=0&&is_validate=1的公司userId
        List<Integer> userIds = companyDetailsService.list().stream().map(x -> {
            if (x.getIsPersonal() == false && x.getIsValidate() == true) {
                return x.getUserId();
            } else {
                return null;
            }
        }).collect(Collectors.toList());

        List<User> collect = list.stream().filter(x -> {
            if (userIds.contains(x.getId())) {
                return true;
            } else {
                return false;
            }
        }).collect(Collectors.toList());

        for (User user : collect) {
            String decryptString = DESEncryption.getDecryptString(user.getPassword());
            user.setPassword(decryptString);
        }

        Page pages = PageUtils.getPages((int) page.getCurrent(), (int) page.getSize(), collect);

        return R.ok(pages, "查詢成功");
    }

    @Override
    public R getAllStudio(Page page, AdminPageDTO adminPageDTO) {
        //所有部门为2的账户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dept_id",2);
        queryWrapper.orderByDesc("id");
        if (CommonUtils.isNotEmpty(adminPageDTO.getId())){
            queryWrapper.eq("id", adminPageDTO.getId());
        }
        if (CommonUtils.isNotEmpty(adminPageDTO.getNumber())){
            queryWrapper.like("number", adminPageDTO.getNumber());
        }
        if (CommonUtils.isNotEmpty(adminPageDTO.getNickname())){
            queryWrapper.like("nickname", adminPageDTO.getNickname());
        }
        if (CommonUtils.isNotEmpty(adminPageDTO.getName())){
            queryWrapper.like("name", adminPageDTO.getName());
        }
        if (CommonUtils.isNotEmpty(adminPageDTO.getEmail())){
            queryWrapper.like("email", adminPageDTO.getEmail());
        }
        if (CommonUtils.isNotEmpty(adminPageDTO.getPhone())){
            queryWrapper.like("phone", adminPageDTO.getPhone());
        }
        List<User> list = this.list(queryWrapper);

        //所有满足is_personal=0&&is_validate=1的公司userId
        List<Integer> userIds = companyDetailsService.list().stream().map(x -> {
            if (x.getIsPersonal() == false && x.getIsValidate() == false) {
                return x.getUserId();
            } else {
                return null;
            }
        }).collect(Collectors.toList());

        List<User> collect = list.stream().filter(x -> {
            if (userIds.contains(x.getId())) {
                return true;
            } else {
                return false;
            }
        }).collect(Collectors.toList());

        for (User user : collect) {
            String decryptString = DESEncryption.getDecryptString(user.getPassword());
            user.setPassword(decryptString);
        }
        Page pages = PageUtils.getPages((int) page.getCurrent(), (int) page.getSize(), collect);

        return R.ok(pages, "查詢成功");
    }

    @Override
    public R getInfo() {
        Integer userId = TokenUtils.getCurrentUserId();
        User byId = this.getById(userId);
        return R.ok(byId);
    }
}
