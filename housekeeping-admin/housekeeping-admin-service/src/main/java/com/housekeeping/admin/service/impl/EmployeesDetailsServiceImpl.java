package com.housekeeping.admin.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.CompanyDetails;
import com.housekeeping.admin.entity.EmployeesDetails;
import com.housekeeping.admin.mapper.EmployeesDetailsMapper;
import com.housekeeping.admin.service.EmployeesDetailsService;
import com.housekeeping.admin.service.ICompanyDetailsService;
import com.housekeeping.common.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.net.UnknownHostException;
import java.time.LocalDateTime;

@Service("employeesDetailsService")
public class EmployeesDetailsServiceImpl extends ServiceImpl<EmployeesDetailsMapper, EmployeesDetails> implements EmployeesDetailsService {

    @Autowired
    private ICompanyDetailsService companyDetailsService;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public R saveEmp(EmployeesDetails employeesDetails) {
        if(this.addEmployee()){
            if(CommonUtils.isNotEmpty(employeesDetails)){
                QueryWrapper<CompanyDetails> wrComp=new QueryWrapper<>();
                wrComp.inSql("id","select id from company_details where user_id="+ TokenUtils.getCurrentUserId());
                CompanyDetails one = companyDetailsService.getOne(wrComp);
                String s = String.valueOf(System.currentTimeMillis());
                employeesDetails.setNumber("emp"+s);
                employeesDetails.setUpdateTime(LocalDateTime.now());
                employeesDetails.setCreateTime(LocalDateTime.now());
                employeesDetails.setCompanyId(one.getId());
                employeesDetails.setLastReviserId(TokenUtils.getCurrentUserId());
                this.save(employeesDetails);
            }
        }else {
            return R.failed("公司員工人數達到上綫，請升級公司規模");
        }
        return R.ok("添加員工成功");
    }

    @Override
    public R updateEmp(EmployeesDetails employeesDetails) {
        employeesDetails.setLastReviserId(TokenUtils.getCurrentUserId());
        employeesDetails.setUpdateTime(LocalDateTime.now());
        if(this.updateById(employeesDetails)){
            return R.ok("修改成功");
        }else {
            return R.failed("修改失敗");
        }

    }

    @Override
    public IPage cusPage(Page page, Integer id) {
        Integer userId = TokenUtils.getCurrentUserId();
        QueryWrapper<CompanyDetails> wrComp=new QueryWrapper<>();
        wrComp.inSql("id","select id from company_details where user_id="+ userId);
        CompanyDetails one = companyDetailsService.getOne(wrComp);
        return baseMapper.cusPage(page,id,one.getId());
    }

    @Override
    public R getLinkToLogin(Integer id, Long h) throws UnknownHostException {
        EmployeesDetails employeesDetails = baseMapper.selectById(id);
        if (CommonUtils.isNotEmpty(employeesDetails)){
            String url = "";
            String mysteriousCode = CommonUtils.getMysteriousCode(); //神秘代码
            String key = CommonConstants.LOGIN_EMPLOYEES_PREFIX + mysteriousCode;
            redisUtils.set(key, id, 60 * 60 * h);//有效期12小时
            //拼接url链接
            url = CommonUtils.getRequestPrefix() + "/auth/Employees/" + mysteriousCode;
            return R.ok(url);
        } else {
            return R.failed("員工不存在，請刷新頁面重試");
        }
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
        if(split[1]=="n"){
            companyMaxsize = 1000000000;
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
