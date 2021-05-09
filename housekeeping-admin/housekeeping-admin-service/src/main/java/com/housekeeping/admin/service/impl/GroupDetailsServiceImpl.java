package com.housekeeping.admin.service.impl;


import com.aliyun.oss.OSSClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.GroupDetailsDTO;
import com.housekeeping.admin.dto.GroupDetailsUpdateDTO;
import com.housekeeping.admin.entity.*;
import com.housekeeping.admin.mapper.GroupDetailsMapper;
import com.housekeeping.admin.service.EmployeesDetailsService;
import com.housekeeping.admin.service.ICompanyDetailsService;
import com.housekeeping.admin.service.IGroupDetailsService;
import com.housekeeping.admin.service.ManagerDetailsService;
import com.housekeeping.admin.vo.GroupDetailsVo;
import com.housekeeping.admin.vo.GroupVO;
import com.housekeeping.common.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("groupService")
public class GroupDetailsServiceImpl extends ServiceImpl<GroupDetailsMapper, GroupDetails> implements IGroupDetailsService {

    @Resource
    private ICompanyDetailsService companyDetailsService;
    @Resource
    private GroupManagerServiceImpl groupManagerService;
    @Resource
    private GroupEmployeesServiceImpl groupEmployeesService;
    @Resource
    private ManagerDetailsService managerDetailsService;
    @Resource
    private EmployeesDetailsService employeesDetailsService;
    @Resource
    private OSSClient ossClient;

    @Value("${oss.bucketName}")
    private String bucketName;

    @Value("${oss.urlPrefix}")
    private String urlPrefix;

    @Override
    public R saveGroup(GroupDetailsDTO groupDetailsDTO) {
        Integer userId = TokenUtils.getCurrentUserId();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        CompanyDetails companyDetails = companyDetailsService.getOne(queryWrapper);
        Integer companyId = companyDetails.getId();
        /** 检查组名重复性 */
        List<GroupDetails> groupDetailsList = new ArrayList<>();
        QueryWrapper queryWrapper1 = new QueryWrapper();
        queryWrapper1.eq("company_id", companyId);
        groupDetailsList = baseMapper.selectList(queryWrapper1);

        for (int i = 0; i < groupDetailsList.size(); i++) {
            if (groupDetailsList.get(i).getGroupName().equals(groupDetailsDTO.getGroupName())){
                return R.failed("贵公司组名"+ groupDetailsDTO.getGroupName() +"已经被占有");
            }
        }

        GroupDetails groupDetails = new GroupDetails();
        groupDetails.setCompanyId(companyId);
        groupDetails.setGroupName(groupDetailsDTO.getGroupName());
        groupDetails.setCreateTime(LocalDateTime.now());
        groupDetails.setUpdateTime(LocalDateTime.now());
        groupDetails.setLastReviserId(userId);
        groupDetails.setHeadUrl("https://test-live-video.oss-cn-shanghai.aliyuncs.com/HKFile/ImPhoto/userId=/20210508111402.png");
        return R.ok(baseMapper.insert(groupDetails), "成功添加分組");
    }

    @Override
    public R updateGroup(GroupDetailsUpdateDTO groupDetailsUpdateDTO) {
        Integer userId = TokenUtils.getCurrentUserId();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        CompanyDetails companyDetails = companyDetailsService.getOne(queryWrapper);
        Integer companyId = companyDetails.getId();
        /** 检查组名重复性 */
        List<GroupDetails> groupDetailsList = new ArrayList<>();
        QueryWrapper queryWrapper1 = new QueryWrapper();
        queryWrapper1.eq("company_id", companyId);
        queryWrapper1.eq("group_name",groupDetailsUpdateDTO.getGroupName());

        GroupDetails one = this.getOne(queryWrapper1);
        if(CommonUtils.isNotEmpty(one)&&!groupDetailsUpdateDTO.getId().equals(one.getId())){
            return R.failed("贵公司组名"+ groupDetailsUpdateDTO.getGroupName() +"已经被占有");
        }

        GroupDetails groupDetails = new GroupDetails();
        groupDetails.setId(groupDetailsUpdateDTO.getId());
        groupDetails.setGroupName(groupDetailsUpdateDTO.getGroupName());
        groupDetails.setUpdateTime(LocalDateTime.now());
        groupDetails.setLastReviserId(userId);
        /*baseMapper.updateGroup(groupDetails);*/
        this.updateById(groupDetails);
        return R.ok("成功修改分組");
    }

    @Override
    public R cusRemove(Integer id) {
        if(id != null){
            this.removeById(id);
            Boolean a = baseMapper.removeEmp(id);
            Boolean b = baseMapper.removeMan(id);
        }else {
            return R.failed("id爲空");
        }
        return R.ok("刪除分組成功");
    }

    @Override
    public IPage getGroup(Page page, Integer companyId, Integer id) {
        return baseMapper.getGroup(page,companyId,id);
    }

