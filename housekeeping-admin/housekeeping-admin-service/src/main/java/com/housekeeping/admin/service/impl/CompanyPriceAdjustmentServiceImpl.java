package com.housekeeping.admin.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.CompanyPriceAdjustmentDTO;
import com.housekeeping.admin.dto.EmployeesPriceAdjustmentDTO;
import com.housekeeping.admin.entity.CompanyPriceAdjustment;
import com.housekeeping.admin.entity.EmployeesPriceAdjustment;
import com.housekeeping.admin.entity.Skill;
import com.housekeeping.admin.mapper.CompanyPriceAdjustmentMapper;
import com.housekeeping.admin.mapper.EmployeesPriceAdjustmentMapper;
import com.housekeeping.admin.service.CompanyPriceAdjustmentService;
import com.housekeeping.admin.service.IEmployeesPriceAdjustmentService;
import com.housekeeping.admin.service.ISysJobContendService;
import com.housekeeping.common.utils.R;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Service("companyPriceAdjustmentService")
public class CompanyPriceAdjustmentServiceImpl extends ServiceImpl<CompanyPriceAdjustmentMapper, CompanyPriceAdjustment> implements CompanyPriceAdjustmentService {

    @Resource
    private ISysJobContendService sysJobContentService;
    @Resource
    private IEmployeesPriceAdjustmentService employeesPriceAdjustmentService;

    @Override
    public R add(CompanyPriceAdjustment employ) {

        if(StringUtils.isBlank(employ.getJobIds())){
            return R.failed("請選擇工作內容！");
        }

        //判重
        Integer companyId = employ.getCompanyId();
        String jobIds = employ.getJobIds();
        String date = employ.getDate();
        List<String> jobs = Arrays.asList(jobIds.split(","));
        List<String> times = Arrays.asList(date.split(","));
        ArrayList<CompanyPriceAdjustment> companyPriceAdjustments = new ArrayList<>();
        times.forEach(x ->{
            jobs.forEach(y ->{
                QueryWrapper<CompanyPriceAdjustment> qw = new QueryWrapper<>();
                qw.eq("company_id",companyId);
                qw.like("date",x);
                qw.like("job_ids",y);
                List<CompanyPriceAdjustment> list = this.list(qw);
                if(CollectionUtils.isNotEmpty(list)){
                    companyPriceAdjustments.addAll(list);
                }
            });
        });
        if(CollectionUtils.isNotEmpty(companyPriceAdjustments)){
            return R.failed("存在重複的工作內容價格設定!");
        }
        this.save(employ);
        return R.ok("新增成功");

    }

    @Override
    @Transactional
    public R cusUpdate(CompanyPriceAdjustment employ) {
        //判重
        Integer companyId = employ.getCompanyId();
        String jobIds = employ.getJobIds();
        String date = employ.getDate();
        List<String> jobs = Arrays.asList(jobIds.split(","));
        List<String> times = Arrays.asList(date.split(","));
        ArrayList<CompanyPriceAdjustment> companyPriceAdjustments = new ArrayList<>();
        times.forEach(x ->{
            jobs.forEach(y ->{
                QueryWrapper<CompanyPriceAdjustment> qw = new QueryWrapper<>();
                qw.eq("company_id",companyId);
                qw.like("date",x);
                qw.like("job_ids",y);
                List<CompanyPriceAdjustment> list = this.list(qw);
                if(CollectionUtils.isNotEmpty(list)){
                    list.forEach(z ->{
                        if(!z.getId().equals(employ.getId())){
                            companyPriceAdjustments.add(z);
                        }
                    });
                }
            });
        });
        if(CollectionUtils.isNotEmpty(companyPriceAdjustments)){
            return R.failed("存在重複的工作內容價格設定!");
        }
        this.updateById(employ);
        return R.ok("修改成功");

    }

    @Override
    public R getAll(Integer id, Integer companyId) {
        QueryWrapper<CompanyPriceAdjustment> qw = new QueryWrapper<>();
        if(id!=null){
            qw.eq("id",id);
        }
        if(companyId!=null){
            qw.eq("company_id",companyId);
        }
        List<CompanyPriceAdjustment> list = this.list(qw);
        List<CompanyPriceAdjustmentDTO> collect = list.stream().map(x -> {
            CompanyPriceAdjustmentDTO employ = new CompanyPriceAdjustmentDTO(x);

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

    @Override
    public R copyByEmp(Integer id, Integer empId) {
        CompanyPriceAdjustment employ = this.getById(id);

        //判重
        String jobIds = employ.getJobIds();
        String date = employ.getDate();
        List<String> jobs = Arrays.asList(jobIds.split(","));
        List<String> times = Arrays.asList(date.split(","));
        ArrayList<EmployeesPriceAdjustment> employeesPriceAdjustments = new ArrayList<>();
        times.forEach(x ->{
            jobs.forEach(y ->{
                QueryWrapper<EmployeesPriceAdjustment> qw = new QueryWrapper<>();
                qw.eq("employees_id",empId);
                qw.like("date",x);
                qw.like("job_ids",y);
                List<EmployeesPriceAdjustment> list = employeesPriceAdjustmentService.list(qw);
                if(CollectionUtils.isNotEmpty(list)){
                    employeesPriceAdjustments.addAll(list);
                }
            });
        });
        if(CollectionUtils.isNotEmpty(employeesPriceAdjustments)){
            return R.failed("存在重複的工作內容價格設定!");
        }

        EmployeesPriceAdjustment employeesPriceAdjustment = new EmployeesPriceAdjustment();
        employeesPriceAdjustment.setEmployeesId(empId);
        employeesPriceAdjustment.setCode(employ.getCode());
        employeesPriceAdjustment.setStartDate(employ.getStartDate());
        employeesPriceAdjustment.setDate(employ.getDate());
        employeesPriceAdjustment.setEndDate(employ.getEndDate());
        employeesPriceAdjustment.setHourlyWage(employ.getHourlyWage());
        employeesPriceAdjustment.setJobIds(employ.getJobIds());
        employeesPriceAdjustment.setPercentage(employ.getPercentage());
        employeesPriceAdjustment.setType(employ.getType());
        employeesPriceAdjustment.setStatus(true);
        employeesPriceAdjustmentService.save(employeesPriceAdjustment);

        return R.ok("新增成功");
    }


}
