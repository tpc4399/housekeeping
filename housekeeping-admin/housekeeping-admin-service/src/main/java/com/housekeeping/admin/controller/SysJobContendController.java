package com.housekeeping.admin.controller;

import com.housekeeping.admin.service.ISysJobContendService;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @ApiOperation("根据ids获取树")
    @GetMapping("/getTreeByIds")
    public R getTreeByIds(Integer[] ids){
        return sysJobContendService.getTreeByIds(ids);
    }

}