    @Override
    public String uploadLogo(MultipartFile file, Integer groupId) throws IOException {

        String res = "";

        LocalDateTime now = LocalDateTime.now();
        String nowString = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String catalogue = CommonConstants.HK_GROUP_URL_ABSTRACT_PATH_PREFIX_PROV + TokenUtils.getCurrentUserId();
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
    public R updateLogUrlByGroupId(String fileName, Integer groupId) {
        GroupDetails byId = this.getById(groupId);
        byId.setHeadUrl(fileName);
        return R.ok(this.updateById(byId));
    }

    @Override
    public R getGroupData(Page page,Integer companyId, Integer id,String groupName) {
        QueryWrapper<GroupDetails> qw = new QueryWrapper<>();
        qw.eq("company_id",companyId);
        List<GroupDetails> list = this.list(qw);
        if(CollectionUtils.isEmpty(list)){
            return R.ok(null);
        }
        ArrayList<GroupVO> groupVOS = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                GroupVO groupVO = new GroupVO();
                groupVO.setGroupId(list.get(i).getId());
                groupVO.setGroupName(list.get(i).getGroupName());
                groupVO.setHeadUrl(list.get(i).getHeadUrl());
                groupVO.setManNum(groupManagerService.count(list.get(i).getId()));
                groupVO.setEmpNum(groupEmployeesService.count(list.get(i).getId()));
                List<Integer> manIds = groupManagerService.getManIdsByGroupId(list.get(i).getId());
                StringBuilder sb = new StringBuilder();
                if(CollectionUtils.isEmpty(manIds)){
                    groupVO.setResponsible("");
                }else {
                    for (int j = 0; j < manIds.size(); j++) {
                        ManagerDetails byId = managerDetailsService.getById(manIds.get(j));
                        String s = (CommonUtils.isNotEmpty(byId.getName())?byId.getName() : "");
                        sb.append(s).append(",");
                    }
                    String s = sb.toString();
                    groupVO.setResponsible(s.substring(0, s.length() - 1));
                }
                groupVOS.add(groupVO);
            }
            if(CommonUtils.isNotEmpty(id)){
                List<GroupVO> groupVOS1 = search2(id, groupVOS);
                return R.ok(groupVOS1);
            }
            if(StringUtils.isNotEmpty(groupName)){
            List<GroupVO> search = search(groupName, groupVOS);
                Page pages = PageUtils.getPages((int) page.getCurrent(), (int) page.getSize(), search);
                return R.ok(pages);
        }
        Page pages = PageUtils.getPages((int) page.getCurrent(), (int) page.getSize(), groupVOS);
        return R.ok(pages);
    }

    @Override
    public R saveGroupByAdmin(Integer companyId, String groupName) {
        /** 检查组名重复性 */
        List<GroupDetails> groupDetailsList = new ArrayList<>();
        QueryWrapper queryWrapper1 = new QueryWrapper();
        queryWrapper1.eq("company_id", companyId);
        groupDetailsList = baseMapper.selectList(queryWrapper1);

        for (int i = 0; i < groupDetailsList.size(); i++) {
            if (groupDetailsList.get(i).getGroupName().equals(groupName)){
                return R.failed("贵公司组名"+ groupName +"已经被占有");
            }
        }

        GroupDetails groupDetails = new GroupDetails();
        groupDetails.setCompanyId(companyId);
        groupDetails.setGroupName(groupName);
        groupDetails.setCreateTime(LocalDateTime.now());
        groupDetails.setUpdateTime(LocalDateTime.now());
        groupDetails.setLastReviserId(TokenUtils.getCurrentUserId());
        return R.ok(baseMapper.insert(groupDetails), "成功添加分組");
    }

    @Override
    public R updateGroupByAdmin(Integer id, String groupName, Integer companyId) {
        /** 检查组名重复性 */
        List<GroupDetails> groupDetailsList = new ArrayList<>();
        QueryWrapper queryWrapper1 = new QueryWrapper();
        queryWrapper1.eq("company_id", companyId);
        groupDetailsList = baseMapper.selectList(queryWrapper1);

        for (int i = 0; i < groupDetailsList.size(); i++) {
            if (groupDetailsList.get(i).getGroupName().equals(groupName)){
                return R.failed("贵公司组名"+ groupName +"已经被占有");
            }
        }

        GroupDetails groupDetails = new GroupDetails();
        groupDetails.setId(id);
        groupDetails.setGroupName(groupName);
        groupDetails.setUpdateTime(LocalDateTime.now());
        groupDetails.setLastReviserId(TokenUtils.getCurrentUserId());
        /*baseMapper.updateGroup(groupDetails);*/
        this.updateById(groupDetails);
        return R.ok("成功修改分組");
    }

    @Override
    public R getAllGroups(Page page, String groupName) {
        List<GroupDetailsVo> groupDetailsVos = baseMapper.getAllGroups(groupName);
        for (int i = 0; i < groupDetailsVos.size(); i++) {
            if(CommonUtils.isEmpty(groupDetailsVos.get(i).getCompanyName())){
                groupDetailsVos.get(i).setCompanyName("未认证公司");
            }
        }
        Page pages = PageUtils.getPages((int) page.getCurrent(), (int) page.getSize(), groupDetailsVos);
        return R.ok(pages);
    }

