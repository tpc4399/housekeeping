package com.housekeeping.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.housekeeping.admin.entity.ManagerDetails;
import com.housekeeping.admin.service.ManagerDetailsService;
import com.housekeeping.common.logs.annotation.LogFlag;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.net.UnknownHostException;

@Api(value="經理controller",tags={"經理信息管理接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/managerDetails")
public class ManagerDetailsController {

    private final ManagerDetailsService managerDetailsService;

    @ApiOperation("新增經理")
    @LogFlag(description = "新增經理")
    @PostMapping("/saveEmp")
    public R saveEmp(@RequestBody ManagerDetails managerDetails){
        return managerDetailsService.saveEmp(managerDetails);
    }

    @ApiOperation("修改經理信息")
    @LogFlag(description = "修改經理信息")
    @PostMapping("/updateEmp")
    public R updateEmp(@RequestBody ManagerDetails managerDetails){
        return managerDetailsService.updateEmp(managerDetails);
    }

    @ApiOperation("刪除經理")
    @LogFlag(description = "刪除經理")
    @DeleteMapping("/deleteEmp")
    public R deleteEmp(@RequestBody ManagerDetails managerDetails){
        return R.ok(managerDetailsService.removeById(managerDetails));
    }

    @ApiOperation("查詢當前公司經理(所有、id)")
    @LogFlag(description = "查詢經理")
    @GetMapping("/page")
    public R page(Page page, Integer id){
        return R.ok(managerDetailsService.cusPage(page,id));
    }

    @ApiOperation("根据id生成登入链接")
    @GetMapping("/getLinkToLogin/{id}")
    public R getLinkToLogin(@PathVariable Integer id, @RequestParam("h") Long h) throws UnknownHostException {
        return managerDetailsService.getLinkToLogin(id, h);
    }
}
