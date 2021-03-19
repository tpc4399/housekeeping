package com.housekeeping.admin.service.impl;


import com.aliyun.oss.OSSClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.ManagerDetailsDTO;
import com.housekeeping.admin.dto.PageOfManagerDTO;
import com.housekeeping.admin.dto.PageOfManagerDetailsDTO;
import com.housekeeping.admin.entity.*;
import com.housekeeping.admin.mapper.ManagerDetailsMapper;
import com.housekeeping.admin.service.*;
import com.housekeeping.common.utils.*;
import com.sun.org.apache.regexp.internal.RE;
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
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service("managerDetailsService")
public class ManagerDetailsServiceImpl extends ServiceImpl<ManagerDetailsMapper, ManagerDetails> implements ManagerDetailsService {

    @Resource
    private ICompanyDetailsService companyDetailsService;
    @Resource
    private IManagerMenuService managerMenuService;
    @Resource
    private ISysMenuService sysMenuService;
    @Resource
    private IUserService userService;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private OSSClient ossClient;
    @Value("${oss.bucketName}")
    private String bucketName;
    @Value("${oss.urlPrefix}")
    private String urlPrefix;
    @Resource
    private IGroupManagerService groupManagerService;
    @Resource
    private IGroupEmployeesService groupEmployeesService;
    @Resource
    private ManagerDetailsService managerDetailsService;

    @Override
    public R saveEmp(ManagerDetailsDTO managerDetailsDTO) {

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
                Integer maxManagerId = 0;
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
                managerDetails.setEducationBackground(managerDetailsDTO.getEducationBackground());
                managerDetails.setUpdateTime(LocalDateTime.now());
                managerDetails.setCreateTime(LocalDateTime.now());
                managerDetails.setCompanyId(one.getId());
                managerDetails.setLastReviserId(TokenUtils.getCurrentUserId());
                synchronized (this){
                    this.save(managerDetails);
                    maxManagerId = ((ManagerDetails) CommonUtils.getMaxId("manager_details", this)).getId();
                }

                /** 2021-01-16 su新增 增加经理的同时，给予所有菜单权限 */
                List<SysMenu> sysMenuList = sysMenuService.list();
                Integer finalMaxManagerId = maxManagerId;
                List<ManagerMenu> managerMenuList = sysMenuList.stream().map(x -> {
                    ManagerMenu managerMenu = new ManagerMenu();
                    managerMenu.setManagerId(finalMaxManagerId);
                    managerMenu.setMenuId(x.getId());
                    return managerMenu;
                }).collect(Collectors.toList());
                managerMenuService.saveBatch(managerMenuList);
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
        managerDetails.setEducationBackground(managerDetailsDTO.getEducationBackground());
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
        /* 鉴权 */
        String roleType = TokenUtils.getRoleType();
        if (roleType.equals(CommonConstants.REQUEST_ORIGIN_COMPANY)){
            Integer userId = TokenUtils.getCurrentUserId();
            QueryWrapper qw = new QueryWrapper();
            qw.eq("user_id", userId);
            CompanyDetails companyDetails = companyDetailsService.getOne(qw);
            ManagerDetails managerDetails = managerDetailsService.getById(id);

            if (companyDetails.getId().equals(managerDetails.getCompanyId())){
                //鑒權成功
            }else {
                return R.failed(null, "該經理不存在");
            }
        }else {
            return R.failed(null, "鑒權失敗");
        }

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

    @Override
    public Boolean thereIsACleaner(Integer employeesId) {
        AtomicReference<Boolean> res = new AtomicReference<>(false);
        Integer userId = TokenUtils.getCurrentUserId();
        QueryWrapper qw = new QueryWrapper();
        qw.eq("user_id", userId);
        ManagerDetails managerDetails = this.getOne(qw);
        Integer managerId = managerDetails.getId();
        QueryWrapper qw2 = new QueryWrapper();
        qw2.eq("manager_id", managerId);
        List<GroupManager> groupManagerList = groupManagerService.list(qw2);
        groupManagerList.forEach(groupManager -> {
            QueryWrapper qw3 = new QueryWrapper();
            qw3.eq("group_id", groupManager.getGroupId());
            List<GroupEmployees> groupEmployeesList = groupEmployeesService.list(qw3);
            groupEmployeesList.forEach(groupEmployees -> {
                if (groupEmployees.getEmployeesId().equals(employeesId)){
                    res.set(true);
                }
            });
        });
        return res.get();
    }

    @Override
    public R cusRemove(Integer managerId) {
        ManagerDetails managerDetails = managerDetailsService.getById(managerId);
        Integer userId = OptionalBean.ofNullable(managerDetails)
                .getBean(ManagerDetails::getUserId).get();
        if (CommonUtils.isEmpty(userId)){
            return R.failed("该經理不存在！");
        }
        userService.removeById(userId); //删除依赖1
        QueryWrapper qw = new QueryWrapper<>();
        qw.eq("manager_id", managerId);
        groupManagerService.remove(qw); //删除依赖2
        managerMenuService.remove(qw); //删除依赖3
        this.removeById(managerId);
        return R.ok("删除成功");
    }

    @Override
    public R getAllByCompanyUserId(Integer companyUserId) {
        QueryWrapper qw = new QueryWrapper();
        qw.eq("user_id", companyUserId);
        CompanyDetails companyDetails = companyDetailsService.getOne(qw);
        Integer companyId = companyDetails.getId();
        QueryWrapper qw2 = new QueryWrapper();
        qw2.eq("company_id", companyId);
        List<ManagerDetails> managerDetailsList = managerDetailsService.list(qw2);
        return R.ok(managerDetailsList, "查询成功");
    }

    @Override
    public R getInfoById() {
        Integer currentUserId = TokenUtils.getCurrentUserId();
        QueryWrapper qw = new QueryWrapper();
        qw.eq("user_id", currentUserId);
        ManagerDetails one = this.getOne(qw);
        return R.ok(one);
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
        String[] split = scaleById.split(" ");
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
