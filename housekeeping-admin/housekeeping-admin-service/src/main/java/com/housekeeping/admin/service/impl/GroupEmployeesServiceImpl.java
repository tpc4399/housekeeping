package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.GroupAdminDTO;
import com.housekeeping.admin.dto.GroupDTO;
import com.housekeeping.admin.dto.GroupEmployeesAdminDTO;
import com.housekeeping.admin.dto.GroupEmployeesDTO;
import com.housekeeping.admin.entity.*;
import com.housekeeping.admin.mapper.EmployeesDetailsMapper;
import com.housekeeping.admin.mapper.GroupEmployeesMapper;
import com.housekeeping.admin.service.IGroupEmployeesService;
import com.housekeeping.admin.service.IGroupManagerService;
import com.housekeeping.admin.service.ISysJobContendService;
import com.housekeeping.admin.service.ManagerDetailsService;
import com.housekeeping.admin.vo.EmpVo;
import com.housekeeping.admin.vo.EmployeesDetailsSkillVo;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author su
 * @create 2020/11/19 14:56
 */
@Service("groupEmployeesService")
public class GroupEmployeesServiceImpl extends ServiceImpl<GroupEmployeesMapper, GroupEmployees> implements IGroupEmployeesService {

    @Resource
    private CompanyDetailsServiceImpl companyDetailsService;
    @Resource
    private EmployeesDetailsServiceImpl employeesDetailsService;
    @Resource
    private ManagerDetailsService managerDetailsService;
    @Resource
    private IGroupManagerService groupManagerService;
    @Resource
    private ISysJobContendService sysJobContendService;
    @Resource
    private EmployeesDetailsMapper employeesDetailsMapper;

    @Override
    public R save(GroupEmployeesDTO groupEmployeesDTO) {

        //刪
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("group_id", groupEmployeesDTO.getGroupId());
        baseMapper.delete(queryWrapper);

        //增
        groupEmployeesDTO.getEmployeesId().forEach(x -> {
            GroupEmployees groupEmployees = new GroupEmployees();
            groupEmployees.setEmployeesId(x);
            groupEmployees.setGroupId(groupEmployeesDTO.getGroupId());
            baseMapper.insert(groupEmployees);
        });
        return R.ok("員工修改成功");

    }

    @Override
    public R matchTheEmployees(Integer managerId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        // select employees_id from group_employees
        // where group_id in (select group_id from group_manager where manager_id = #{managerId})
        queryWrapper.inSql("group_id", "select group_id from group_manager where manager_id = " + managerId);
        return R.ok(baseMapper.selectList(queryWrapper), "经理"+ managerId +"管理的员工,获取成功");
    }

    public Integer count(Integer groupId){
        return baseMapper.count(groupId);
    }

