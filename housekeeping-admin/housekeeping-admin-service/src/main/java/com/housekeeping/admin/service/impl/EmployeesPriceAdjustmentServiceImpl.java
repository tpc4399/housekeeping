package com.housekeeping.admin.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.EmployeesPriceAdjustmentDTO;
import com.housekeeping.admin.entity.CompanyWorkList;
import com.housekeeping.admin.entity.EmployeesPriceAdjustment;
import com.housekeeping.admin.entity.Skill;
import com.housekeeping.admin.mapper.CompanyWorkListMapper;
import com.housekeeping.admin.mapper.EmployeesPriceAdjustmentMapper;
import com.housekeeping.admin.service.ICompanyWorkListService;
import com.housekeeping.admin.service.IEmployeesPriceAdjustmentService;
import com.housekeeping.admin.service.ISysJobContendService;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service("employeesPriceAdjustmentService")
public class EmployeesPriceAdjustmentServiceImpl extends ServiceImpl<EmployeesPriceAdjustmentMapper, EmployeesPriceAdjustment> implements IEmployeesPriceAdjustmentService {

    @Resource
    private ISysJobContendService sysJobContentService;

    @Override
    public R add(EmployeesPriceAdjustment employ) {

        if(StringUtils.isBlank(employ.getJobIds())){
            return R.failed("請選擇工作內容！");
        }

        //判重
        Integer employeesId = employ.getEmployeesId();
        String jobIds = employ.getJobIds();
        String date = employ.getDate();
        List<String> jobs = Arrays.asList(jobIds.split(","));
        List<String> times = Arrays.asList(date.split(","));
        ArrayList<EmployeesPriceAdjustment> employeesPriceAdjustments = new ArrayList<>();
        times.forEach(x ->{
            jobs.forEach(y ->{
                QueryWrapper<EmployeesPriceAdjustment> qw = new QueryWrapper<>();
                qw.eq("employees_id",employeesId);
                qw.like("date",x);
                qw.like("job_ids",y);
                List<EmployeesPriceAdjustment> list = this.list(qw);
                if(CollectionUtils.isNotEmpty(list)){
                    employeesPriceAdjustments.addAll(list);
                }
            });
        });
        if(CollectionUtils.isNotEmpty(employeesPriceAdjustments)){
            return R.failed("存在重複的工作內容價格設定!");
        }

        this.save(employ);
        return R.ok("新增成功");

    }

    @Override
    @Transactional
    public R cusUpdate(EmployeesPriceAdjustment employ) {
        //判重
        Integer employeesId = employ.getEmployeesId();
        String jobIds = employ.getJobIds();
        String date = employ.getDate();
        List<String> jobs = Arrays.asList(jobIds.split(","));
        List<String> times = Arrays.asList(date.split(","));
        ArrayList<EmployeesPriceAdjustment> employeesPriceAdjustments = new ArrayList<>();
        times.forEach(x ->{
            jobs.forEach(y ->{
                QueryWrapper<EmployeesPriceAdjustment> qw = new QueryWrapper<>();
                qw.eq("employees_id",employeesId);
                qw.like("date",x);
                qw.like("job_ids",y);
                List<EmployeesPriceAdjustment> list = this.list(qw);
                if(CollectionUtils.isNotEmpty(list)){
                    list.forEach(z ->{
                        if(!z.getId().equals(employ.getId())){
                            employeesPriceAdjustments.add(z);
                        }
                    });
                }
            });
        });
        if(CollectionUtils.isNotEmpty(employeesPriceAdjustments)){
            return R.failed("存在重複的工作內容價格設定!");
        }
        this.updateById(employ);
        return R.ok("修改成功");

    }

    @Override
    public R getAll(Integer id, Integer empId) {
        QueryWrapper<EmployeesPriceAdjustment> qw = new QueryWrapper<>();
        if(id!=null){
            qw.eq("id",id);
        }
        if(empId!=null){
            qw.eq("employees_id",empId);
        }
        List<EmployeesPriceAdjustment> list = this.list(qw);
        List<EmployeesPriceAdjustmentDTO> collect = list.stream().map(x -> {
            EmployeesPriceAdjustmentDTO employ = new EmployeesPriceAdjustmentDTO(x);

            String jobIds = employ.getJobIds();
            List<Skill> skills = new ArrayList<>();
            List<String> strings = Arrays.asList(jobIds.split(","));
            for (int i = 0; i < strings.size(); i++) {
                Skill skill = new Skill();
                skill.setJobId(Integer.parseInt(strings.get(i)));
                skill.setContend(sysJobContentService.getById(Integer.parseInt(strings.get(i))).getContend());
                skills.add(skill);
            }
            employ.setSkillTags(skills);
            return employ;
        }).collect(Collectors.toList());
        return R.ok(collect);
    }
}
