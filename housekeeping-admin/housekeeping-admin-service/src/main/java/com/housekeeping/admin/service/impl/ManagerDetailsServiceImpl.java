package com.housekeeping.admin.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.CompanyDetails;
import com.housekeeping.admin.entity.EmployeesDetails;
import com.housekeeping.admin.entity.ManagerDetails;
import com.housekeeping.admin.mapper.EmployeesDetailsMapper;
import com.housekeeping.admin.mapper.ManagerDetailsMapper;
import com.housekeeping.admin.service.EmployeesDetailsService;
import com.housekeeping.admin.service.ICompanyDetailsService;
import com.housekeeping.admin.service.ManagerDetailsService;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service("employeesDetailsService")
public class ManagerDetailsServiceImpl extends ServiceImpl<ManagerDetailsMapper, ManagerDetails> implements ManagerDetailsService {

    @Autowired
    private ICompanyDetailsService companyDetailsService;

    @Override
    public R saveEmp(ManagerDetails managerDetails) {
        if(this.addManager()){
            if(CommonUtils.isNotEmpty(managerDetails)){
                QueryWrapper<CompanyDetails> wrComp=new QueryWrapper<>();
                wrComp.inSql("id","select id from company_details where user_id="+ TokenUtils.getCurrentUserId());
                CompanyDetails one = companyDetailsService.getOne(wrComp);
                String s = String.valueOf(System.currentTimeMillis());
                managerDetails.setNumber("man"+s);
                managerDetails.setUpdateTime(LocalDateTime.now());
                managerDetails.setCreateTime(LocalDateTime.now());
                managerDetails.setCompanyId(one.getId());
                managerDetails.setLastReviserId(TokenUtils.getCurrentUserId());
                this.save(managerDetails);
            }
        }else {
            return R.failed("公司經理人數達到上綫，請升級公司規模");
        }
        return R.ok("添加經理成功");
    }

    @Override
    public R updateEmp(ManagerDetails managerDetails) {
        managerDetails.setLastReviserId(TokenUtils.getCurrentUserId());
        managerDetails.setUpdateTime(LocalDateTime.now());
        if(this.updateById(managerDetails)){
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

    /**
     * 判斷公司是否可以新增員工
     * @return
     */
    public Boolean addManager(){
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
        QueryWrapper<ManagerDetails> qw = new QueryWrapper<>();
        qw.eq("company_id",one.getId());
        Integer currentSize = baseMapper.selectCount(qw);
        if(companyMaxsize>currentSize){
            return true;
        }else {
            return false;
        }
    }
}
