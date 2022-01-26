package com.housekeeping.admin.controller;

import com.housekeeping.admin.dto.AddJobContendDTO;
import com.housekeeping.admin.dto.JobNoteDTO;
import com.housekeeping.admin.entity.SysJobNote;
import com.housekeeping.admin.service.ISysJobContendService;
import com.housekeeping.admin.service.ISysJobNoteService;
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
@Api(tags={"【工作笔记】管理接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/sysJobNote")
public class SysJobNoteController {

    private final ISysJobNoteService sysJobNoteService;

    @Access({RolesEnum.SYSTEM_ADMIN})
    @PostMapping
    @ApiOperation("【管理员】批量或单个地增加工作笔记")
    public R add(@RequestBody List<SysJobNote> dos){
        return sysJobNoteService.add(dos);
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @PutMapping("/update")
    @ApiOperation("【管理员】修改工作笔记")
    public R update(@RequestBody SysJobNote sysJobNote){
        return R.ok(sysJobNoteService.updateById(sysJobNote));
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @DeleteMapping("/del")
    @ApiOperation("【管理员】批量删除工作笔记")
    public R del(@RequestParam List<Integer> ids){
        return sysJobNoteService.cusRemove(ids);
    }

    @ApiOperation("获取所有工作笔记，ids為null--查詢所有  ids有值--查詢id對應的工作內容標籤")
    @GetMapping("/getAll")
    public R getAll(@RequestParam(value = "ids", required = false) List<Integer> ids){
        return sysJobNoteService.getAll(ids);
    }

    @ApiOperation("通过工作内容id获取所有工作笔记")
    @GetMapping("/getAllByContent")
    public R getAllByContent(@RequestParam Integer contentId){
        return sysJobNoteService.getAllByContent(contentId);
    }

}
