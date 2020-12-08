package com.housekeeping.admin.controller;

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
@Api(tags={"【企业认证】相关接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/comAuth")
public class SysEnterpriseAuthenticationMessageController {

    private final ISysEnterpriseAuthenticationMessageService sysEnterpriseAuthenticationMessageService;

    @ApiOperation("【公司】加載申請和材料表單草稿")
    @GetMapping("/mineDraft")
    public R loadingTheDraft(){
        return R.ok();
    }

    @ApiOperation("【公司】发布企业认证申请和材料")
    @PostMapping
    public R sendAuthMessage(SysEnterpriseAuthenticationMessagePostDTO authMessageDTO){
        return sysEnterpriseAuthenticationMessageService.sendAuthMessage(authMessageDTO);
    }

}
