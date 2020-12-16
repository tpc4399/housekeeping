package com.housekeeping.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.housekeeping.admin.dto.EmployeesDetailsDTO;
import com.housekeeping.admin.dto.PageOfEmployeesDTO;
import com.housekeeping.admin.dto.PageOfEmployeesDetailsDTO;
import com.housekeeping.admin.entity.EmployeesDetails;
import com.housekeeping.admin.service.EmployeesDetailsService;
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

@Api(value="員工controller",tags={"【员工详情】接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/employeesDetails")
public class EmployeesDetailsController {

    private final EmployeesDetailsService employeesDetailsService;

    @ApiOperation("【公司】新增員工")
    @LogFlag(description = "新增員工")
    @PostMapping("/saveEmp")
    public R saveEmp(@RequestBody EmployeesDetailsDTO employeesDetailsDTO){
        return employeesDetailsService.saveEmp(employeesDetailsDTO);
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
    public R deleteEmp(@RequestBody EmployeesDetails employeesDetails){
        return R.ok(employeesDetailsService.removeById(employeesDetails));
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

    @ApiOperation("【公司】【經理】根据id生成员工登入链接")
    @GetMapping("/getLinkToLogin/{id}")
    public R getLinkToLogin(@PathVariable Integer id, @RequestParam("h") Long h) throws UnknownHostException {
        return employeesDetailsService.getLinkToLogin(id, h);
    }

    @ApiOperation("【公司】【經理】根据id生成员工登入二维码")
    @GetMapping("/getQrCodeToLogin/{id}")
    public void getQrCodeToLogin(@PathVariable Integer id,
                              @RequestParam("h") Long h,
                              HttpServletResponse response) {
        try {
            OutputStream os = response.getOutputStream();
            //从配置文件读取需要生成二维码的连接
            String url = (String) employeesDetailsService.getLinkToLogin(id, h).getData();
            //requestUrl:需要生成二维码的连接，logoPath：内嵌图片的路径，os：响应输出流，needCompress:是否压缩内嵌的图片
            QrCodeUtils.encode(url, "", os, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
