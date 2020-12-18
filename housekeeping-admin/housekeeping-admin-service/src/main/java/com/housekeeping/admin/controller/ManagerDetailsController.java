package com.housekeeping.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.housekeeping.admin.dto.ManagerDetailsDTO;

import com.housekeeping.admin.dto.PageOfEmployeesDetailsDTO;
import com.housekeeping.admin.dto.PageOfManagerDTO;
import com.housekeeping.admin.dto.PageOfManagerDetailsDTO;
import com.housekeeping.admin.entity.ManagerDetails;
import com.housekeeping.admin.service.ManagerDetailsService;
import com.housekeeping.common.logs.annotation.LogFlag;
import com.housekeeping.common.utils.CommonConstants;
import com.housekeeping.common.utils.QrCodeUtils;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.UnknownHostException;

@Api(value="經理controller",tags={"【经理详情】接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/managerDetails")
public class ManagerDetailsController {

    private final ManagerDetailsService managerDetailsService;

    @ApiOperation("【公司】新增經理")
    @LogFlag(description = "新增經理")
    @PostMapping("/saveEmp")
    public R saveEmp(@RequestBody ManagerDetailsDTO managerDetailsDTO){
        return managerDetailsService.saveEmp(managerDetailsDTO);
    }

    @ApiOperation("【公司】修改經理信息")
    @LogFlag(description = "修改經理信息")
    @PostMapping("/updateEmp")
    public R updateEmp(@RequestBody ManagerDetailsDTO managerDetailsDTO){
        return managerDetailsService.updateEmp(managerDetailsDTO);
    }

    @ApiOperation("【公司】刪除經理")
    @LogFlag(description = "刪除經理")
    @DeleteMapping("/deleteEmp")
    public R deleteEmp(@RequestBody ManagerDetails managerDetails){
        return R.ok(managerDetailsService.removeById(managerDetails));
    }

    @ApiOperation("【公司】根据id生成登入链接")
    @GetMapping("/getLinkToLogin/{id}")
    public R getLinkToLogin(@PathVariable Integer id, @RequestParam("h") Long h) throws UnknownHostException {
        return managerDetailsService.getLinkToLogin(id, h);
    }

    @ApiOperation("【管理员】查詢所有公司經理")
    @LogFlag(description = "查詢經理")
    @GetMapping("/page1")
    public R page1(Page page, PageOfManagerDTO pageOfEmployeesDTO){
        return managerDetailsService.cusPage1(page, pageOfEmployeesDTO, CommonConstants.REQUEST_ORIGIN_ADMIN);
    }

    @ApiOperation("【公司】查詢该公司所有經理")
    @LogFlag(description = "查詢經理")
    @GetMapping("/page2")
    public R page2(Page page, PageOfManagerDetailsDTO pageOfEmployeesDetailsDTO){
        return managerDetailsService.cusPage(page, pageOfEmployeesDetailsDTO, CommonConstants.REQUEST_ORIGIN_COMPANY);
    }

    @ApiOperation("【经理】上传头像")
    @PostMapping("/uploadHead")
    public R uploadHead(@RequestParam("file") MultipartFile file) throws IOException {
        Integer reviserId = TokenUtils.getCurrentUserId();
        //服务器存储head
        String fileName = managerDetailsService.uploadHead(file, reviserId);
        //数据库存储headUrl
        managerDetailsService.updateHeadUrlByUserId(fileName, reviserId);
        return R.ok("頭像保存成功");
    }
}
