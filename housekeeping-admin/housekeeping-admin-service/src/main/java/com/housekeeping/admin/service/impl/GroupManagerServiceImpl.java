package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.GroupAdminDTO;
import com.housekeeping.admin.dto.GroupDTO;
import com.housekeeping.admin.dto.GroupManagerDTO;
import com.housekeeping.admin.entity.CompanyDetails;
import com.housekeeping.admin.entity.GroupManager;
import com.housekeeping.admin.entity.ManagerDetails;
import com.housekeeping.admin.mapper.GroupManagerMapper;
import com.housekeeping.admin.service.IGroupManagerService;
import com.housekeeping.admin.service.ManagerDetailsService;
import com.housekeeping.admin.vo.EmpVo;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author su
 * @create 2020/11/25 14:52
 */
@Service("groupManagerService")
public class GroupManagerServiceImpl extends ServiceImpl<GroupManagerMapper, GroupManager> implements IGroupManagerService {

    @Resource
    private CompanyDetailsServiceImpl companyDetailsService;
    @Resource
    private ManagerDetailsServiceImpl managerDetailsService;

    @Override
    public R save(GroupManagerDTO groupManagerDTO) {

        //先删
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("group_id", groupManagerDTO.getGroupId());
        baseMapper.delete(queryWrapper);

        //再加
        groupManagerDTO.getManagerId().forEach(x -> {
            GroupManager groupManager = new GroupManager();
            groupManager.setManagerId(x);
            groupManager.setGroupId(groupManagerDTO.getGroupId());
            baseMapper.insert(groupManager);
        });
        return R.ok("經理保存成功");

    }

    public Integer count(Integer groupId){
        return baseMapper.count(groupId);
    }

    public List<Integer> getManIdsByGroupId(Integer groupId){
        return baseMapper.getManIdsByGroupId(groupId);
    }

    @Override
    public R getAllMan(GroupDTO groupDTO) {
        QueryWrapper<CompanyDetails>  queryWrapper = new QueryWrapper();
        Integer userId = TokenUtils.getCurrentUserId();
        Integer companyId = companyDetailsService.getCompanyIdByUserId(userId);
        queryWrapper.eq("id", companyId);
        CompanyDetails one = companyDetailsService.getOne(queryWrapper);

        List<Integer> ids = managerDetailsService.getManIdsByCompId(one.getId());
        List<Integer> idsByGroupId = this.getManIdsByGroupId(groupDTO.getGroupId());
        ArrayList<EmpVo> empVos = new ArrayList<>();
        ids.removeAll(idsByGroupId);
        for (int i = 0; i < idsByGroupId.size(); i++) {
            ManagerDetails byId = managerDetailsService.getById(idsByGroupId.get(i));
            EmpVo empVo = new EmpVo();
            empVo.setId(byId.getId());
            empVo.setHeadUrl(byId.getHeadUrl());
            empVo.setName(byId.getName());
            empVo.setStatus(1);
            empVos.add(empVo);
        }
        for (int i = 0; i < ids.size(); i++) {
            ManagerDetails byId = managerDetailsService.getById(ids.get(i));
            EmpVo empVo = new EmpVo();
            empVo.setId(byId.getId());
            empVo.setHeadUrl(byId.getHeadUrl());
            empVo.setName(byId.getName());
            empVo.setStatus(0);
            empVos.add(empVo);
        }if(CommonUtils.isNotEmpty(groupDTO.getId())){
            List<EmpVo> empVos1 = search2(groupDTO.getId(), empVos);
            return R.ok(empVos1);
        }if(StringUtils.isNotBlank(groupDTO.getName())){
            List<EmpVo> search = this.search(groupDTO.getName(), empVos);
            return R.ok(search);
        }else {
            return R.ok(empVos);
        }
    }

    @Override
    public R getAllManById(Integer groupId) {
        ArrayList<ManagerDetails> ms = new ArrayList<>();
        List<Integer> manIdsByGroupId = this.getManIdsByGroupId(groupId);
        for (int i = 0; i < manIdsByGroupId.size(); i++) {
            ManagerDetails byId = managerDetailsService.getById(manIdsByGroupId.get(i));
            ms.add(byId);
        }
        if(CollectionUtils.isEmpty(ms)){
            return R.ok(null);
        }else {
            return R.ok(ms);
        }
    }

    @Override
    public R getAllManByAdmin(GroupAdminDTO groupDTO) {

        CompanyDetails one = companyDetailsService.getById(groupDTO.getCompanyId());

        List<Integer> ids = managerDetailsService.getManIdsByCompId(one.getId());
        List<Integer> idsByGroupId = this.getManIdsByGroupId(groupDTO.getGroupId());
        ArrayList<EmpVo> empVos = new ArrayList<>();
        ids.removeAll(idsByGroupId);
        for (int i = 0; i < idsByGroupId.size(); i++) {
            ManagerDetails byId = managerDetailsService.getById(idsByGroupId.get(i));
            EmpVo empVo = new EmpVo();
            empVo.setId(byId.getId());
            empVo.setHeadUrl(byId.getHeadUrl());
            empVo.setName(byId.getName());
            empVo.setStatus(1);
            empVos.add(empVo);
        }
        for (int i = 0; i < ids.size(); i++) {
            ManagerDetails byId = managerDetailsService.getById(ids.get(i));
            EmpVo empVo = new EmpVo();
            empVo.setId(byId.getId());
            empVo.setHeadUrl(byId.getHeadUrl());
            empVo.setName(byId.getName());
            empVo.setStatus(0);
            empVos.add(empVo);
        }if(CommonUtils.isNotEmpty(groupDTO.getId())){
            List<EmpVo> empVos1 = search2(groupDTO.getId(), empVos);
            return R.ok(empVos1);
        }if(StringUtils.isNotBlank(groupDTO.getName())){
            List<EmpVo> search = this.search(groupDTO.getName(), empVos);
            return R.ok(search);
        }else {
            return R.ok(empVos);
        }
    }

    public List<EmpVo> search(String name,List<EmpVo> list){
        List<EmpVo> results = new ArrayList();
        Pattern pattern = Pattern.compile(name);
        for(int i=0; i < list.size(); i++){
            Matcher matcher = pattern.matcher(((EmpVo)list.get(i)).getName());
            if(matcher.find()){
                results.add(list.get(i));
            }
        }
        return results;
    }

    public List<EmpVo> search2(Integer id,List<EmpVo> list){
        List<EmpVo> results = new ArrayList();
        Pattern pattern = Pattern.compile(id.toString());
        for(int i=0; i < list.size(); i++){
            Matcher matcher = pattern.matcher(((EmpVo)list.get(i)).getId().toString());
            if(matcher.matches()){
                results.add(list.get(i));
            }
        }
        return results;
    }
}
