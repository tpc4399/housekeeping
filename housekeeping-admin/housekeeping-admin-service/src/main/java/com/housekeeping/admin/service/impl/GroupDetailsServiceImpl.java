package com.housekeeping.admin.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.GroupDetailsDTO;
import com.housekeeping.admin.dto.GroupDetailsUpdateDTO;
import com.housekeeping.admin.dto.GroupEmployeesDTO;
import com.housekeeping.admin.dto.GroupManagerDTO;
import com.housekeeping.admin.entity.CompanyDetails;
import com.housekeeping.admin.entity.GroupDetails;
import com.housekeeping.admin.mapper.GroupDetailsMapper;
import com.housekeeping.admin.service.ICompanyDetailsService;
import com.housekeeping.admin.service.IGroupDetailsService;
import com.housekeeping.admin.service.IUserService;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service("groupService")
public class GroupDetailsServiceImpl extends ServiceImpl<GroupDetailsMapper, GroupDetails> implements IGroupDetailsService {

    @Resource
    private ICompanyDetailsService companyDetailsService;

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

}
