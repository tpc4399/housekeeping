package com.housekeeping.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.housekeeping.admin.dto.SysEnterpriseAuthenticationMessagePostDTO;
import com.housekeeping.admin.service.ISysEnterpriseAuthenticationMessageService;
import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @Author su
 * @Date 2020/12/8 14:35
 */
@Api(tags={"【企业认证】接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/comAuth")
public class SysEnterpriseAuthenticationMessageController {

    private final ISysEnterpriseAuthenticationMessageService sysEnterpriseAuthenticationMessageService;

    @Access({RolesEnum.USER_COMPANY})
    @ApiOperation("【公司】是否验证")
    @GetMapping("/isValidate")
    public R isValidate(){
        return sysEnterpriseAuthenticationMessageService.isValidate();
    }

    @Access({RolesEnum.USER_COMPANY})
    @ApiOperation("【公司】加載申請和材料表單草稿")
    @GetMapping("/mineDraft")
    public R loadingTheDraft(){
        return sysEnterpriseAuthenticationMessageService.loadingTheDraft();
    }

    @Access({RolesEnum.USER_COMPANY})
    @ApiOperation("【公司】发布企业认证申请和材料")
    @PostMapping
    public R sendAuthMessage(@RequestBody SysEnterpriseAuthenticationMessagePostDTO authMessageDTO){
        return sysEnterpriseAuthenticationMessageService.sendAuthMessage(authMessageDTO);
    }

    @Access({RolesEnum.USER_COMPANY})
    @ApiOperation("【公司】查看我的申請")
    @GetMapping("/viewMine")
    public R viewMine(){
        return sysEnterpriseAuthenticationMessageService.viewMine();
    }

    @Access({RolesEnum.USER_COMPANY})
    @ApiOperation("【公司】撤銷申請成為草稿")
    @GetMapping("/undo")
    public R undo(){
        return sysEnterpriseAuthenticationMessageService.undo();
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @ApiOperation("【管理員】按時間順序分頁查詢申請信息")
    @GetMapping("/query")
    public R query(Page page){
        return sysEnterpriseAuthenticationMessageService.query(page);
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @ApiOperation("【管理員】驗證操作：同意或者不同意")
    @GetMapping("/doAudit")
    public R doAudit(Integer id, Boolean isThrough){
        return sysEnterpriseAuthenticationMessageService.doAudit(id, isThrough);
    }

}
