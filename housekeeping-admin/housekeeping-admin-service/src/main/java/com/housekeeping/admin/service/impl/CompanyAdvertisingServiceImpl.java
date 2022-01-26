package com.housekeeping.admin.service.impl;


import com.aliyun.oss.OSSClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.AdvertisingRenewalAdminVo;
import com.housekeeping.admin.dto.CompanyAdvertisingAdminVo;
import com.housekeeping.admin.entity.CompanyAdvertising;
import com.housekeeping.admin.entity.CompanyDetails;
import com.housekeeping.admin.mapper.CompanyAdvertisingMapper;
import com.housekeeping.admin.service.ICompanyAdvertisingService;
import com.housekeeping.admin.vo.*;
import com.housekeeping.common.utils.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;

@Service("companyAdvertisingService")
public class CompanyAdvertisingServiceImpl extends ServiceImpl<CompanyAdvertisingMapper, CompanyAdvertising> implements ICompanyAdvertisingService {

    @Resource
    private CompanyDetailsServiceImpl companyDetailsService;

    @Resource
    private OSSClient ossClient;

    @Value("${oss.bucketName}")
    private String bucketName;

    @Value("${oss.urlPrefix}")
    private String urlPrefix;

    @Transactional
    @Override
    public R add(CompanyAdvertisingVo companyAdvertising) {

        CompanyDetails company = companyDetailsService.getById(companyDetailsService.getCompanyIdByUserId(TokenUtils.getCurrentUserId()));
        Integer companyId = company.getId();
        int tokens = company.getTokens().intValue();
        int day = companyAdvertising.getDay().intValue();
        int payTokens = companyAdvertising.getTokens().intValue();
        if(tokens<payTokens){
            return R.failed("推廣失敗，您的代幣不足，請及時充值");
        }else {
            LocalDateTime now = LocalDateTime.now();
            CompanyAdvertising companyAdvertising1 = new CompanyAdvertising();
            companyAdvertising1.setTypeId(companyAdvertising.getType());
            companyAdvertising1.setCompanyId(companyId);
            companyAdvertising1.setContent(companyAdvertising.getContent());
            companyAdvertising1.setLink(companyAdvertising.getLink());
            companyAdvertising1.setPhoto(companyAdvertising.getPhoto());
            companyAdvertising1.setTitle(companyAdvertising.getTitle());
            companyAdvertising1.setPromotion(true);
            companyAdvertising1.setEndTime(now.plusDays(day));
            this.save(companyAdvertising1);
            companyDetailsService.promotion(companyId,payTokens);
            return R.ok("推廣成功");
        }
    }

    @Override
    public R cusUpdate(AdvertisingVo companyAdvertising) {
        CompanyAdvertising byId = this.getById(companyAdvertising.getId());
        byId.setTitle(companyAdvertising.getTitle());
        byId.setPhoto(companyAdvertising.getPhoto());
        byId.setLink(companyAdvertising.getLink());
        byId.setContent(companyAdvertising.getContent());
        this.updateById(byId);
        return R.ok("修改廣告成功");
    }

    @Override
    @Transactional
    public R renewal(AdvertisingRenewalVo companyAdvertising) {
        CompanyDetails company = companyDetailsService.getById(companyDetailsService.getCompanyIdByUserId(TokenUtils.getCurrentUserId()));
        Integer companyId = company.getId();
        int tokens = company.getTokens().intValue();
        int day = companyAdvertising.getDay().intValue();
        int payTokens = companyAdvertising.getTokens().intValue();
        if(tokens<payTokens){
            return R.failed("續費失敗，您的代幣不足，請及時充值");
        }else {
            CompanyAdvertising byId = this.getById(companyAdvertising.getId());
            if(CommonUtils.isEmpty(byId.getEndTime())||LocalDateTime.now().isAfter(byId.getEndTime())){
                byId.setPromotion(true);
                LocalDateTime now = LocalDateTime.now();
                byId.setEndTime(now.plusDays(day));
                companyDetailsService.promotion(companyId,payTokens);
                this.updateById(byId);
            }else {
                byId.setPromotion(true);
                byId.setEndTime(byId.getEndTime().plusDays(day));
                companyDetailsService.promotion(companyId,payTokens);
                this.updateById(byId);
            }
            return R.ok("續費成功");
        }
    }

    @Override
    public R getByCompanyId(Integer companyId,Integer typeId, Integer id, String name) {
        QueryWrapper<CompanyAdvertising> qw = new QueryWrapper<>();
        if(CommonUtils.isNotEmpty(companyId)){
            qw.eq("company_id",companyId);
        }
        if(CommonUtils.isNotEmpty(id)){
            qw.eq("id",id);
        }
        if(CommonUtils.isNotEmpty(typeId)){
            qw.eq("type_id",typeId);
        }
        if(CommonUtils.isNotEmpty(name)){
            qw.like("title",name);
        }
        List<CompanyAdvertising> list = this.list(qw);
        for (int i = 0; i < list.size(); i++) {
            if(CommonUtils.isEmpty(list.get(i).getEndTime())||LocalDateTime.now().isAfter(list.get(i).getEndTime())){
                list.get(i).setPromotion(false);
            }else {
                list.get(i).setPromotion(true);
            }
        }
        return R.ok(list);
    }

    @Override
    public R getByRan(Integer typeId,Integer ran) {
        return R.ok(baseMapper.getByRan(typeId,ran));
    }

