package com.housekeeping.admin.controller;

import com.housekeeping.admin.dto.GroupEmployeesDTO;
import com.housekeeping.admin.dto.GroupManagerDTO;
import com.housekeeping.admin.service.IGroupEmployeesService;
import com.housekeeping.admin.service.IGroupManagerService;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @Author su
 * @create 2020/11/25 14:58
 */
@Api(value="分組controller",tags={"【分組&经理】中間表接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/groupManager")
public class GroupManagerController {

    private final IGroupManagerService groupManagerService;

    @ApiOperation("【公司】分组批量添加员工")
    @PostMapping
    public R add(@RequestBody GroupManagerDTO groupManagerDTO){
        return groupManagerService.add(groupManagerDTO);
    }

    @ApiOperation("【公司】分组批量刪除员工")
    @DeleteMapping
    public R delete(@RequestBody GroupManagerDTO groupManagerDTO){
        return groupManagerService.delete(groupManagerDTO);
    }

}
