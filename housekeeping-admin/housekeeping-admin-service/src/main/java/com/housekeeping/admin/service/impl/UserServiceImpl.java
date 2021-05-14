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
import com.housekeeping.common.sms.SendMessage;
import com.housekeeping.common.utils.*;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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

    @Override
    public User getUserByPhone(String phonePrefix, String phone, Integer deptId) {
        QueryWrapper qr = new QueryWrapper();
        qr.eq("phone_prefix", phonePrefix);
        qr.eq("dept_id", deptId);
        qr.eq("phone", phone);
        User res = baseMapper.selectOne(qr);

        return res;
    }

    @Override
    public R checkData(Integer deptId, String data, Integer type) {
        QueryWrapper qr = new QueryWrapper();
        switch (type) {
            case 1:
                qr.eq("dept_id", deptId);
                qr.eq("phone", data);
                break;
            case 2:
                qr.eq("dept_id", deptId);
                qr.eq("email", data);
                break;
        }
        if (this.userMapper.selectCount(qr) == 0) {
            return R.ok("手机号或者邮箱不存在,可以注册");
        } else {
            return R.failed("手机号或者邮箱已存在");
        }
    }

    @Override
    public R sendRegisterMSMessage(String phonePrefix, String phone, Integer deptId) {
        User hkUser = this.getUserByPhone(phonePrefix, phone, deptId);
        if (CommonUtils.isEmpty(hkUser)) {
            //生成随即验证码
            String code = CommonUtils.getRandomSixCode();
            String key = CommonConstants.REGISTER_KEY_BY_PHONE + "_" + deptId + "_+" + phonePrefix + "_" + phone;
            //存入redis
            redisUtils.set(key, code);
            redisUtils.expire(key, CommonConstants.VALID_TIME_MINUTES * 60);//三分鐘
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
//                        companyDetails.setCompanyName(registerDTO.getName());
                        companyDetails.setUserId(maxUserId);
                        companyDetails.setIsValidate(false);
                        companyDetails.setLastReviserId(TokenUtils.getCurrentUserId());
                        companyDetails.setCreateTime(LocalDateTime.now());
                        companyDetails.setUpdateTime(LocalDateTime.now());
                        companyDetails.setLogoUrl("https://test-live-video.oss-cn-shanghai.aliyuncs.com/HKFile/ImPhoto/userId=/20210508104224.png");
                        companyDetailsService.save(companyDetails);
                        /*
                        公司推廣
                        * */
                        Integer maxCompanyId = ((CompanyDetails) CommonUtils.getMaxId("company_details", companyDetailsService)).getId();
                        CompanyPromotion companyPromotion = new CompanyPromotion();
                        companyPromotion.setCompanyId(maxCompanyId);
                        companyPromotionService.save(companyPromotion);
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
        return R.ok("创建公司账户成功");
    }

    @Override
    public R saveCus(RegisterCustomerDTO dto) {
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
                        customerAddressService.save(customerAddress);
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
        return R.ok("創建客戶成功");
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
    public R sendForgetMSMessage(String phonePrefix, String phone, Integer deptId) {
        User hkUser = this.getUserByPhone(phonePrefix, phone, deptId);
        if (CommonUtils.isNotEmpty(hkUser)) {
            //生成随即验证码
            String code = CommonUtils.getRandomSixCode();
            String key = CommonConstants.FORGET_KEY_BY_PHONE + "_" + deptId + "_+" + phonePrefix + "_" + phone;
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

    @Override
    public R updatePwd(ForgetDTO forgetDTO) {
        if(!forgetDTO.getPassword().equals(forgetDTO.getRePassword())){
            return R.failed("两次密码不一致");
        }
        User user = this.getUserByPhone(forgetDTO.getPhonePrefix(), forgetDTO.getPhone(), forgetDTO.getDeptId());
        user.setPassword(DESEncryption.getEncryptString(forgetDTO.getPassword()));
        this.updateById(user);
        return R.ok("密碼修改成功");
    }

    @Override
    public R verfifyCode(String phonePrefix, String phone, String code,Integer deptId) {
        if (code.equals(redisUtils.get(CommonConstants.FORGET_KEY_BY_PHONE + "_" + deptId + "_+" + phonePrefix + "_" + phone))){
            return R.ok("验证码通过");
        }
        return R.failed("验证码错误");
    }

    @Override
    public R getAllUser(IPage<User> page, AdminPageDTO adminPageDTO,Integer deptId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("dept_id",deptId);
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
        Boolean existPhone = this.isExistPhone(dto.getPhonePrefix(), dto.getPhone(), dto.getDeptId());
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
            customerDetailsService.save(customer);
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
            employees.setUserId(maxUserId);
            employees.setCompanyId(dto.getCompanyId());
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
        Boolean existPhone = this.isExistPhone(dto.getPhonePrefix(), dto.getPhone(), dto.getDeptId());
        if (existPhone){
            return R.failed(null, "該手機號已存在");
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
        return R.ok("賬戶修改成功");
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
        return R.ok("删除成功");
    }

    @Override
    public Boolean isExistPhone(String phonePrefix, String phone, Integer deptId) {
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

}
