package com.housekeeping.admin.service.impl;

import com.aliyun.oss.OSSClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.CompanyDetailsDTO;
import com.housekeeping.admin.dto.CompanyDetailsPageDTO;
import com.housekeeping.admin.dto.CompanyDetailsUpdateDTO;
import com.housekeeping.admin.entity.*;
import com.housekeeping.admin.mapper.CompanyDetailsMapper;
import com.housekeeping.admin.service.EmployeesDetailsService;
import com.housekeeping.admin.service.ICompanyDetailsService;
import com.housekeeping.admin.service.ICompanyScaleService;
import com.housekeeping.admin.service.ISysOrderService;
import com.housekeeping.common.utils.CommonConstants;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


@Service("companyDetailsService")
public class CompanyDetailsServiceImpl extends ServiceImpl<CompanyDetailsMapper, CompanyDetails> implements ICompanyDetailsService {

    @Resource
    private OSSClient ossClient;
    @Value("${oss.bucketName}")
    private String bucketName;
    @Value("${oss.urlPrefix}")
    private String urlPrefix;
    @Resource
    private EmployeesDetailsService employeesDetailsService;
    @Resource
    private ICompanyScaleService companyScaleService;
    @Resource
    ISysOrderService sysOrderService;
    @Override
    public String uploadLogo(MultipartFile file, Integer reviserId) throws IOException {

        String res = "";

        LocalDateTime now = LocalDateTime.now();
        String nowString = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String catalogue = CommonConstants.HK_COMPANY_LOGO_ABSTRACT_PATH_PREFIX_PROV + reviserId;
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
    public String uploadFiveImg(MultipartFile[] files, Integer reviserId) {

        AtomicReference<String> res = new AtomicReference<>("");

        LocalDateTime now = LocalDateTime.now();
        String nowString = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String catalogue = CommonConstants.HK_COMPANY_IMG_ABSTRACT_PATH_PREFIX_PROV + reviserId;
        File mkdir = new File(catalogue);
        if (!mkdir.exists()){
            mkdir.mkdirs();
        }
        AtomicReference<Integer> count = new AtomicReference<>(0);
        Arrays.stream(files).forEach(file -> {
            String fileType = file.getOriginalFilename().split("\\.")[1];
            String fileName = nowString + "[" + count.toString() + "]."+ fileType;
            String fileAbstractPath = catalogue + "/" + fileName;
            try {
                ossClient.putObject(bucketName, fileAbstractPath, new ByteArrayInputStream(file.getBytes()));
                res.set(urlPrefix + fileAbstractPath + " " + res.get());
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                count.getAndSet(count.get() + 1);
            }
        });

        return res.get().trim();
    }

    @Override
    public R updateLogUrlByUserId(String logoUrl, Integer id) {
        baseMapper.updateLogoUrlById(logoUrl, id);
        return R.ok();
    }

    @Override
    public R updateFiveImgUrlByUserId(String imgUrl, Integer id) {
        baseMapper.updateFiveImgUrlByUserId(imgUrl, id);
        return null;
    }

    @Override
    public String getLogoUrlByUserId(Integer userId) {
        return baseMapper.getLogoUrlByUserId(userId);
    }

    @Override
    public String getPhotosByUserId(Integer userId) {
        return baseMapper.getPhotosByUserId(userId);
    }

    @Override
    public void updateById(CompanyDetailsDTO companyDetailsDTO, Integer lastReviserId) {
        baseMapper.updateById(companyDetailsDTO, lastReviserId);
    }

    @Override
    public void updateById2(CompanyDetailsUpdateDTO dto, Integer lastReviserId) {
        baseMapper.updateById2(dto, lastReviserId);
    }

    @Override
    public Integer getCompanyIdByUserId(Integer userId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        CompanyDetails companyDetails = baseMapper.selectOne(queryWrapper);
        return companyDetails.getId();
    }

    @Override
    public R pageOfCompanyByAdmin(IPage<CompanyDetails> page, CompanyDetailsPageDTO companyDetailsPageDTO) {
        QueryWrapper queryWrapper = new QueryWrapper();
        if (CommonUtils.isNotEmpty(companyDetailsPageDTO.getNumber())){
            queryWrapper.like("number", companyDetailsPageDTO.getNumber());
        }
        if (CommonUtils.isNotEmpty(companyDetailsPageDTO.getCompanyName())){
            queryWrapper.like("company_name", companyDetailsPageDTO.getCompanyName());
        }
        if (CommonUtils.isNotEmpty(companyDetailsPageDTO.getCompanySizeId())){
            queryWrapper.eq("company_size_id", companyDetailsPageDTO.getCompanySizeId());
        }
        if (CommonUtils.isNotEmpty(companyDetailsPageDTO.getLegalPerson())){
            queryWrapper.eq("legal_person", companyDetailsPageDTO.getLegalPerson());
        }
        if (CommonUtils.isNotEmpty(companyDetailsPageDTO.getIsValidate())){
            queryWrapper.eq("is_validate", companyDetailsPageDTO.getIsValidate());
        }
        if (CommonUtils.isNotEmpty(companyDetailsPageDTO.getIndustrialNumber())){
            queryWrapper.eq("industrial_number", companyDetailsPageDTO.getIndustrialNumber());
        }
        if (CommonUtils.isNotEmpty(companyDetailsPageDTO.getAddress1())){
            queryWrapper.like("address1", companyDetailsPageDTO.getAddress1());
        }
        if (CommonUtils.isNotEmpty(companyDetailsPageDTO.getAddress2())){
            queryWrapper.like("address2", companyDetailsPageDTO.getAddress2());
        }
        if (CommonUtils.isNotEmpty(companyDetailsPageDTO.getAddress3())){
            queryWrapper.like("address3", companyDetailsPageDTO.getAddress3());
        }
        if (CommonUtils.isNotEmpty(companyDetailsPageDTO.getAddress4())){
            queryWrapper.like("address4", companyDetailsPageDTO.getAddress4());
        }
        if (CommonUtils.isNotEmpty(companyDetailsPageDTO.getServiceHotline())){
            queryWrapper.like("service_hotline", companyDetailsPageDTO.getServiceHotline());
        }
        if (CommonUtils.isNotEmpty(companyDetailsPageDTO.getEmail())){
            queryWrapper.like("email", companyDetailsPageDTO.getEmail());
        }
        if (CommonUtils.isNotEmpty(companyDetailsPageDTO.getWebPages())){
            queryWrapper.like("web_pages", companyDetailsPageDTO.getWebPages());
        }
        if (CommonUtils.isNotEmpty(companyDetailsPageDTO.getAccountLine())){
            queryWrapper.like("account_line", companyDetailsPageDTO.getAccountLine());
        }
        if (CommonUtils.isNotEmpty(companyDetailsPageDTO.getConnectionFacebook())){
            queryWrapper.like("connection_facebook", companyDetailsPageDTO.getConnectionFacebook());
        }
        if (CommonUtils.isNotEmpty(companyDetailsPageDTO.getConnectionInstagram())){
            queryWrapper.like("connection_instagram", companyDetailsPageDTO.getConnectionInstagram());
        }
        if (CommonUtils.isNotEmpty(companyDetailsPageDTO.getDescribes())){
            queryWrapper.like("describes", companyDetailsPageDTO.getDescribes());
        }
        if (CommonUtils.isNotEmpty(companyDetailsPageDTO.getMethodPayment())){
            queryWrapper.like("method_payment", companyDetailsPageDTO.getMethodPayment());
        }
        if (CommonUtils.isNotEmpty(companyDetailsPageDTO.getCreateTimeStart())){
            queryWrapper.ge("create_time", companyDetailsPageDTO.getCreateTimeStart());
        }
        if (CommonUtils.isNotEmpty(companyDetailsPageDTO.getCreateTimeEnd())){
            queryWrapper.le("create_time", companyDetailsPageDTO.getCreateTimeEnd());
        }
        IPage<CompanyDetails> companyDetailsIPage = baseMapper.selectPage(page, queryWrapper);
        return R.ok(companyDetailsIPage, "查詢公司成功");
    }

    @Override
    public void authSuccess(Integer companyId, String companyName) {
        baseMapper.authSuccess(companyId, companyName);
    }

    @Transactional
    @Override
    public R getFiveTokens() {
        Integer currentUserId = TokenUtils.getCurrentUserId();
        QueryWrapper<CompanyDetails> qw = new QueryWrapper<>();
        qw.eq("user_id",currentUserId);
        CompanyDetails one = this.getOne(qw);
        if(one.getIsNew()){
            baseMapper.cusUpdateById(one.getTokens(),one.getId());
            return R.ok("您的賬戶已新增5個代幣");
        }else {
            return R.failed("您的賬戶已領取過，請勿重複領取");
        }
    }

    @Override
    public R buyHundredTokens() {
        Integer currentUserId = TokenUtils.getCurrentUserId();
        QueryWrapper<CompanyDetails> qw = new QueryWrapper<>();
        qw.eq("user_id",currentUserId);
        CompanyDetails one = this.getOne(qw);
        baseMapper.buyTokens(one.getTokens(),one.getId(),100);
        return R.ok("購買一百代幣成功");
    }

    @Override
    public R buyThousandTokens() {
        Integer currentUserId = TokenUtils.getCurrentUserId();
        QueryWrapper<CompanyDetails> qw = new QueryWrapper<>();
        qw.eq("user_id",currentUserId);
        CompanyDetails one = this.getOne(qw);
        baseMapper.buyTokens(one.getTokens(),one.getId(),1000);
        return R.ok("購買一千代幣成功");
    }

    @Override
    public Boolean thereIsACleaner(Integer employeesId) {
        Integer userId = TokenUtils.getCurrentUserId();
        QueryWrapper qw = new QueryWrapper();
        qw.eq("user_id", userId);
        CompanyDetails companyDetails = this.getOne(qw);
        Integer companyId = companyDetails.getId();
        EmployeesDetails employeesDetails = employeesDetailsService.getById(employeesId);

        return employeesDetails.getCompanyId().equals(companyId);
    }

    @Override
    public R getPay(Integer type) {
        Integer currentUserId = TokenUtils.getCurrentUserId();
        QueryWrapper<CompanyDetails> qw = new QueryWrapper<>();
        qw.eq("user_id",currentUserId);
        CompanyDetails one = this.getOne(qw);
        if(CommonUtils.isEmpty(one)){
            return R.failed("当前账户不是公司账户");
        }
        CompanyScale companyScale = companyScaleService.getById(one.getCompanySizeId());
        if(type.equals(0)){
            return R.ok(companyScale.getMonthPrice());
        }else {
            return R.ok(companyScale.getYearPrice());
        }
    }

    @Override
    public R pay(Integer type) {
        Integer companyId = this.getCompanyIdByUserId(TokenUtils.getCurrentUserId());
        CompanyDetails company = this.getById(companyId);
        if(CommonUtils.isEmpty(company.getExpireDate())||LocalDateTime.now().isAfter(company.getExpireDate())){
            LocalDateTime now = LocalDateTime.now();
            if(type.equals(0)){
                company.setExpireDate(now.plusDays(30L));
            }else {
                company.setExpireDate(now.plusDays(365L));
            }

        }else {
            if(type.equals(0)){
                company.setExpireDate(company.getExpireDate().plusDays(30L));
            }else {
                company.setExpireDate(company.getExpireDate().plusDays(365L));
            }
        }
        company.setLastReviserId(TokenUtils.getCurrentUserId());
        this.updateById(company);
        return R.ok("续费成功");
    }

    public void promotion(Integer companyId,Integer tokens){
        CompanyDetails byId = this.getById(companyId);
        baseMapper.promotion(companyId,byId.getTokens(),tokens);
    }

    @Override
    public Boolean checkCompPay(Integer companyId) {
        Integer scaleSwitch = baseMapper.getScaleSwitch();
        List<Integer> empIds = employeesDetailsService.getAllIdsByCompanyId(companyId);
        int count = sysOrderService.count(new QueryWrapper<SysOrder>().lambda().in(SysOrder::getEmployeesId, empIds));
        CompanyDetails company = this.getById(companyId);
        Boolean Renewed = true;
        if(CommonUtils.isEmpty(company.getExpireDate())||LocalDateTime.now().isAfter(company.getExpireDate())){
            Renewed = false;
        }
        if(scaleSwitch.equals(0)||count<=3||Renewed){
            return false;
        }else {
            return true;
        }
    }

    @Override
    public R getAllCompany() {
        return null;
    }

}
