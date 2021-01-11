package com.housekeeping.admin.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.CompanyPromotionDTO;
import com.housekeeping.admin.dto.EmployeesPromotionDTO;
import com.housekeeping.admin.entity.CompanyDetails;
import com.housekeeping.admin.entity.CompanyPromotion;
import com.housekeeping.admin.entity.EmployeesDetails;
import com.housekeeping.admin.entity.EmployeesPromotion;
import com.housekeeping.admin.mapper.CompanyPromotionMapper;
import com.housekeeping.admin.mapper.EmployeesPromotionMapper;
import com.housekeeping.admin.service.EmployeesDetailsService;
import com.housekeeping.admin.service.ICompanyPromotionService;
import com.housekeeping.admin.service.IEmployeesPromotionService;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service("employeesPromotionService")
public class EmployeesPromotionServiceImpl extends ServiceImpl<EmployeesPromotionMapper, EmployeesPromotion> implements IEmployeesPromotionService {

    @Resource
    private CompanyDetailsServiceImpl companyDetailsService;
    @Resource
    private EmployeesDetailsService employeesDetailsService;

    @Override
    @Transactional
    public R promotionDay(Integer empId) {
        EmployeesDetails byId = employeesDetailsService.getById(empId);
        int tokens = companyDetailsService.getById(byId.getCompanyId()).getTokens().intValue();
        if(tokens<1){
            return R.failed("推廣失敗，您的代幣不足，請及時充值");
        }else {
            QueryWrapper<EmployeesPromotion> qw = new QueryWrapper<>();
            qw.eq("employees_id",empId);
            EmployeesPromotion one = this.getOne(qw);
            if(one.getPromotion()){
                one.setPromotion(true);
                one.setStartTime(one.getStartTime());
                one.setEndTime(one.getEndTime().plusDays(1L));
                companyDetailsService.promotion(byId.getCompanyId(), 1);
                this.updateById(one);
                return R.ok("推廣成功");
            }else {
                one.setPromotion(true);
                one.setStartTime(LocalDateTime.now());
                LocalDateTime now = LocalDateTime.now();
                one.setEndTime(now.plusDays(1L));
                companyDetailsService.promotion(byId.getCompanyId(), 1);
                this.updateById(one);
                return R.ok("推廣成功");
            }
        }
    }

    @Override
    @Transactional
    public R promotionTenDay(Integer empId) {
        EmployeesDetails byId = employeesDetailsService.getById(empId);
        int tokens = companyDetailsService.getById(byId.getCompanyId()).getTokens().intValue();
        if(tokens<8){
            return R.failed("推廣失敗，您的代幣不足，請及時充值");
        }else {
            QueryWrapper<EmployeesPromotion> qw = new QueryWrapper<>();
            qw.eq("employees_id",empId);
            EmployeesPromotion one = this.getOne(qw);
            if(one.getPromotion()){
                one.setPromotion(true);
                one.setStartTime(one.getStartTime());
                one.setEndTime(one.getEndTime().plusDays(10L));
                companyDetailsService.promotion(byId.getCompanyId(), 8);
                this.updateById(one);
                return R.ok("推廣成功");
            }else {
                one.setPromotion(true);
                one.setStartTime(LocalDateTime.now());
                LocalDateTime now = LocalDateTime.now();
                one.setEndTime(now.plusDays(10L));
                companyDetailsService.promotion(byId.getCompanyId(), 8);
                this.updateById(one);
                return R.ok("推廣成功");
            }
        }
    }

    @Override
    public R getEmpByRan(Integer random) {
        List<Integer> empIds = baseMapper.getEmpByRan(random);
        List<EmployeesDetails> employeesDetails = new ArrayList<>();
        for (int i = 0; i < empIds.size(); i++) {
            EmployeesDetails byId = employeesDetailsService.getById(empIds.get(i));
            employeesDetails.add(byId);
        }if(CollectionUtils.isEmpty(employeesDetails)){
            return R.ok(null);
        }else {
            return R.ok(employeesDetails);
        }

    }

    @Override
    @Transactional
    public R getEmpInfoByCompanyId(Integer empId,String empName) {
        Integer companyId = companyDetailsService.getCompanyIdByUserId(TokenUtils.getCurrentUserId());
        List<EmployeesPromotionDTO> employeesPromotionDTOS = baseMapper.getEmpInfoByCompanyId(companyId,empId,empName);
        for (int i = 0; i < employeesPromotionDTOS.size(); i++) {
            if(CommonUtils.isEmpty(employeesPromotionDTOS.get(i).getEndTime())||LocalDateTime.now().isAfter(employeesPromotionDTOS.get(i).getEndTime())){
                employeesPromotionDTOS.get(i).setPromotion(false);
                employeesPromotionDTOS.get(i).setStartTime(null);
                employeesPromotionDTOS.get(i).setDays(0);
            }else {
                employeesPromotionDTOS.get(i).setPromotion(true);
                Duration duration = Duration.between(employeesPromotionDTOS.get(i).getStartTime(),LocalDateTime.now());
                int days = (int)duration.toDays();
                employeesPromotionDTOS.get(i).setDays(days);
            }
        }
        return R.ok(employeesPromotionDTOS);
    }
}
