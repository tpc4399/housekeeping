package com.housekeeping.admin.service.impl;

import com.aliyun.oss.OSSClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.CompanyDetailsPageDTO;
import com.housekeeping.admin.dto.CustomerUpdateDTO;
import com.housekeeping.admin.dto.PageOfEmployeesDTO;
import com.housekeeping.admin.entity.*;
import com.housekeeping.admin.mapper.CustomerDetailsMapper;
import com.housekeeping.admin.mapper.EmployeesDetailsMapper;
import com.housekeeping.admin.service.*;
import com.housekeeping.admin.vo.CancelCollectionVO;
import com.housekeeping.admin.vo.EmployeesDetailsSkillVo;
import com.housekeeping.common.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Author su
 * @create 2020/11/23 10:56
 */
@Service("customerDetailsService")
public class CustomerDetailsServiceImpl extends ServiceImpl<CustomerDetailsMapper, CustomerDetails> implements ICustomerDetailsService {

    @Resource
    private OSSClient ossClient;

    @Resource
    private CompanyDetailsServiceImpl companyDetailsService;

    @Resource
    private IUserService userService;

    @Value("${oss.bucketName}")
    private String bucketName;

    @Value("${oss.urlPrefix}")
    private String urlPrefix;

    @Resource
    private ICustomerAddressService customerAddressService;

    @Resource
    private EmployeesDetailsMapper employeesDetailsMapper;

    @Resource
    private ISysJobContendService sysJobContendService;

    @Resource
    private EmployeesDetailsService employeesDetailsService;

    @Override
    public R toDefault(Integer id) {
        Integer userId = TokenUtils.getCurrentUserId();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        CustomerDetails customerDetails = this.getOne(queryWrapper);

        QueryWrapper queryWrapper1 = new QueryWrapper();
        queryWrapper1.eq("id", id);
        queryWrapper1.eq("customer_id", customerDetails.getId());
        CustomerAddress customerAddress = customerAddressService.getOne(queryWrapper1);
        if (CommonUtils.isNotEmpty(customerAddress)){
            /** 設置為默認地址 */
            QueryWrapper queryWrapper2 = new QueryWrapper();
            queryWrapper2.eq("customer_id", customerDetails.getId());
            List<CustomerAddress> customerAddressList = customerAddressService.list(queryWrapper2);
            List<CustomerAddress> customerAddressList1 = customerAddressList.stream().map(x -> {
                x.setIsDefault(x.getId() == id);
                return x;
            }).collect(Collectors.toList());
            customerAddressService.updateBatchById(customerAddressList1);
            return R.ok("地址修改成功");
        }else {
            return R.failed("地址不存在");
        }
    }

