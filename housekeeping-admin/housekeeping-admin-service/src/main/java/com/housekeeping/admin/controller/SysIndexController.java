package com.housekeeping.admin.controller;

import com.housekeeping.admin.dto.IndexQueryDTO;
import com.housekeeping.admin.dto.QueryIndexDTO;
import com.housekeeping.admin.dto.SysIndexAddDto;
import com.housekeeping.admin.dto.SysIndexUpdateDTO;
import com.housekeeping.admin.service.ISysIndexService;
import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(tags={"【首页】接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/index")
public class SysIndexController {

    private final ISysIndexService sysIndexService;

    @Access({RolesEnum.USER_CUSTOMER})
    @GetMapping
    @ApiOperation("【客户】获取所有分类")
    public R getAll(){
        return sysIndexService.getAll();
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @PostMapping
    @ApiOperation("【平台】新增分类")
    public R add(@RequestBody SysIndexAddDto sysIndexAddDto){
        return sysIndexService.add(sysIndexAddDto);
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @PutMapping
    @ApiOperation("【平台】修改分类")
    public R update(@RequestBody SysIndexUpdateDTO dto){
        return sysIndexService.update(dto);
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @ApiOperation("【平台】删除分类")
    @DeleteMapping("/{indexId}")
    public R delete(@PathVariable Integer indexId){
        return sysIndexService.delete(indexId);
    }

    @Access({RolesEnum.USER_CUSTOMER})
    @GetMapping("/getById")
    @ApiOperation("【客户】通过首页分类获取一级分类")
    public R getById(Integer id){
        return sysIndexService.getCusById(id);
    }

    @Access({RolesEnum.USER_CUSTOMER})
    @ApiOperation("【客户】主页搜索")
    @PostMapping("/query")
    public R query(@RequestBody QueryIndexDTO dto) throws InterruptedException {
        return sysIndexService.query(dto);
    }

}
