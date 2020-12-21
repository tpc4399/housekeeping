package com.housekeeping.admin.service.impl;


import com.aliyun.oss.OSSClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.ManagerDetailsDTO;
import com.housekeeping.admin.dto.PageOfManagerDTO;
import com.housekeeping.admin.dto.PageOfManagerDetailsDTO;
import com.housekeeping.admin.entity.CompanyDetails;
import com.housekeeping.admin.entity.EmployeesDetails;
import com.housekeeping.admin.entity.ManagerDetails;
import com.housekeeping.admin.entity.User;
import com.housekeeping.admin.mapper.ManagerDetailsMapper;
import com.housekeeping.admin.service.ICompanyDetailsService;
import com.housekeeping.admin.service.IUserService;
import com.housekeeping.admin.service.ManagerDetailsService;
import com.housekeeping.common.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service("managerDetailsService")
public class ManagerDetailsServiceImpl extends ServiceImpl<ManagerDetailsMapper, ManagerDetails> implements ManagerDetailsService {

    @Autowired
    private ICompanyDetailsService companyDetailsService;

    @Autowired
    private IUserService userService;

    @Autowired
    private RedisUtils redisUtils;

    @Resource
    private OSSClient ossClient;

    @Value("${oss.bucketName}")
    private String bucketName;

    @Value("${oss.urlPrefix}")
    private String urlPrefix;

    @Override
    public R saveEmp(ManagerDetailsDTO managerDetailsDTO) {
        if(this.addManager()){
            if(CommonUtils.isNotEmpty(managerDetailsDTO)){
                //先保存User
                User user = new User();
                String ss = String.valueOf(System.currentTimeMillis());
                user.setNumber("c"+ss);
                user.setDeptId(4);
                user.setLastReviserId(TokenUtils.getCurrentUserId());
                user.setCreateTime(LocalDateTime.now());
                user.setUpdateTime(LocalDateTime.now());
                Integer maxUserId = 0;
                synchronized (this) {
                    userService.save(user);
                    maxUserId = ((User) CommonUtils.getMaxId("sys_user", userService)).getId();
                }
                ManagerDetails managerDetails = new ManagerDetails();
                QueryWrapper<CompanyDetails> wrComp=new QueryWrapper<>();
                wrComp.inSql("id","select id from company_details where user_id=" + TokenUtils.getCurrentUserId());
                CompanyDetails one = companyDetailsService.getOne(wrComp);
                String s = String.valueOf(System.currentTimeMillis());
                managerDetails.setUserId(maxUserId);
                managerDetails.setNumber("man"+s);
                managerDetails.setName(managerDetailsDTO.getName());
                managerDetails.setDateOfBirth(managerDetailsDTO.getDateOfBirth());
                managerDetails.setPhone(managerDetailsDTO.getPhone());
                managerDetails.setAddress(managerDetailsDTO.getAddress());
                managerDetails.setDescribes(managerDetailsDTO.getDescribes());
                managerDetails.setSex(managerDetailsDTO.getSex());
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
    public R updateEmp(ManagerDetailsDTO managerDetailsDTO) {
        ManagerDetails managerDetails = new ManagerDetails();
        managerDetails.setId(managerDetailsDTO.getId());
        managerDetails.setName(managerDetailsDTO.getName());
        managerDetails.setDateOfBirth(managerDetailsDTO.getDateOfBirth());
        managerDetails.setPhone(managerDetailsDTO.getPhone());
        managerDetails.setAddress(managerDetailsDTO.getAddress());
        managerDetails.setDescribes(managerDetailsDTO.getDescribes());
        managerDetails.setSex(managerDetailsDTO.getSex());
        managerDetails.setLastReviserId(TokenUtils.getCurrentUserId());
        managerDetails.setCreateTime(LocalDateTime.now());
        managerDetails.setUpdateTime(LocalDateTime.now());
        if(this.updateById(managerDetails)){
            return R.ok("修改成功");
        }else {
            return R.failed("修改失敗");
        }

    }



    @Override
    public R getLinkToLogin(Integer id, Long h) throws UnknownHostException {
        ManagerDetails managerDetails = baseMapper.selectById(id);
        if (CommonUtils.isNotEmpty(managerDetails)){
            String mysteriousCode = CommonUtils.getMysteriousCode(); //神秘代码
            String key = CommonConstants.LOGIN_MANAGER_PREFIX + mysteriousCode;
            Integer value = managerDetails.getUserId();
            redisUtils.set(key, value, 60 * 60 * h);//有效期12小时
            //拼接url链接
            return R.ok(mysteriousCode);
        } else {
            return R.failed("經理不存在，請刷新頁面重試");
        }
    }

    @Override
    public Integer getCompanyIdByManagerId(Integer managerId) {
        ManagerDetails managerDetails = baseMapper.selectById(managerId);
        return managerDetails.getCompanyId();
    }

    @Override
    public R cusPage1(Page page, PageOfManagerDTO pageOfEmployeesDTO, String requestOriginAdmin) {
        QueryWrapper  queryWrapper = new QueryWrapper();
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
        IPage<ManagerDetails> managerDetailsIPage = baseMapper.selectPage(page, queryWrapper);
        return R.ok(managerDetailsIPage, "分頁查詢成功");
    }

    @Override
    public R cusPage(Page page, PageOfManagerDetailsDTO pageOfEmployeesDetailsDTO, String type) {
        QueryWrapper  queryWrapper = new QueryWrapper();
        if (CommonUtils.isNotEmpty(pageOfEmployeesDetailsDTO.getId())){
            queryWrapper.eq("id", pageOfEmployeesDetailsDTO.getId());
        }
        if (CommonUtils.isNotEmpty(pageOfEmployeesDetailsDTO.getName())){
            queryWrapper.like("name", pageOfEmployeesDetailsDTO.getName());
        }
        if (CommonUtils.isNotEmpty(pageOfEmployeesDetailsDTO.getAccountLine())){
            queryWrapper.like("account_line", pageOfEmployeesDetailsDTO.getAccountLine());
        }
        if (type.equals(CommonConstants.REQUEST_ORIGIN_COMPANY)){
            Integer userId = TokenUtils.getCurrentUserId();
            Integer companyId = companyDetailsService.getCompanyIdByUserId(userId);
            queryWrapper.eq("company_id", companyId);
        }
        IPage<EmployeesDetails> employeesDetailsIPage = baseMapper.selectPage(page, queryWrapper);
        return R.ok(employeesDetailsIPage, "分頁查詢成功");
    }

    @Override
    public String uploadHead(MultipartFile file, Integer id) throws IOException {
        String res = "";

        LocalDateTime now = LocalDateTime.now();
        String nowString = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String catalogue = CommonConstants.HK_MANAGER_HEAD_ABSTRACT_PATH_PREFIX_PROV + id;
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
    public List<Integer> getManIdsByCompId(Integer id) {
        return baseMapper.getManIdsByCompId(id);
    }

    /**
     * 判斷公司是否可以新增員工
     * @return
     */
    public Boolean addManager(){
        Integer userId = TokenUtils.getCurrentUserId();
        QueryWrapper<CompanyDetails> wrComp = new QueryWrapper<>();
        wrComp.inSql("id","select id from company_details where user_id=" + userId);
        CompanyDetails one = companyDetailsService.getOne(wrComp);
        String scaleById = baseMapper.getScaleById(one.getCompanySizeId());
        String[] split = scaleById.split("~");
        Integer companyMaxsize;
        if(split[1].equals("n")){
            companyMaxsize = Integer.MAX_VALUE;
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