    @Override
    public R addGroup2(MultipartFile headPortrait, String name, Integer[] managerIds, Integer[] employeesIds) {
        Integer companyId = 0;
        Integer userId = TokenUtils.getCurrentUserId();
        String roleType = TokenUtils.getRoleType();
        if (roleType.equals(CommonConstants.REQUEST_ORIGIN_COMPANY)) {
            if (managerIds.length == 0) return R.failed(null, "公司端新增組必須增加至少一個管理者");
            companyId = companyDetailsService.getCompanyIdByUserId(userId);
        }
        if (roleType.equals(CommonConstants.REQUEST_ORIGIN_MANAGER)){
            companyId = managerDetailsService.getCompanyIdByManagerId(userId);
        }

        /* 判斷經理存在性 */
        for (int i = 0; i < managerIds.length; i++) {
            Integer managerId = managerIds[i];
            Boolean exist = managerDetailsService.judgeManagerInCompany(managerId, companyId);
            if (!exist) return R.failed(null,"新增經理只能是本公司的經理");
        }
        /* 員工存在性判斷 */
        for (int i = 0; i < employeesIds.length; i++) {
            Integer employeesId = employeesIds[i];
            Boolean exist = employeesDetailsService.judgeEmployeesInCompany(employeesId, companyId);
            if (!exist) return R.failed(null,"新增經理只能是本公司的經理");
        }

        /* 組名存在性判斷 */
        if (this.judgeGroupNameInCompany(name, companyId)) return R.failed(null, "該組名已經存在");

        /* 開始組頭像oss存儲 */
        String logoUrl;
        if(CommonUtils.isEmpty(headPortrait)){
            logoUrl = "https://test-live-video.oss-cn-shanghai.aliyuncs.com/HKFile/ImPhoto/userId=/20210508111402.png";
        }else {
            logoUrl = this.logoSave(headPortrait);
        }

        /* 開始數據庫存儲 */
        LocalDateTime now = LocalDateTime.now();
        GroupDetails gd = new GroupDetails(null, companyId, logoUrl, name, now, now, userId);
        Integer groupId = 0;
        synchronized (this){
            this.save(gd);
            groupId = ((GroupDetails) CommonUtils.getMaxId("group_details", this)).getId();
        }
        List<GroupManager> gms = new ArrayList<>();
        List<GroupEmployees> ges = new ArrayList<>();
        for (int i = 0; i < managerIds.length; i++) {
            GroupManager gm = new GroupManager(null, groupId, managerIds[i]);
            gms.add(gm);
        }
        groupManagerService.saveBatch(gms);
        for (int i = 0; i < employeesIds.length; i++) {
            GroupEmployees ge = new GroupEmployees(null, groupId, employeesIds[i]);
            ges.add(ge);
        }
        groupEmployeesService.saveBatch(ges);
        return R.ok(null, "成功添加組");
    }

    @Override
    public Boolean judgeGroupNameInCompany(String name, Integer companyId) {
        QueryWrapper qw = new QueryWrapper();
        qw.eq("group_name", name);
        qw.eq("company_id", companyId);
        List<GroupDetails> gds = this.baseMapper.selectList(qw);
        if (!gds.isEmpty()) return true;
        return false;
    }

    @Override
    public String logoSave(MultipartFile logo) {
        String res = "";

        LocalDateTime now = LocalDateTime.now();
        String nowString = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String catalogue = CommonConstants.HK_GROUP_URL_ABSTRACT_PATH_PREFIX_PROV; //     HKFile/GroupLogoImg
        String type = logo.getOriginalFilename().split("\\.")[1];
        String fileAbstractPath = catalogue + "/" + nowString+"."+ type;

        try {
            ossClient.putObject(bucketName, fileAbstractPath, new ByteArrayInputStream(logo.getBytes()));
            res = urlPrefix + fileAbstractPath;
        } catch (Exception e) {
            e.printStackTrace();
            return "error upload";
        }

        return res;
    }

    public List<GroupVO> search(String name, List<GroupVO> list){
        List<GroupVO> results = new ArrayList();
        Pattern pattern = Pattern.compile(name);
        for(int i=0; i < list.size(); i++){
            Matcher matcher = pattern.matcher(((GroupVO)list.get(i)).getGroupName());
            if(matcher.find()){
                results.add(list.get(i));
            }
        }
        return results;
    }

    public List<GroupVO> search2(Integer id,List<GroupVO> list){
        List<GroupVO> results = new ArrayList();
        Pattern pattern = Pattern.compile(id.toString());
        for(int i=0; i < list.size(); i++){
            Matcher matcher = pattern.matcher(((GroupVO)list.get(i)).getGroupId().toString());
            if(matcher.matches()){
                results.add(list.get(i));
            }
        }
        return results;
    }


}
