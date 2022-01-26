package com.housekeeping.admin.controller;

import com.housekeeping.admin.dto.CompanySkillsDTO;
import com.housekeeping.admin.service.CompanySkillsService;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;


@Api(tags={"【公司工作技能模板】管理接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/companySkills")
public class CompanySkillsController {

    private final CompanySkillsService companySkillsService;

    @PostMapping
    @ApiOperation("新增工作技能模板")
    public R saveCompanySkills(@RequestBody CompanySkillsDTO  companySkillsDTO){
        return companySkillsService.saveCompanySkills(companySkillsDTO);
    }

    @PostMapping("/update")
    @ApiOperation("修改工作技能模板")
    public R update(@RequestBody CompanySkillsDTO companySkillsDTO){
        return companySkillsService.cusUpdate(companySkillsDTO);
    }

    @DeleteMapping
    @ApiOperation("刪除工作技能模板")
    public R remove(Integer id){
        return R.ok(companySkillsService.removeById(id));
    }

    @GetMapping("/getCompanySkills")
    @ApiOperation("獲取工作技能模板")
    public R getCompanySkills(Integer id,Integer companyId){
        return companySkillsService.getCompanySkills(id, companyId);
    }

    @GetMapping("/copyByEmp")
    @ApiOperation("員工選擇工作技能模板")
    public R copyByEmp(Integer id,Integer empId){
        return companySkillsService.copyByEmp(id, empId);
    }


}
