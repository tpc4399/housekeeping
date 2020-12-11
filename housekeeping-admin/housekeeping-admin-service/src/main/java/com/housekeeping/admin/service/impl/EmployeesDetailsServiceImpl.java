package com.housekeeping.admin.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.EmployeesDetailsDTO;
import com.housekeeping.admin.dto.PageOfEmployeesDTO;
import com.housekeeping.admin.dto.PageOfEmployeesDetailsDTO;
import com.housekeeping.admin.entity.CompanyDetails;
import com.housekeeping.admin.entity.EmployeesDetails;
import com.housekeeping.admin.entity.ManagerDetails;
import com.housekeeping.admin.entity.User;
import com.housekeeping.admin.mapper.EmployeesDetailsMapper;
import com.housekeeping.admin.service.*;
import com.housekeeping.common.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.net.UnknownHostException;
import java.time.LocalDateTime;

@Service("employeesDetailsService")
public class EmployeesDetailsServiceImpl extends ServiceImpl<EmployeesDetailsMapper, EmployeesDetails> implements EmployeesDetailsService {

    @Resource
    private ICompanyDetailsService companyDetailsService;

    @Resource
    private ManagerDetailsService managerDetailsService;

    @Resource
    private IUserService userService;

    @Autowired
    private RedisUtils redisUtils;

    @Resource
    private IEmployeesWorkExperienceService employeesWorkExperienceService;

