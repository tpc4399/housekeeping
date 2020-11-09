package com.housekeeping.admin.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.CompanyDetails;
import com.housekeeping.admin.entity.EmployeesDetails;
import com.housekeeping.admin.mapper.EmployeesDetailsMapper;
import com.housekeeping.admin.service.EmployeesDetailsService;
import com.housekeeping.admin.service.ICompanyDetailsService;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("employeesDetailsService")
public class EmployeesDetailsServiceImpl extends ServiceImpl<EmployeesDetailsMapper, EmployeesDetails> implements EmployeesDetailsService {

    @Autowired
    private ICompanyDetailsService companyDetailsService;
    @Override
    public R saveEmp(EmployeesDetails employeesDetails) {
        this.countByUserId();
        return R.ok();
    }

    public Integer countByUserId(){
        QueryWrapper<CompanyDetails> wrComp=new QueryWrapper<>();
        String scaleById = baseMapper.getScaleById(1);
        wrComp.inSql("id","select id from company_details where user_id="+ TokenUtils.getCurrentUserId());
        CompanyDetails one = companyDetailsService.getOne(wrComp);
        QueryWrapper<EmployeesDetails> qw = new QueryWrapper<>();
        qw.eq("company_id",one.getId());
        return baseMapper.selectCount(qw);
    }
}
