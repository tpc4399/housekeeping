package com.housekeeping.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.housekeeping.admin.dto.EmployeesDetailsDTO;
import com.housekeeping.admin.dto.PageOfEmployeesDTO;
import com.housekeeping.admin.dto.PageOfEmployeesDetailsDTO;
import com.housekeeping.admin.entity.EmployeesDetails;
import com.housekeeping.admin.entity.GroupEmployees;
import com.housekeeping.admin.entity.GroupManager;
import com.housekeeping.admin.service.*;
import com.housekeeping.admin.service.impl.GroupEmployeesServiceImpl;
import com.housekeeping.common.logs.annotation.LogFlag;
import com.housekeeping.common.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.UnknownHostException;

@Api(value="員工controller",tags={"【员工详情】接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/employeesDetails")
public class EmployeesDetailsController {

    private final EmployeesDetailsService employeesDetailsService;
    private final GroupEmployeesServiceImpl groupEmployeesService;
    private final IEmployeesWorkExperienceService employeesWorkExperienceService;
    private final IEmployeesJobsService employeesJobsService;
    private final IEmployeesCalendarService employeesCalendarService;
    private final IEmployeesPromotionService employeesPromotionService;
    private final IUserService userService;

    @ApiOperation("【公司】新增員工")
    @LogFlag(description = "新增員工")
    @PostMapping("/saveEmp")
    public R saveEmp(@RequestBody EmployeesDetailsDTO employeesDetailsDTO){
        return employeesDetailsService.saveEmp(employeesDetailsDTO,CommonConstants.REQUEST_ORIGIN_COMPANY);
    }

    @ApiOperation("【经理】新增員工")
    @LogFlag(description = "经理新增員工")
    @PostMapping("/saveEmpByMan")
    public R saveEmpByMan(@RequestBody EmployeesDetailsDTO employeesDetailsDTO){
        return employeesDetailsService.saveEmp(employeesDetailsDTO,CommonConstants.REQUEST_ORIGIN_MANAGER);
    }

    @ApiOperation("【公司】修改員工信息")
    @LogFlag(description = "修改員工信息")
    @PostMapping("/updateEmp")
    public R updateEmp(@RequestBody EmployeesDetailsDTO employeesDetailsDTO){
        return employeesDetailsService.updateEmp(employeesDetailsDTO);
    }

    @ApiOperation("【公司】【經理】刪除員工")
    @LogFlag(description = "刪除員工")
    @DeleteMapping("/deleteEmp")
    public R deleteEmp(Integer employeesId){
        EmployeesDetails employeesDetails = employeesDetailsService.getById(employeesId);
        Integer userId = OptionalBean.ofNullable(employeesDetails)
                .getBean(EmployeesDetails::getUserId).get();
        if (CommonUtils.isEmpty(userId)){
            return R.failed("该员工不存在！");
        }
        userService.removeById(userId); //刪除依賴1
        QueryWrapper qw = new QueryWrapper<>();
        qw.eq("employees_id", employeesId);
        groupEmployeesService.remove(qw); //刪除依賴2
        employeesWorkExperienceService.remove(qw); //刪除依賴3
        employeesJobsService.remove(qw); //删除依赖4
        employeesCalendarService.remove(qw); //删除依赖5
        employeesPromotionService.remove(qw); //删除依赖6
        //……
        return R.ok(employeesDetailsService.removeById(employeesId));
    }

    @ApiOperation("【管理员】查詢所有公司員工")
    @LogFlag(description = "查詢員工")
    @GetMapping("/page1")
    public R page1(Page page, PageOfEmployeesDTO pageOfEmployeesDTO){
        return employeesDetailsService.cusPage1(page, pageOfEmployeesDTO, CommonConstants.REQUEST_ORIGIN_ADMIN);
    }

    @ApiOperation("【公司】查詢该公司所有員工")
    @LogFlag(description = "查詢員工")
    @GetMapping("/page2")
    public R page2(Page page, PageOfEmployeesDetailsDTO pageOfEmployeesDetailsDTO){
        return employeesDetailsService.cusPage(page, pageOfEmployeesDetailsDTO, CommonConstants.REQUEST_ORIGIN_COMPANY);
    }

    @ApiOperation("【经理】查詢所在公司所有員工")
    @LogFlag(description = "查詢員工")
    @GetMapping("/page3")
    public R page3(Page page, PageOfEmployeesDetailsDTO pageOfEmployeesDetailsDTO){
        return employeesDetailsService.cusPage(page, pageOfEmployeesDetailsDTO, CommonConstants.REQUEST_ORIGIN_MANAGER);
    }

    @ApiOperation("【公司】【經理】根据id生成员工登入参数")
    @GetMapping("/getLinkToLogin/{id}")
    public R getLinkToLogin(@PathVariable Integer id, @RequestParam("h") Long h) throws UnknownHostException {
        return employeesDetailsService.getLinkToLogin(id, h);
    }

    @ApiOperation("【员工】上传头像")
    @PostMapping("/uploadHead")
    public R uploadHead(@RequestParam("file") MultipartFile file) throws IOException {
        Integer reviserId = TokenUtils.getCurrentUserId();
        //服务器存储head
        String fileName = employeesDetailsService.uploadHead(file, reviserId);
        //数据库存储headUrl
        employeesDetailsService.updateHeadUrlByUserId(fileName, reviserId);
        return R.ok("頭像保存成功");
    }
}