    @Transactional
    @Override
    public R saveEmp(EmployeesDetailsDTO employeesDetailsDTO) {
        if(this.addEmployee()){
            if(CommonUtils.isNotEmpty(employeesDetailsDTO)){
                //先保存User
                User user = new User();
                String ss = String.valueOf(System.currentTimeMillis());
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
                employeesDetails.setNumber(employeesDetailsDTO.getNumber());
                employeesDetails.setName(employeesDetailsDTO.getName());
                employeesDetails.setSex(employeesDetailsDTO.getSex());
                employeesDetails.setDateOfBirth(employeesDetailsDTO.getDateOfBirth());
                employeesDetails.setIdCard(employeesDetailsDTO.getIdCard());
                employeesDetails.setAddress1(employeesDetailsDTO.getAddress1());
                employeesDetails.setAddress2(employeesDetailsDTO.getAddress2());
                employeesDetails.setAddress3(employeesDetailsDTO.getAddress3());
                employeesDetails.setAddress4(employeesDetailsDTO.getAddress4());
                employeesDetails.setRecordOfFormalSchooling(employeesDetailsDTO.getRecordOfFormalSchooling());
                employeesDetails.setPhone(employeesDetailsDTO.getPhone());
                employeesDetails.setAccountLine(employeesDetailsDTO.getAccountLine());
                employeesDetails.setDescribes(employeesDetailsDTO.getDescribes());
                employeesDetails.setWorkYear(employeesDetailsDTO.getWorkYear());

                employeesDetails.setUpdateTime(LocalDateTime.now());
                employeesDetails.setCreateTime(LocalDateTime.now());
                employeesDetails.setCompanyId(one.getId());
                employeesDetails.setLastReviserId(TokenUtils.getCurrentUserId());
                Integer maxEmployeesId = 0;
                Object savePoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
                try {
                    synchronized (this){
                        this.save(employeesDetails);
                        maxEmployeesId = ((EmployeesDetails) CommonUtils.getMaxId("employees_details", this)).getId();
                    }
                    /**
                     * 工作经验保存
                     */
                    employeesWorkExperienceService.saveEmployeesWorkExperience(employeesDetailsDTO.getWorkExperiencesDTO(), maxEmployeesId);
                } catch (Exception e){
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savePoint);
                    return R.failed("添加失敗");
                }

            }
        }else {
            return R.failed("公司員工人數達到上綫，請升級公司規模");
        }
        return R.ok("添加員工成功");
    }

    @Override
    public R updateEmp(EmployeesDetailsDTO employeesDetailsDTO) {
        EmployeesDetails employeesDetails = new EmployeesDetails();
        employeesDetails.setId(employeesDetailsDTO.getId());
        employeesDetails.setNumber(employeesDetailsDTO.getNumber());
        employeesDetails.setName(employeesDetailsDTO.getName());
        employeesDetails.setSex(employeesDetailsDTO.getSex());
        employeesDetails.setDateOfBirth(employeesDetailsDTO.getDateOfBirth());
        employeesDetails.setIdCard(employeesDetailsDTO.getIdCard());
        employeesDetails.setAddress1(employeesDetailsDTO.getAddress1());
        employeesDetails.setAddress2(employeesDetailsDTO.getAddress2());
        employeesDetails.setAddress3(employeesDetailsDTO.getAddress3());
        employeesDetails.setAddress4(employeesDetailsDTO.getAddress4());
        employeesDetails.setRecordOfFormalSchooling(employeesDetailsDTO.getRecordOfFormalSchooling());
        employeesDetails.setPhone(employeesDetailsDTO.getPhone());
        employeesDetails.setAccountLine(employeesDetailsDTO.getAccountLine());
        employeesDetails.setDescribes(employeesDetailsDTO.getDescribes());
        employeesDetails.setWorkYear(employeesDetailsDTO.getWorkYear());

        employeesDetails.setUpdateTime(LocalDateTime.now());
        employeesDetails.setLastReviserId(TokenUtils.getCurrentUserId());

        Object savePoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
        try {
            this.updateById(employeesDetails);

            /**
             * 工作经验修改
             */
            employeesWorkExperienceService.updateEmployeesWorkExperience(employeesDetailsDTO.getWorkExperiencesDTO(), employeesDetailsDTO.getId());
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savePoint);
            return R.failed("修改失敗");
        }
        return R.ok("修改成功");


    }

    @Override
    public R cusPage(Page page, PageOfEmployeesDetailsDTO pageOfEmployeesDetailsDTO, String type) {
        QueryWrapper  queryWrapper = new QueryWrapper();
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
            queryWrapper.eq("company_id", companyId);
        }

        if (type.equals(CommonConstants.REQUEST_ORIGIN_MANAGER)){
            QueryWrapper queryWrapper2 = new QueryWrapper();
            queryWrapper2.eq("user_id", TokenUtils.getCurrentUserId());
            ManagerDetails managerDetails = managerDetailsService.getOne(queryWrapper2);
            Integer managerId = managerDetails.getId();
            Integer companyId = managerDetailsService.getCompanyIdByManagerId(managerId);
            queryWrapper.eq("company_id", companyId);
        }

        IPage<EmployeesDetails> employeesDetailsIPage = baseMapper.selectPage(page, queryWrapper);
        return R.ok(employeesDetailsIPage, "分頁查詢成功");
    }

    @Override
    public R getLinkToLogin(Integer id, Long h) throws UnknownHostException {
        EmployeesDetails employeesDetails = baseMapper.selectById(id);
        if (CommonUtils.isNotEmpty(employeesDetails)){
            String url = "";
            String mysteriousCode = CommonUtils.getMysteriousCode(); //神秘代码
            String key = CommonConstants.LOGIN_EMPLOYEES_PREFIX + mysteriousCode;
            Integer value = employeesDetails.getUserId();
            redisUtils.set(key, value, 60 * 60 * h);//有效期12小时
            //拼接url链接
            url = CommonUtils.getRequestPrefix() + "/auth/Employees/" + mysteriousCode;
            return R.ok(url);
        } else {
            return R.failed("員工不存在，請刷新頁面重試");
        }
    }

    @Override
    public R cusPage1(Page page, PageOfEmployeesDTO pageOfEmployeesDTO, String type) {
        QueryWrapper  queryWrapper = new QueryWrapper();
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
        IPage<EmployeesDetails> employeesDetailsIPage = baseMapper.selectPage(page, queryWrapper);
        return R.ok(employeesDetailsIPage, "分頁查詢成功");
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
        String[] split = scaleById.split("~");
        Integer companyMaxsize;
        if("n".equals(split[1])){
            companyMaxsize = Integer.MAX_VALUE;
        }else {
            companyMaxsize = Integer.parseInt(split[1]);
        }
        QueryWrapper<EmployeesDetails> qw = new QueryWrapper<>();
        qw.eq("company_id",one.getId());
        Integer currentSize = baseMapper.selectCount(qw);
        if(companyMaxsize>currentSize){
            return true;
        }else {
            return false;
        }
    }
}