    @Override
    public R getAllEmp(GroupDTO groupDTO) {
        QueryWrapper<CompanyDetails>  queryWrapper = new QueryWrapper();
        Integer userId = TokenUtils.getCurrentUserId();
        Integer companyId = companyDetailsService.getCompanyIdByUserId(userId);
        queryWrapper.eq("id", companyId);
        CompanyDetails one = companyDetailsService.getOne(queryWrapper);
        List<Integer> ids = employeesDetailsService.getAllIdsByCompanyId(one.getId());
        List<Integer> idsByGroupId = this.getIdsByGroupId(groupDTO.getGroupId());
        ArrayList<EmpVo> empVos = new ArrayList<>();
        ids.removeAll(idsByGroupId);
        for (int i = 0; i < idsByGroupId.size(); i++) {
            EmployeesDetails byId = employeesDetailsService.getById(idsByGroupId.get(i));
            EmpVo empVo = new EmpVo();
            empVo.setId(byId.getId());
            empVo.setHeadUrl(byId.getHeadUrl());
            empVo.setName(byId.getName());
            empVo.setStatus(1);
            empVos.add(empVo);
        }
        for (int i = 0; i < ids.size(); i++) {
            EmployeesDetails byId = employeesDetailsService.getById(ids.get(i));
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
    public List<EmployeesDetailsSkillVo> getAllEmpById(Integer groupId) {
        List<Integer> idsByGroupId = this.getIdsByGroupId(groupId);

        List<EmployeesDetailsSkillVo> es = new ArrayList<>();
        for (int i = 0; i < idsByGroupId.size(); i++) {
            EmployeesDetailsSkillVo byId = employeesDetailsMapper.getCusById(idsByGroupId.get(i));
            if(CommonUtils.isNotEmpty(byId)){
                if(CommonUtils.isEmpty(byId.getPresetJobIds())||byId.getPresetJobIds().equals("")){
                    byId.setSkillTags(null);
                }else {
                    String presetJobIds = byId.getPresetJobIds();
                    List<Skill> skills = new ArrayList<>();
                    List<String> strings = Arrays.asList(presetJobIds.split(" "));
                    for (int x = 0; x < strings.size(); x++) {
                        Skill skill = new Skill();
                        skill.setJobId(Integer.parseInt(strings.get(x)));
                        skill.setContend(sysJobContendService.getById(Integer.parseInt(strings.get(x))).getContend());
                        skills.add(skill);
                    }
                    byId.setSkillTags(skills);
            }
                es.add(byId);
        }
        }
        if(CollectionUtils.isEmpty(es)){
            return null;
        }else {
            return es;
        }
    }

    @Override
    public R getAllEmpByAdmin(GroupAdminDTO groupDTO) {
        CompanyDetails one = companyDetailsService.getById(groupDTO.getCompanyId());
        List<Integer> ids = employeesDetailsService.getAllIdsByCompanyId(one.getId());
        List<Integer> idsByGroupId = this.getIdsByGroupId(groupDTO.getGroupId());
        ArrayList<EmpVo> empVos = new ArrayList<>();
        ids.removeAll(idsByGroupId);
        for (int i = 0; i < idsByGroupId.size(); i++) {
            EmployeesDetails byId = employeesDetailsService.getById(idsByGroupId.get(i));
            EmpVo empVo = new EmpVo();
            empVo.setId(byId.getId());
            empVo.setHeadUrl(byId.getHeadUrl());
            empVo.setName(byId.getName());
            empVo.setStatus(1);
            empVos.add(empVo);
        }
        for (int i = 0; i < ids.size(); i++) {
            EmployeesDetails byId = employeesDetailsService.getById(ids.get(i));
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
    public R saveByAdmin(GroupEmployeesAdminDTO groupEmployeesDTO) {
        //刪
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("group_id", groupEmployeesDTO.getGroupId());
        baseMapper.delete(queryWrapper);

        String[] split = groupEmployeesDTO.getEmployeesId().split(",");
        List<String> strings = Arrays.asList(split);
        //增
        strings.forEach(x -> {
            GroupEmployees groupEmployees = new GroupEmployees();
            groupEmployees.setEmployeesId(Integer.parseInt(x));
            groupEmployees.setGroupId(groupEmployeesDTO.getGroupId());
            baseMapper.insert(groupEmployees);
        });
        return R.ok("員工修改成功");
    }

    @Override
    public List<Integer> getEmployeesIdsByManager() {
        ManagerDetails md = managerDetailsService.getManagerDetailsByUserId(TokenUtils.getCurrentUserId());
        List<Integer> employeesIds = baseMapper.getEmployeesIdsByManager(md.getId());
        return employeesIds.isEmpty()?new ArrayList<>():employeesIds;
    }

    public List<Integer> getIdsByGroupId(Integer groupId){
        return baseMapper.getIdsByGroupId(groupId);
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


    @Override
    public List<Integer> getEmployeesIdsByManagerId(Integer manId) {
        Integer userId = managerDetailsService.getById(manId).getUserId();
        ManagerDetails md = managerDetailsService.getManagerDetailsByUserId(userId);
        List<Integer> employeesIds = baseMapper.getEmployeesIdsByManager(md.getId());
        return employeesIds.isEmpty()?new ArrayList<>():employeesIds;
    }
}
