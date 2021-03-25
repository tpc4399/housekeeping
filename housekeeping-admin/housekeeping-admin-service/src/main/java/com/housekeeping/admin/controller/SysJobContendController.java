package com.housekeeping.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.housekeeping.admin.dto.AddJobContendDTO;
import com.housekeeping.admin.service.ISysJobContendService;
import com.housekeeping.admin.vo.SysJobContendVo;
import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Access({RolesEnum.SYSTEM_ADMIN})
    @PostMapping
    @ApiOperation("【管理员】批量或单个地增加工作内容")
    public R add(@RequestBody List<AddJobContendDTO> dos){
        return sysJobContendService.add(dos);
    }

    @ApiOperation("获取所有工作内容标签，ids為null--查詢所有  ids有值--查詢id對應的工作內容標籤")
    @GetMapping("/getAll")
    public R getAll(@RequestParam(value = "ids", required = false) List<Integer> ids){
        return sysJobContendService.getAll(ids);
    }

    @ApiOperation("获取所有工作内容标签, 根据'1000 1001 1002 1003'这样的字符串ids查询工作内容，参数只能含有数字字符以及空格")
    @GetMapping("/getAll2")
    public R getAll2(String ids){
        return sysJobContendService.getAll2(ids);
    }

}