    @Override
    public String uploadHead(MultipartFile file, Integer id) throws IOException {
        String res = "";

        LocalDateTime now = LocalDateTime.now();
        String nowString = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String catalogue = CommonConstants.HK_CUSTOMER_HEAD_ABSTRACT_PATH_PREFIX_PROV + id;
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
    public R updateHeadUrlByUserId(String headUrl, Integer id) {
        baseMapper.updateHeadUrlById(headUrl, id);
        return R.ok();
    }

    @Override
    public R getCustomerList(Page page,Integer cid, String name) {
        Integer userId = TokenUtils.getCurrentUserId();
        Integer companyId = companyDetailsService.getCompanyIdByUserId(userId);
        List<Integer> ids = baseMapper.getIdbyByCompanyId(companyId);
        List<Integer> ids5 = new ArrayList<>();
        List<CustomerDetails> userIds3 = new ArrayList<>();
        if(CollectionUtils.isEmpty(ids)){
            return R.ok(null);
        }
        else {
            Set<Integer> userIds = new HashSet<>();
            for (int i = 0; i < ids.size(); i++) {
                List<Integer> users = baseMapper.getUserIdByGId(ids.get(i));
                for (int j = 0; j < users.size(); j++) {
                    userIds.add(users.get(j));
                }
            }
            for (Integer id : userIds) {
                User one = userService.getUserByIdAndDept(id,3);
                if(CommonUtils.isNotEmpty(one)){
                    ids5.add(one.getId());
                }
            }
            for (int i = 0; i < ids5.size(); i++) {
                QueryWrapper<CustomerDetails> qw3 = new QueryWrapper<>();
                qw3.eq("user_id",ids5.get(i));
                CustomerDetails one = this.getOne(qw3);
                userIds3.add(one);
            }
            if(CommonUtils.isNotEmpty(cid)){
                List<CustomerDetails> users = search2(cid, userIds3);
                if(CommonUtils.isEmpty(users)){
                    return R.ok(null);
                }
                Page pages = PageUtils.getPages((int) page.getCurrent(), (int) page.getSize(), users);
                return R.ok(pages);
            }
            if(CommonUtils.isNotEmpty(name)){
                List<CustomerDetails> search = search(name, userIds3);
                if(CommonUtils.isEmpty(search)){
                    return R.ok(null);
                }
                Page pages = PageUtils.getPages((int) page.getCurrent(), (int) page.getSize(), search);
                return R.ok(pages);
            }else {
                Page pages = PageUtils.getPages((int) page.getCurrent(), (int) page.getSize(), userIds3);
                return R.ok(pages);
            }
        }
    }

    @Override
    public R updateCus(CustomerUpdateDTO customerUpdateDTO) {
        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setHeadUrl(customerUpdateDTO.getHeadUrl());
        customerDetails.setId(customerUpdateDTO.getId());
        customerDetails.setName(customerUpdateDTO.getName());
        customerDetails.setSex(customerUpdateDTO.getSex());
        return R.ok(this.updateById(customerDetails));
    }

    @Override
    public R blacklist(Integer customerId, Boolean action) {
        baseMapper.blacklist(customerId, action);
        return R.ok(null, "操作成功");
    }

    @Override
    public CustomerDetails getByUserId(Integer userId) {
        QueryWrapper qw = new QueryWrapper();
        qw.eq("user_id", userId);
        CustomerDetails cd = this.getOne(qw);
        return cd;
    }

    @Override
    public R collection(Integer empId) {
        //获取客户id
        QueryWrapper qw = new QueryWrapper();
        qw.eq("user_id", TokenUtils.getCurrentUserId());
        CustomerDetails cd = this.getOne(qw);
        if(cd==null){
            return R.failed("客戶不存在!");
        }

        Integer id = baseMapper.checkCollection(cd.getId(), empId);
        if(id != null){
            return R.failed("已收藏該員工，請勿重複收藏");
        }

        //将员工加入收藏
        baseMapper.collection(cd.getId(),empId);
        return R.ok("收藏成功");
    }

    @Override
    public R getCollectionList(PageOfEmployeesDTO pageOfEmployeesDTO) {
        //获取客户id
        QueryWrapper qw = new QueryWrapper();
        qw.eq("user_id", TokenUtils.getCurrentUserId());
        CustomerDetails customerDetails = this.getOne(qw);
        if(customerDetails==null){
            return R.failed("客戶不存在!");
        }

        List<Integer> empIds = baseMapper.getAllEmpId(customerDetails.getId());

        QueryWrapper<EmployeesDetails>  queryWrapper = new QueryWrapper();
        if (CommonUtils.isNotEmpty(pageOfEmployeesDTO.getId())){
            queryWrapper.eq("id", pageOfEmployeesDTO.getId());
        }
        if (CommonUtils.isNotEmpty(pageOfEmployeesDTO.getName())){
            queryWrapper.like("name", pageOfEmployeesDTO.getName());
        }
        if (CommonUtils.isNotEmpty(pageOfEmployeesDTO.getAccountLine())){
            queryWrapper.like("account_line", pageOfEmployeesDTO.getAccountLine());
        }
        if (CommonUtils.isNotEmpty(pageOfEmployeesDTO.getCompanyId())){
            queryWrapper.like("company_id", pageOfEmployeesDTO.getCompanyId());
        }
        List<EmployeesDetails> list = employeesDetailsService.list(queryWrapper);

        List<EmployeesDetails> collect1 = list.stream().filter(x -> {
            Integer id = x.getId();
            if (empIds.contains(id)) {
                return true;
            } else {
                return false;
            }
        }).collect(Collectors.toList());

        List<EmployeesDetailsSkillVo> collect = collect1.stream().map(x -> {
            EmployeesDetailsSkillVo byId = employeesDetailsMapper.getCusById(x.getId());
            /** certified:員工認證準備 */
            Integer certified;
            //不属于公司就是个体户 1个体户 2工作室 3公司
            if (byId.getCompanyId() == null) {
                certified = 1;
            } else {
                CompanyDetails cd = companyDetailsService.getById(byId.getCompanyId());
                Boolean isCertified = cd.getIsValidate();
                if (isCertified == false) {
                    certified = 2;
                } else {
                    certified = 3;
                }
            }

            if (CommonUtils.isEmpty(byId.getPresetJobIds()) || byId.getPresetJobIds().equals("")) {
                byId.setSkillTags(null);
            } else {
                String presetJobIds = byId.getPresetJobIds();
                List<Skill> skills = new ArrayList<>();
                List<String> strings = Arrays.asList(presetJobIds.split(" "));
                for (int i = 0; i < strings.size(); i++) {
                    Skill skill = new Skill();
                    skill.setJobId(Integer.parseInt(strings.get(i)));
                    skill.setContend(sysJobContendService.getById(Integer.parseInt(strings.get(i))).getContend());
                    skills.add(skill);
                }
                byId.setSkillTags(skills);
                byId.setCertified(certified);
            }
            return byId;
        }).collect(Collectors.toList());
        return R.ok(collect);
    }

    @Override
    public R cancelCollection(String ids) {
        //获取客户id
        QueryWrapper qw = new QueryWrapper();
        qw.eq("user_id", TokenUtils.getCurrentUserId());
        CustomerDetails customerDetails = this.getOne(qw);
        if(customerDetails==null){
            return R.failed("客戶不存在!");
        }

        List<String> strings = Arrays.asList(ids.split(","));

        strings.forEach(x ->{
            baseMapper.cancelCollection(customerDetails.getId(),Integer.parseInt(x));
        });
        return R.ok("取消收藏成功");
    }

    @Override
    public R checkCollection(Integer empId) {

        //获取客户id
        QueryWrapper qw = new QueryWrapper();
        qw.eq("user_id", TokenUtils.getCurrentUserId());
        CustomerDetails customerDetails = this.getOne(qw);

        Integer id = baseMapper.checkCollection(customerDetails.getId(), empId);
        if(id == null){
            return R.ok(false);
        }else{
            return R.ok(true);
        }
    }

    @Override
    public R collectionCompany(Integer companyId) {
        //获取客户id
        QueryWrapper qw = new QueryWrapper();
        qw.eq("user_id", TokenUtils.getCurrentUserId());
        CustomerDetails cd = this.getOne(qw);
        if(cd==null){
            return R.failed("客戶不存在!");
        }

        Integer id = baseMapper.checkCollectionCompany(cd.getId(), companyId);
        if(id != null) {
            return R.failed("已收藏該公司，請勿重複收藏");
        }
        //将员工加入收藏
        baseMapper.collectionCompany(cd.getId(),companyId);
        return R.ok("收藏成功");
    }

    @Override
    public R getCollectionCompanyList(CompanyDetailsPageDTO companyDetailsPageDTO) {
        //获取客户id
        QueryWrapper qw = new QueryWrapper();
        qw.eq("user_id", TokenUtils.getCurrentUserId());
        CustomerDetails customerDetails = this.getOne(qw);
        if(customerDetails==null){
            return R.failed("客戶不存在!");
        }

        List<Integer> compId = baseMapper.getAllCompId(customerDetails.getId());

        QueryWrapper<CompanyDetails>  queryWrapper = new QueryWrapper();
        if (CommonUtils.isNotEmpty(companyDetailsPageDTO.getNumber())){
            queryWrapper.eq("number", companyDetailsPageDTO.getNumber());
        }
        if (CommonUtils.isNotEmpty(companyDetailsPageDTO.getIsValidate())){
            queryWrapper.eq("is_validate", companyDetailsPageDTO.getIsValidate());
        }
        if (CommonUtils.isNotEmpty(companyDetailsPageDTO.getNoCertifiedCompany())){
            queryWrapper.like("no_certified_company", companyDetailsPageDTO.getNoCertifiedCompany());
        }
        List<CompanyDetails> list = companyDetailsService.list(queryWrapper);

        List<CompanyDetails> collect = list.stream().filter(x -> {
            Integer id = x.getId();
            if (compId.contains(id)) {
                return true;
            } else {
                return false;
            }
        }).collect(Collectors.toList());
        return R.ok(collect);

    }

    @Override
    public R cancelCollectionCompany(String ids) {
        //获取客户id
        QueryWrapper qw = new QueryWrapper();
        qw.eq("user_id", TokenUtils.getCurrentUserId());
        CustomerDetails customerDetails = this.getOne(qw);
        if(customerDetails==null){
            return R.failed("客戶不存在!");
        }

        List<String> strings = Arrays.asList(ids.split(","));

        strings.forEach(x ->{
            baseMapper.cancelCollectionCompany(customerDetails.getId(),Integer.parseInt(x));
        });
        return R.ok("取消收藏成功");
    }

    @Override
    public R checkCollectionCompany(Integer companyId) {
        //获取客户id
        QueryWrapper qw = new QueryWrapper();
        qw.eq("user_id", TokenUtils.getCurrentUserId());
        CustomerDetails customerDetails = this.getOne(qw);

        Integer id = baseMapper.checkCollectionCompany(customerDetails.getId(), companyId);
        if(id == null){
            return R.ok(false);
        }else{
            return R.ok(true);
        }
    }


    public List<CustomerDetails> search(String name, List<CustomerDetails> list){
        List<CustomerDetails> results = new ArrayList();
        Pattern pattern = Pattern.compile(name);
        for(int i=0; i < list.size(); i++){
            Matcher matcher = pattern.matcher(((CustomerDetails)list.get(i)).getName());
            if(matcher.find()){
                results.add(list.get(i));
            }
        }
        return results;
    }

    public List<CustomerDetails> search2(Integer id,List<CustomerDetails> list){
        List<CustomerDetails> results = new ArrayList();
        Pattern pattern = Pattern.compile(id.toString());
        for(int i=0; i < list.size(); i++){
            Matcher matcher = pattern.matcher(((CustomerDetails)list.get(i)).getId().toString());
            if(matcher.matches()){
                results.add(list.get(i));
            }
        }
        return results;
    }
}
