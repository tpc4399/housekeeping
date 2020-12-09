package com.housekeeping.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.housekeeping.admin.dto.SysEnterpriseAuthenticationMessagePostDTO;
import com.housekeeping.admin.service.ISysEnterpriseAuthenticationMessageService;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @ApiOperation("【公司】加載申請和材料表單草稿")
    @GetMapping("/mineDraft")
    public R loadingTheDraft(){
        return sysEnterpriseAuthenticationMessageService.loadingTheDraft();
    }

    @ApiOperation("【公司】发布企业认证申请和材料")
    @PostMapping
    public R sendAuthMessage(SysEnterpriseAuthenticationMessagePostDTO authMessageDTO){
        return sysEnterpriseAuthenticationMessageService.sendAuthMessage(authMessageDTO);
    }

    @ApiOperation("【公司】查看我的申請")
    @GetMapping("/viewMine")
    public R viewMine(){
        return sysEnterpriseAuthenticationMessageService.viewMine();
    }

    @ApiOperation("【公司】撤銷申請成為草稿")
    @GetMapping("/undo")
    public R undo(){
        return sysEnterpriseAuthenticationMessageService.undo();
    }

    @ApiOperation("【管理員】按時間順序分頁查詢申請信息")
    @GetMapping("/query")
    public R query(Page page){
        return sysEnterpriseAuthenticationMessageService.query(page);
    }

    @ApiOperation("【管理員】驗證操作：同意或者不同意")
    @GetMapping("/doAudit")
    public R doAudit(Integer id, Boolean isThrough){
        return sysEnterpriseAuthenticationMessageService.doAudit(id, isThrough);
    }

}
