package com.housekeeping.admin.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.housekeeping.admin.dto.IndexQueryDTO;
import com.housekeeping.admin.entity.SysIndex;
import com.housekeeping.admin.service.ISysIndexService;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(value="首頁controller",tags={"【首页】接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/index")
public class SysIndexController {

    private final ISysIndexService sysIndexService;

    @GetMapping("getAll")
    @ApiOperation("【客户】获取所有分类")
    public R getAll(Page page){
        return R.ok(sysIndexService.page(page,new QueryWrapper<>()));
    }

    @PostMapping
    @ApiOperation("【平台】新增分类")
    public R add(@RequestBody SysIndex sysIndex){
        return R.ok(sysIndexService.save(sysIndex));
    }

    @GetMapping("getById")
    @ApiOperation("【客户】通过首页分类获取一级分类")
    public R getById(Integer id){
        return sysIndexService.getCusById(id);
    }

    @ApiOperation("【客户】搜索")
    @PostMapping("/query")
    public R query(@RequestBody IndexQueryDTO indexQueryDTO){
        return sysIndexService.query(indexQueryDTO);
    }

}
