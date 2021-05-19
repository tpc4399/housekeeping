package com.housekeeping.admin.controller;

import com.housekeeping.admin.dto.*;
import com.housekeeping.admin.service.IQueryService;
import com.housekeeping.admin.service.ISysIndexService;
import com.housekeeping.auth.annotation.PassToken;
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
    private final IQueryService queryService;

    @GetMapping
    @ApiOperation("获取所有分类")
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

    @GetMapping("/getById")
    @ApiOperation("通过首页大类获取下面工作内容标签")
    public R getById(Integer id){
        return sysIndexService.getCusById(id);
    }

    @ApiOperation("输入关键词进行搜索")
    @PostMapping("/query")
    public R query(@RequestBody QueryParamsDTO dto) {
        return sysIndexService.query(dto);
    }

    @ApiOperation("主页搜索,新接口 2021-5-9 18:12")
    @PostMapping("/query2")
    public R querySimplifiedVersion(@RequestBody QueryDTO dto) throws InterruptedException {
        return queryService.query(dto);
    }

    @GetMapping("/tree")
    @ApiOperation("获取六大类包括类下的工作内容")
    public R tree(){
        return sysIndexService.tree();
    }

    @ApiOperation("获取默认推荐,如果没有地址")
    @PostMapping("/defaultRecommendation")
    public R defaultRecommendation(@RequestBody AddressDTO dto){
        return sysIndexService.defaultRecommendation(dto);
    }

    @ApiOperation("获取默认推荐,更多保洁员,点击更多保洁员时调用")
    @PostMapping("/more1")
    public R more1(@RequestBody AddressDTO dto){
        return sysIndexService.more1(dto);
    }

    @ApiOperation("获取默认推荐,继续获取保洁员，点击加载更多时调用")
    @GetMapping("/goon1")
    public R goon1(String credential){
        return sysIndexService.goon1(credential);
    }

    @ApiOperation("获取默认推荐,清除缓存，离开更多保洁员界面并且确定不需要缓存时调用")
    @PostMapping("/flush1")
    public R flush1(String credential){
        return sysIndexService.flush1(credential);
    }

    @ApiOperation("获取默认推荐,更多公司，点击更公司时调用")
    @PostMapping("/more2")
    public R more2(@RequestBody AddressDTO dto){
        return sysIndexService.more2(dto);
    }

    @ApiOperation("获取默认推荐,继续获取公司，点击加载更多时调用")
    @GetMapping("/goon2")
    public R goon2(String credential){
        return sysIndexService.goon2(credential);
    }

    @ApiOperation("获取默认推荐,继续获取公司，离开更多公司界面并且确定不需要缓存时调用")
    @GetMapping("/flush2")
    public R flush2(String credential){
        return sysIndexService.flush2(credential);
    }



}
