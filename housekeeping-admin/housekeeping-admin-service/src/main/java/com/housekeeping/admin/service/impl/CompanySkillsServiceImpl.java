package com.housekeeping.admin.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.CompanySkillsDTO;
import com.housekeeping.admin.dto.SkillAndPriceDTO;
import com.housekeeping.admin.dto.SkillDTO;
import com.housekeeping.admin.dto.SkillPriceDTO;
import com.housekeeping.admin.entity.*;
import com.housekeeping.admin.mapper.CompanySkillsMapper;
import com.housekeeping.admin.mapper.InvitationMapper;
import com.housekeeping.admin.service.*;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service("companySkillsService")
public class CompanySkillsServiceImpl extends ServiceImpl<CompanySkillsMapper, CompanySkills> implements CompanySkillsService {

    @Resource
    private EmployeesDetailsService employeesDetailsService;
    @Resource
    private ISysJobContendService sysJobContendService;
    @Resource
    private ISysIndexContentService sysIndexContentService;
    @Resource
    private ISysIndexService sysIndexService;

    @Override
    public R saveCompanySkills(CompanySkillsDTO dto) {

        if(CollectionUtils.isEmpty(dto.getSkills())){
            return R.failed("請選擇工作內容");
        }

        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        dto.getSkills().forEach(job->{
            sb.append(job.getJobId());
            sb.append(" ");
            sb2.append(job.getPrice());
            sb2.append(" ");
        });
        String jobIdsString = new String(sb).trim();
        String jobPrice = new String(sb2).trim();

        CompanySkills companySkills = new CompanySkills();
        companySkills.setCompanyId(dto.getCompanyId());
        companySkills.setPresetJobIds(jobIdsString);
        companySkills.setJobPrice(jobPrice);
        this.save(companySkills);
        return R.ok("新增成功");
    }

    @Override
    public R cusUpdate(CompanySkillsDTO dto) {
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        dto.getSkills().forEach(job->{
            sb.append(job.getJobId());
            sb.append(" ");
            sb2.append(job.getPrice());
            sb2.append(" ");
        });
        String jobIdsString = new String(sb).trim();
        String jobPrice = new String(sb2).trim();

        CompanySkills byId = this.getById(dto.getId());
        byId.setPresetJobIds(jobIdsString);
        byId.setJobPrice(jobPrice);
        this.updateById(byId);
        return R.ok("修改成功");
    }

    @Override
    public R getCompanySkills(Integer id, Integer companyId) {
        QueryWrapper<CompanySkills> qw = new QueryWrapper<>();
        if(CommonUtils.isNotEmpty(id)){
            qw.eq("id",id);
        }
        if(CommonUtils.isNotEmpty(companyId)){
            qw.eq("company_id",companyId);
        }
        List<CompanySkills> list = this.list(qw);
        List<CompanySkillsDTO> collect = list.stream().map(x -> {
            CompanySkillsDTO companySkillsDTO = new CompanySkillsDTO();
            companySkillsDTO.setId(x.getId());
            companySkillsDTO.setCompanyId(x.getCompanyId());

            ArrayList<SkillDTO> skillPriceDTOS = new ArrayList();
            if (x.getPresetJobIds() == null) {
                companySkillsDTO.setSkills(null);
            } else {
                List<String> jobs = Arrays.asList(x.getPresetJobIds().split(" "));
                List<String> price = Arrays.asList(x.getJobPrice().split(" "));
                for (int i = 0; i < jobs.size(); i++) {
                    SkillDTO skillPrice = new SkillDTO();
                    skillPrice.setJobId(Integer.parseInt(jobs.get(i)));
                    skillPrice.setContend(sysJobContendService.getById(Integer.parseInt(jobs.get(i))).getContend());
                    skillPrice.setPrice(price.get(i));

                    //获取父类id与名称
                    QueryWrapper<SysIndexContent> qw2 = new QueryWrapper<>();
                    qw2.eq("content_id",Integer.parseInt(jobs.get(i)));
                    SysIndexContent one = sysIndexContentService.getOne(qw2);

                    QueryWrapper<SysIndex> qw3 = new QueryWrapper<>();
                    qw3.eq("id",one.getIndexId());
                    SysIndex sysIndex = sysIndexService.getOne(qw3);
                    skillPrice.setParentId(sysIndex.getId());
                    skillPrice.setParentName(sysIndex.getName());

                    skillPriceDTOS.add(skillPrice);
                }
                companySkillsDTO.setSkills(skillPriceDTOS);
            }
            return companySkillsDTO;
        }).collect(Collectors.toList());
        return R.ok(collect);
    }

    @Override
    public R copyByEmp(Integer id, Integer empId) {
        CompanySkills byId = this.getById(id);
        EmployeesDetails employeesDetails = employeesDetailsService.getById(empId);
        employeesDetails.setPresetJobIds(byId.getPresetJobIds());
        employeesDetails.setJobPrice(byId.getJobPrice());
        return R.ok(employeesDetailsService.updateById(employeesDetails));
    }
}
