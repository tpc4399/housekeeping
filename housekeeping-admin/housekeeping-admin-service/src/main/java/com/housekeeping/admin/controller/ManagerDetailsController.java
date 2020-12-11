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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
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

    @ApiOperation("【公司】根据id生成登入二维码")
    @GetMapping("/getQrCodeToLogin/{id}")
    public void getQrCodeToLogin(@PathVariable Integer id,
                                 @RequestParam("h") Long h,
                                 HttpServletResponse response) {
        try {
            OutputStream os = response.getOutputStream();
            //从配置文件读取需要生成二维码的连接
            String url = (String) managerDetailsService.getLinkToLogin(id, h).getData();
            //requestUrl:需要生成二维码的连接，logoPath：内嵌图片的路径，os：响应输出流，needCompress:是否压缩内嵌的图片
            QrCodeUtils.encode(url, "", os, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
}
