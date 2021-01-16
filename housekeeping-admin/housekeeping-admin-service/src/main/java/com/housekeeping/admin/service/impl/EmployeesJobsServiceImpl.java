package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.EmployeesJobsDTO;
import com.housekeeping.admin.entity.EmployeesJobs;
import com.housekeeping.admin.mapper.EmployeesJobsMapper;
import com.housekeeping.admin.service.IEmployeesJobsService;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import org.springframework.stereotype.Service;

/**
 * @Author su
 * @Date 2021/1/11 10:23
 */
@Service("employeesJobsService")
public class EmployeesJobsServiceImpl
        extends ServiceImpl<EmployeesJobsMapper, EmployeesJobs>
        implements IEmployeesJobsService {

    @Override
    public R updateEmployeesJobs(EmployeesJobsDTO employeesJobsDTO) {
        if (CommonUtils.isEmpty(employeesJobsDTO.getEmployeesId())){
            return R.failed("員工id不能為空");
        }
        if (employeesJobsDTO.getJobIds() == null){
            return R.failed("工作內容不能為null");
        }

        /** 先刪後存 */
        QueryWrapper wr = new QueryWrapper();
        wr.eq("employees_id", employeesJobsDTO.getEmployeesId());
        baseMapper.delete(wr);

        employeesJobsDTO.getJobIds().forEach(x->{
            EmployeesJobs employeesJobs = new EmployeesJobs();
            employeesJobs.setEmployeesId(employeesJobsDTO.getEmployeesId());
            employeesJobs.setJobId(x);
            baseMapper.insert(employeesJobs);
        });

        return R.ok("修改成功");
    }

}
