package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.EmployeesJobsDTO;
import com.housekeeping.admin.dto.JobsDTO;
import com.housekeeping.admin.entity.EmployeesJobs;
import com.housekeeping.admin.mapper.EmployeesJobsMapper;
import com.housekeeping.admin.service.IEmployeesJobsService;
import com.housekeeping.admin.service.ISysJobContendService;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author su
 * @Date 2021/1/11 10:23
 */
@Service("employeesJobsService")
public class EmployeesJobsServiceImpl
        extends ServiceImpl<EmployeesJobsMapper, EmployeesJobs>
        implements IEmployeesJobsService {

    @Resource
    private ISysJobContendService sysJobContendService;
    @Resource
    private IEmployeesJobsService employeesJobsService;

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
//            employeesJobs.setEmployeesId(employeesJobsDTO.getEmployeesId());
            employeesJobs.setJobId(x);
            baseMapper.insert(employeesJobs);
        });

        return R.ok("修改成功");
    }

    @Override
    public R updateEmployeesJobsAndPrices(List<JobsDTO> jobs, Integer employeesId) {
        jobs.forEach(x->{
            Boolean type = sysJobContendService.getType(x.getJobId());
            EmployeesJobs employeesJobs;
            if (type){
                //包工，需要存那些价格段
//                employeesJobs = new EmployeesJobs(employeesId, x);
            }else {
//                employeesJobs = new EmployeesJobs(employeesId, x.getJobId());
            }
//            employeesJobsService.save(employeesJobs);
        });
        return R.ok("更新價格段成功");
    }

    @Override
    public R getJobs(Integer employeesId) {
        return null;
    }

    @Override
    public R setJobIdsByEmployeesId(List<Integer> ids, Integer employeesId) {
        List<EmployeesJobs> employeesJobsList = ids.stream().map(x->{
            EmployeesJobs employeesJobs = new EmployeesJobs();
            employeesJobs.setJobId(x);
            employeesJobs.setEmployeesId(employeesId);
            return employeesJobs;
        }).collect(Collectors.toList());
        return R.ok(this.saveBatch(employeesJobsList), "可工作內容保存成功");
    }

}
