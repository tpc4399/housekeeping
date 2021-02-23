package com.housekeeping.admin.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.CompanyPromotionDTO;
import com.housekeeping.admin.entity.CompanyDetails;
import com.housekeeping.admin.entity.CompanyPromotion;
import com.housekeeping.admin.mapper.CompanyPromotionMapper;
import com.housekeeping.admin.service.ICompanyDetailsService;
import com.housekeeping.admin.service.ICompanyPromotionService;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service("companyPromotionService")
public class CompanyPromotionServiceImpl extends ServiceImpl<CompanyPromotionMapper, CompanyPromotion> implements ICompanyPromotionService {

    @Resource
    private CompanyDetailsServiceImpl companyDetailsService;
    @Override
    public R getInfoById(Integer companyId) {
        CompanyPromotionDTO companyPromotionDTO = baseMapper.getInfoById(companyId);
        LocalDateTime endTime = companyPromotionDTO.getEndTime();
        if(CommonUtils.isEmpty(companyPromotionDTO.getEndTime())){
            return R.ok(companyPromotionDTO);
        }else {
            if(LocalDateTime.now().isAfter(endTime)){
                companyPromotionDTO.setPromotion(false);
            }
        }
        return R.ok(companyPromotionDTO);
    }

    @Override
    @Transactional
    public R promotionDay(Integer companyId) {
        int tokens = companyDetailsService.getById(companyId).getTokens().intValue();
        if(tokens<1){
            return R.failed("推廣失敗，您的代幣不足，請及時充值");
        }else {
            QueryWrapper<CompanyPromotion> qw = new QueryWrapper<>();
            qw.eq("company_id",companyId);
            CompanyPromotion one = this.getOne(qw);
            if(CommonUtils.isEmpty(one.getEndTime())||LocalDateTime.now().isAfter(one.getEndTime())){
                one.setPromotion(true);
                one.setDays(one.getDays()+1);
                LocalDateTime now = LocalDateTime.now();
                one.setEndTime(now.plusDays(1L));
                one.setLastReviserId(TokenUtils.getCurrentUserId());
                companyDetailsService.promotion(companyId,1);
                this.updateById(one);
                return R.ok("推廣成功");

            }else {
                one.setPromotion(true);
                one.setDays(one.getDays()+1);
                one.setEndTime(one.getEndTime().plusDays(1L));
                one.setLastReviserId(TokenUtils.getCurrentUserId());
                companyDetailsService.promotion(companyId,1);
                this.updateById(one);
                return R.ok("推廣成功");
            }
        }
    }

    @Override
    @Transactional
    public R promotionTenDay(Integer companyId) {
        int tokens = companyDetailsService.getById(companyId).getTokens().intValue();
        if(tokens<8){
            return R.failed("推廣失敗，您的代幣不足，請及時充值");
        }else {
            QueryWrapper<CompanyPromotion> qw = new QueryWrapper<>();
            qw.eq("company_id",companyId);
            CompanyPromotion one = this.getOne(qw);
            if(CommonUtils.isEmpty(one.getEndTime())||LocalDateTime.now().isAfter(one.getEndTime())){
                one.setPromotion(true);
                one.setDays(one.getDays()+10);
                LocalDateTime now = LocalDateTime.now();
                one.setEndTime(now.plusDays(10L));
                one.setLastReviserId(TokenUtils.getCurrentUserId());
                companyDetailsService.promotion(companyId,8);
                this.updateById(one);
                return R.ok("推廣成功");
            }else {
                one.setPromotion(true);
                one.setDays(one.getDays()+10);
                one.setEndTime(one.getEndTime().plusDays(10L));
                one.setLastReviserId(TokenUtils.getCurrentUserId());
                companyDetailsService.promotion(companyId,8);
                this.updateById(one);
                return R.ok("推廣成功");
            }
        }
    }

    @Override
    public R getCompanyByRan(Integer random) {
        List<Integer> companyIds = baseMapper.getCompanyByRan(random);
        List<CompanyDetails> companyDetails = new ArrayList<>();
        for (int i = 0; i < companyIds.size(); i++) {
            CompanyDetails byId = companyDetailsService.getById(companyIds.get(i));
            companyDetails.add(byId);
        }if(CollectionUtils.isEmpty(companyDetails)){
            return R.ok(null);
        }else {
            return R.ok(companyDetails);
        }

    }

    @Override
    public R getAllProCompany() {
        List<Integer> companyIds = baseMapper.getAllProCompIds();
        List<CompanyDetails> companyDetails = new ArrayList<>();
        for (int i = 0; i < companyIds.size(); i++) {
            CompanyDetails byId = companyDetailsService.getById(companyIds.get(i));
            companyDetails.add(byId);
        }if(CollectionUtils.isEmpty(companyDetails)){
            return R.ok(null);
        }else {
            return R.ok(companyDetails);
        }
    }

    @Override
    public R promotionByAdmin(Integer companyId, Integer days) {
            QueryWrapper<CompanyPromotion> qw = new QueryWrapper<>();
            qw.eq("company_id",companyId);
            CompanyPromotion one = this.getOne(qw);
            if(CommonUtils.isEmpty(one.getEndTime())||LocalDateTime.now().isAfter(one.getEndTime())){
                one.setPromotion(true);
                one.setDays(one.getDays()+days);
                LocalDateTime now = LocalDateTime.now();
                one.setEndTime(now.plusDays(days));
                one.setLastReviserId(TokenUtils.getCurrentUserId());
                this.updateById(one);
                return R.ok("推廣成功");
            }else {
                one.setPromotion(true);
                one.setDays(one.getDays()+days);
                one.setEndTime(one.getEndTime().plusDays(days));
                one.setLastReviserId(TokenUtils.getCurrentUserId());
                this.updateById(one);
                return R.ok("推廣成功");
        }
    }

    public List<Integer> getAllProCompIds(){
        return baseMapper.getAllProCompIds();
    }

}
