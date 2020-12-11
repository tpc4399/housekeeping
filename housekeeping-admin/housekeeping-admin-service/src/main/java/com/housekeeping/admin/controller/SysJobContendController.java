package com.housekeeping.admin.controller;

import com.housekeeping.admin.service.ISysJobContendService;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author su
 * @Date 2020/12/11 16:09
 */
@Api(tags={"【工作內容】管理接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/sysJobContend")
public class SysJobContendController {

    private final ISysJobContendService sysJobContendService;

    @ApiOperation("获取所有")
    @GetMapping("/all")
    public R getAll(){
        return R.ok(sysJobContendService.list());
    }

    @ApiOperation("【管理员】添加")
    @GetMapping("/add")
    public R add(String contend){
        return sysJobContendService.add(contend);
    }

    @ApiOperation("【管理员】刪除")
    @DeleteMapping
    public R delete(Integer id){
        sysJobContendService.removeById(id);
        return R.ok("刪除成功");
    }

}
