package com.housekeeping.admin.service.impl;


import com.aliyun.oss.OSSClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.GroupDetailsDTO;
import com.housekeeping.admin.dto.GroupDetailsUpdateDTO;
import com.housekeeping.admin.entity.CompanyDetails;
import com.housekeeping.admin.entity.GroupDetails;
import com.housekeeping.admin.entity.ManagerDetails;
import com.housekeeping.admin.mapper.GroupDetailsMapper;
import com.housekeeping.admin.service.ICompanyDetailsService;
import com.housekeeping.admin.service.IGroupDetailsService;
import com.housekeeping.admin.service.ManagerDetailsService;
import com.housekeeping.admin.vo.EmpVo;
import com.housekeeping.admin.vo.GroupVO;
import com.housekeeping.common.utils.CommonConstants;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
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
    private GroupEmployeesServiceImpl groupEmployeesServiceImpl;
    @Resource
    private ManagerDetailsService managerDetailsService;
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
        groupDetailsList = baseMapper.selectList(queryWrapper1);

        for (int i = 0; i < groupDetailsList.size(); i++) {
            if (groupDetailsList.get(i).getGroupName().equals(groupDetailsUpdateDTO.getGroupName())){
                return R.failed("贵公司组名"+ groupDetailsUpdateDTO.getGroupName() +"已经被占有");
            }
        }

        GroupDetails groupDetails = new GroupDetails();
        groupDetails.setId(groupDetailsUpdateDTO.getId());
        groupDetails.setGroupName(groupDetailsUpdateDTO.getGroupName());
        groupDetails.setUpdateTime(LocalDateTime.now());
        groupDetails.setLastReviserId(userId);
        baseMapper.updateGroup(groupDetails);
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
    public R getGroupData(Integer companyId, Integer id,String groupName) {
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
                groupVO.setEmpNum(groupEmployeesServiceImpl.count(list.get(i).getId()));
                List<Integer> manIds = groupManagerService.getManIdsByGroupId(list.get(i).getId());
                StringBuilder sb = new StringBuilder();
                if(CollectionUtils.isEmpty(manIds)){
                    groupVO.setResponsible("");
                }else {
                    for (int j = 0; j < manIds.size(); j++) {
                        ManagerDetails byId = managerDetailsService.getById(manIds.get(j));
                        sb.append(byId.getName()).append(",");
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
            return R.ok(search);
        }
        return R.ok(groupVOS);
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
