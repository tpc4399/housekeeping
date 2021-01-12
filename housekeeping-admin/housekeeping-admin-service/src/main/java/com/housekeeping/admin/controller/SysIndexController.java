package com.housekeeping.admin.controller;

import com.housekeeping.admin.dto.IndexQueryDTO;
import com.housekeeping.admin.service.ISysIndexService;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author su
 * @Date 2021/1/12 14:49
 */
@Api(tags={"【主页】接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/sysIndex")
public class SysIndexController {

    private final ISysIndexService sysIndexService;

    @ApiOperation("【客户】搜索")
    @PostMapping("/query")
    public R query(IndexQueryDTO indexQueryDTO){
        return R.ok();
    }

}