    @Override
    public R uploadPhoto(MultipartFile file) {
        String res = "";

        LocalDateTime now = LocalDateTime.now();
        String nowString = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String catalogue = CommonConstants.HK_COMPANY_LOGO_ABSTRACT_PATH_PREFIX_PROV;
        String type = file.getOriginalFilename().split("\\.")[1];
        String fileAbstractPath = catalogue + "/" + nowString+"."+ type;

        try {
            ossClient.putObject(bucketName, fileAbstractPath, new ByteArrayInputStream(file.getBytes()));
            res = urlPrefix + fileAbstractPath;
        } catch (Exception e) {
            e.printStackTrace();
            return R.failed("error upload");
        }

        return R.ok(res);
    }

    @Override
    public R getByUserId(Integer userId, Integer id, String name) {
        QueryWrapper<CompanyAdvertising> qw = new QueryWrapper<>();
        if(CommonUtils.isNotEmpty(userId)){
            QueryWrapper<CompanyDetails> qw2 = new QueryWrapper<>();
            qw2.eq("user_id",userId);
            CompanyDetails one = companyDetailsService.getOne(qw2);
            qw.eq("company_id",one.getId());
        }
        if(CommonUtils.isNotEmpty(id)){
            qw.eq("id",id);
        }
        if(CommonUtils.isNotEmpty(name)){
            qw.like("title",name);
        }
        qw.gt("end_time",LocalDateTime.now());
        List<CompanyAdvertising> list = this.list(qw);
        for (int i = 0; i < list.size(); i++) {
            if(CommonUtils.isEmpty(list.get(i).getEndTime())||LocalDateTime.now().isAfter(list.get(i).getEndTime())){
                list.get(i).setPromotion(false);
            }else {
                list.get(i).setPromotion(true);
            }
        }
        return R.ok(list);
    }

    @Override
    public R addByAdmin(CompanyAdvertisingAdminVo companyAdvertising) {
        Integer companyId = companyAdvertising.getCompanyId();
        int day = companyAdvertising.getDay().intValue();
            LocalDateTime now = LocalDateTime.now();
            CompanyAdvertising companyAdvertising1 = new CompanyAdvertising();
            companyAdvertising1.setTypeId(companyAdvertising.getTypeId());
            companyAdvertising1.setCompanyId(companyId);
            companyAdvertising1.setContent(companyAdvertising.getContent());
            companyAdvertising1.setLink(companyAdvertising.getLink());
            companyAdvertising1.setPhoto(companyAdvertising.getPhoto());
            companyAdvertising1.setTitle(companyAdvertising.getTitle());
            companyAdvertising1.setPromotion(true);
            companyAdvertising1.setEndTime(now.plusDays(day));
            this.save(companyAdvertising1);
            return R.ok("推廣成功");
    }

    @Override
    public R renewalByAdmin(AdvertisingRenewalAdminVo companyAdvertising) {
        int day = companyAdvertising.getDay().intValue();
            CompanyAdvertising byId = this.getById(companyAdvertising.getId());
            if(CommonUtils.isEmpty(byId.getEndTime())||LocalDateTime.now().isAfter(byId.getEndTime())){
                byId.setPromotion(true);
                LocalDateTime now = LocalDateTime.now();
                byId.setEndTime(now.plusDays(day));
                this.updateById(byId);
            }
                byId.setPromotion(true);
                byId.setEndTime(byId.getEndTime().plusDays(day));
                this.updateById(byId);
            return R.ok("續費成功");
    }

    @Override
    public R getByAdmin(Integer id, String name, Boolean status) {
        QueryWrapper<CompanyAdvertising> qw = new QueryWrapper<>();
        if(CommonUtils.isNotEmpty(id)){
            qw.eq("id",id);
        }
        if(CommonUtils.isNotEmpty(name)){
            qw.like("title",name);
        }
        List<CompanyAdvertising> list = this.list(qw);
        for (int i = 0; i < list.size(); i++) {
            if(CommonUtils.isEmpty(list.get(i).getEndTime())||LocalDateTime.now().isAfter(list.get(i).getEndTime())){
                list.get(i).setPromotion(false);
            }else {
                list.get(i).setPromotion(true);
            }
        }
        if(CommonUtils.isNotEmpty(status)){
            Iterator<CompanyAdvertising> iterator = list.iterator();
            if(status){
                while (iterator.hasNext()) {
                   CompanyAdvertising s = iterator.next();
                   if (s.getPromotion()!=true) {
                       iterator.remove();
                   }
               }
           }else {
                while (iterator.hasNext()) {
                   CompanyAdvertising s = iterator.next();
                   if (s.getPromotion()==true) {
                       iterator.remove();
                   }
               }
           }
        }
        return R.ok(list);
    }

    @Override
    public R getAllAdverByAdmin(Page page, Integer id, String title, Boolean status) {
        List<CompanyAdvertingVo> companyAdvertisingVos = baseMapper.getAllAdderByAdmin(id,title);
        for (int i = 0; i < companyAdvertisingVos.size(); i++) {
            if(CommonUtils.isEmpty(companyAdvertisingVos.get(i).getEndTime())||LocalDateTime.now().isAfter(companyAdvertisingVos.get(i).getEndTime())){
                companyAdvertisingVos.get(i).setPromotion(false);
            }else {
                companyAdvertisingVos.get(i).setPromotion(true);
            }
        }
        if(CommonUtils.isNotEmpty(status)){
            Iterator<CompanyAdvertingVo> iterator = companyAdvertisingVos.iterator();
            if(status){
                while (iterator.hasNext()) {
                    CompanyAdvertising s = iterator.next();
                    if (s.getPromotion()!=true) {
                        iterator.remove();
                    }
                }
            }else {
                while (iterator.hasNext()) {
                    CompanyAdvertising s = iterator.next();
                    if (s.getPromotion()==true) {
                        iterator.remove();
                    }
                }
            }
        }
        Page pages = PageUtils.getPages((int) page.getCurrent(), (int) page.getSize(), companyAdvertisingVos);
        return R.ok(pages);
    }

}
