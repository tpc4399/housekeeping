package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.EmployeesWorkExperienceDTO;
import com.housekeeping.admin.entity.EmployeesWorkExperience;
import com.housekeeping.admin.mapper.EmployeesWorkExperienceMapper;
import com.housekeeping.admin.service.EmployeesDetailsService;
import com.housekeeping.admin.service.IEmployeesWorkExperienceService;
import com.housekeeping.common.utils.R;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * @Author su
 * @Date 2020/12/2 9:56
 */
@Service("employeesWorkExperienceService")
public class EmployeesWorkExperienceServiceImpl extends ServiceImpl<EmployeesWorkExperienceMapper, EmployeesWorkExperience> implements IEmployeesWorkExperienceService {

    @Resource
    private EmployeesDetailsService employeesDetailsService;

    @Override
    public void saveEmployeesWorkExperience(List<EmployeesWorkExperienceDTO> employeesWorkExperienceDTOS, Integer employeesId) {
        //删掉原先的
        this.deleteByEmployeesId(employeesId);
        //存放新的
        employeesWorkExperienceDTOS.forEach(x -> {
            EmployeesWorkExperience employeesWorkExperience = new EmployeesWorkExperience();
            employeesWorkExperience.setEmployeesId(employeesId);
            employeesWorkExperience.setDateStart(x.getDateStart());
            employeesWorkExperience.setDateEnd(x.getDateEnd());
            employeesWorkExperience.setJobs(x.getJobs());
            employeesWorkExperience.setContends(x.getContends());
            baseMapper.insert(employeesWorkExperience);
        });
    }

    @Override
    public void updateEmployeesWorkExperience(List<EmployeesWorkExperienceDTO> employeesWorkExperienceDTOS, Integer employeesId) {
        //删掉原先的
        this.deleteByEmployeesId(employeesId);
        //存放新的
        employeesWorkExperienceDTOS.forEach(x -> {
            EmployeesWorkExperience employeesWorkExperience = new EmployeesWorkExperience();
            employeesWorkExperience.setId(x.getId());
            employeesWorkExperience.setEmployeesId(employeesId);
            employeesWorkExperience.setDateStart(x.getDateStart());
            employeesWorkExperience.setDateEnd(x.getDateEnd());
            employeesWorkExperience.setJobs(x.getJobs());
            employeesWorkExperience.setContends(x.getContends());
            baseMapper.updateById(employeesWorkExperience);
        });
    }

    @Override
    public void deleteByEmployeesId(Integer employeesId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("employees_id", employeesId);
        baseMapper.delete(queryWrapper);
    }

    @Override
    public R getAll(Integer employeesId) {
        QueryWrapper qw = new QueryWrapper();
        qw.eq("employees_id", employeesId);
        return R.ok(baseMapper.selectList(qw), "查詢成功");
    }
}
