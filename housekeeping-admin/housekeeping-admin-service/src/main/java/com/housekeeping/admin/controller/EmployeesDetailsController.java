package com.housekeeping.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.housekeeping.admin.dto.EmployeesDetailsDTO;
import com.housekeeping.admin.dto.EmployeesWorkExperienceDTO;
import com.housekeeping.admin.dto.PageOfEmployeesDTO;
import com.housekeeping.admin.dto.PageOfEmployeesDetailsDTO;
import com.housekeeping.admin.entity.CompanyDetails;
import com.housekeeping.admin.entity.EmployeesDetails;
import com.housekeeping.admin.entity.GroupEmployees;
import com.housekeeping.admin.entity.GroupManager;
import com.housekeeping.admin.service.*;
import com.housekeeping.admin.service.impl.GroupEmployeesServiceImpl;
import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.logs.annotation.LogFlag;
import com.housekeeping.common.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
    private final IEmployeesContractService employeesContractService;
    private final IUserService userService;

    @Access({RolesEnum.USER_COMPANY})
    @ApiOperation("【公司】新增員工,新接口，头像信息一起传")
    @LogFlag(description = "新增員工")
    @PostMapping("/add")
    public R add(@RequestParam("name") String name,
                 @RequestParam("sex") Boolean sex,
                 @RequestParam("dateOfBirth") LocalDate dateOfBirth,
                 @RequestParam("idCard") String idCard,
                 @RequestParam("address1") String address1,
                 @RequestParam("address2") String address2,
                 @RequestParam("address3") String address3,
                 @RequestParam("address4") String address4,
                 @RequestParam("lng") Float lng,
                 @RequestParam("lat") Float lat,
                 @RequestParam("educationBackground") String educationBackground,
                 @RequestParam("phonePrefix") String phonePrefix,
                 @RequestParam("phone") String phone,
                 @RequestParam("accountLine") String accountLine,
                 @RequestParam("describes") String describes,
                 @RequestParam("workYear") String workYear,
                 @RequestParam("workExperiencesDTO") List<EmployeesWorkExperienceDTO> workExperiencesDTO,
                 @RequestParam("jobIds") List<Integer> jobIds,
                 @RequestParam("image") MultipartFile image){

        /* 存储头像 */
        String headerUrls = employeesDetailsService.setHeader(image);
        /* 存储员工实体 */
        return R.ok();
    }

    @Access({RolesEnum.USER_COMPANY})
    @ApiOperation("【公司】新增員工")
    @LogFlag(description = "新增員工")
    @PostMapping("/saveEmp")
    public R saveEmp(@RequestBody EmployeesDetailsDTO employeesDetailsDTO) throws ParseException {
        return employeesDetailsService.saveEmp(employeesDetailsDTO,CommonConstants.REQUEST_ORIGIN_COMPANY);
    }

    @Access({RolesEnum.USER_MANAGER})
    @ApiOperation("【经理】新增員工")
    @LogFlag(description = "经理新增員工")
    @PostMapping("/saveEmpByMan")
    public R saveEmpByMan(@RequestBody EmployeesDetailsDTO employeesDetailsDTO) throws ParseException {
        return employeesDetailsService.saveEmp(employeesDetailsDTO,CommonConstants.REQUEST_ORIGIN_MANAGER);
    }

    @Access({RolesEnum.SYSTEM_ADMIN, RolesEnum.USER_COMPANY, RolesEnum.USER_MANAGER,  RolesEnum.USER_EMPLOYEES})
    @ApiOperation("【管理员】【公司】【经理】【员工】修改員工信息")
    @LogFlag(description = "修改員工信息")
    @PostMapping("/updateEmp")
    public R updateEmp(@RequestBody EmployeesDetailsDTO employeesDetailsDTO) throws ParseException {
        return employeesDetailsService.updateEmp(employeesDetailsDTO);
    }

    @Access({RolesEnum.SYSTEM_ADMIN, RolesEnum.USER_COMPANY, RolesEnum.USER_MANAGER})
    @ApiOperation("【管理员】【公司】【經理】刪除員工")
    @LogFlag(description = "刪除員工")
    @DeleteMapping("/deleteEmp")
    public R deleteEmp(Integer employeesId){
        return employeesDetailsService.cusRemove(employeesId);
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @ApiOperation("【管理员】查詢所有公司員工")
    @LogFlag(description = "查詢員工")
    @GetMapping("/page1")
    public R page1(Page page, PageOfEmployeesDTO pageOfEmployeesDTO){
        return employeesDetailsService.cusPage1(page, pageOfEmployeesDTO, CommonConstants.REQUEST_ORIGIN_ADMIN);
    }

    @Access({RolesEnum.USER_COMPANY})
    @ApiOperation("【公司】查詢该公司所有員工")
    @LogFlag(description = "查詢員工")
    @GetMapping("/page2")
    public R page2(Page page, PageOfEmployeesDetailsDTO pageOfEmployeesDetailsDTO){
        return employeesDetailsService.cusPage(page, pageOfEmployeesDetailsDTO, CommonConstants.REQUEST_ORIGIN_COMPANY);
    }

    @Access({RolesEnum.USER_MANAGER})
    @ApiOperation("【经理】查詢所在公司所有員工")
    @LogFlag(description = "查詢員工")
    @GetMapping("/page3")
    public R page3(Page page, PageOfEmployeesDetailsDTO pageOfEmployeesDetailsDTO){
        return employeesDetailsService.cusPage(page, pageOfEmployeesDetailsDTO, CommonConstants.REQUEST_ORIGIN_MANAGER);
    }

    @ApiOperation("首页获取公司旗下所有员工")
    @GetMapping("/getAllEmpByCompanyId")
    public R getAllEmpByCompanyId(Integer companyId){
        return employeesDetailsService.getAllEmpByCompanyId(companyId);
    }

    @Access({RolesEnum.USER_COMPANY, RolesEnum.USER_MANAGER})
    @ApiOperation("【公司】【經理】根据id生成员工登入参数")
    @GetMapping("/getLinkToLogin/{id}")
    public R getLinkToLogin(@PathVariable Integer id, @RequestParam("h") Long h) throws UnknownHostException {
        return employeesDetailsService.getLinkToLogin(id, h);
    }

    @Access({RolesEnum.USER_EMPLOYEES})
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

    @Access({RolesEnum.USER_COMPANY, RolesEnum.USER_MANAGER})
    @ApiOperation("【公司】【經理】上传保潔員的头像")
    @PostMapping("/uploadHead2")
    public R uploadHead(@RequestParam("file") MultipartFile file,
                        @RequestParam("employeesId") Integer employeesId) throws IOException {
        Integer reviserId = employeesDetailsService.getById(employeesId).getId();
        //服务器存储head
        String fileName = employeesDetailsService.uploadHead(file, reviserId);
        //数据库存储headUrl
        employeesDetailsService.updateHeadUrlByUserId(fileName, reviserId);
        return R.ok("頭像保存成功");
    }

    @Access({RolesEnum.SYSTEM_ADMIN, RolesEnum.USER_COMPANY, RolesEnum.USER_MANAGER, RolesEnum.USER_EMPLOYEES, RolesEnum.USER_CUSTOMER})
    @ApiOperation("【all】有无排班记录决定能否做钟点， 有无发布包工服务决定能否做包工")
    @GetMapping("/canSheMakeAnWork")
    public R canSheMakeAnWork(Integer employeesId){
        return employeesDetailsService.canSheMakeAnWork(employeesId);
    }

    @Access({RolesEnum.USER_EMPLOYEES})
    @ApiOperation("【保洁员】设置自己的可工作区域，最多设置三个")
    @PutMapping("/workArea")
    public R putWorkArea(List<Integer> areaIds){
        return employeesDetailsService.putWorkArea(areaIds);
    }

    @Access({RolesEnum.USER_EMPLOYEES})
    @ApiOperation("【员工】查看自身信息")
    @GetMapping("getInfoById")
    public R getInfoById(){
        return employeesDetailsService.getInfoById();
    }

    @ApiOperation("首页根据员工id获取详细信息")
    @GetMapping("getDetailById")
    public R getDetailById(Integer empId){
        return employeesDetailsService.getDetailById(empId);
    }

    @ApiOperation("首页查看所有保洁员")
    @GetMapping("/getAllEmployees")
    public R getAllCompany(Page page, EmployeesDetails employeesDetails){
        return R.ok(employeesDetailsService.page(page, Wrappers.query(employeesDetails)));
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @ApiOperation("【管理员】保洁员列表所有查詢所有保洁员")
    @GetMapping("/getAllEmployeesByAdmin")
    public R getAllEmployeesByAdmin(Page page , PageOfEmployeesDTO pageOfEmployeesDTO){
        return employeesDetailsService.getAllEmployeesByAdmin(page, pageOfEmployeesDTO);
    }

    @Access({RolesEnum.USER_COMPANY})
    @ApiOperation("【公司】查詢该公司所有員工(返回工作年限)")
    @LogFlag(description = "查詢員工")
    @GetMapping("/page5")
    public R page5(Page page, PageOfEmployeesDetailsDTO pageOfEmployeesDetailsDTO){
        return employeesDetailsService.cusPage5(page, pageOfEmployeesDetailsDTO);
    }

    @Access({RolesEnum.USER_COMPANY, RolesEnum.USER_MANAGER, RolesEnum.USER_EMPLOYEES})
    @ApiOperation("【公司】【經理】【员工】根据员工id查看分组")
    @GetMapping("/getGroupByEmpId")
    public R getGroupByEmpId(Integer employeesId){
        return employeesDetailsService.getGroupByEmpId(employeesId);
    }

    @Access({RolesEnum.USER_COMPANY,RolesEnum.USER_MANAGER})
    @ApiOperation("【經理、公司】根據多個id查詢員工")
    @LogFlag(description = "查詢員工")
    @PutMapping("/getEmployeesByIds")
    public R getEmployeesByIds(@RequestBody List<Integer> ids){
        return employeesDetailsService.getEmployeesByIds(ids);
    }
}
