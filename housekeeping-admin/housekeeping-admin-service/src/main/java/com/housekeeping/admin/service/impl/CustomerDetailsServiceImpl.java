package com.housekeeping.admin.service.impl;

import com.aliyun.oss.OSSClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.CustomerUpdateDTO;
import com.housekeeping.admin.entity.CustomerAddress;
import com.housekeeping.admin.entity.CustomerDetails;
import com.housekeeping.admin.entity.User;
import com.housekeeping.admin.mapper.CustomerDetailsMapper;
import com.housekeeping.admin.service.ICustomerAddressService;
import com.housekeeping.admin.service.ICustomerDetailsService;
import com.housekeeping.admin.service.IUserService;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
            QueryWrapper<User> qw = new QueryWrapper<>();
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
                Page pages = PageUtils.getPages((int) page.getCurrent(), (int) page.getSize(), users);
                return R.ok(pages);
            }
            if(CommonUtils.isNotEmpty(name)){
                List<CustomerDetails> search = search(name, userIds3);
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
