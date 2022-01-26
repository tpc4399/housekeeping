package com.housekeeping.admin.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.housekeeping.admin.service.InvitationService;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Api(tags={"【链接分享】管理接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/invitation")
public class InvitationController {

    private final InvitationService invitationService;

    @ApiOperation("根据用户id查询被邀请人列表及佣金")
    @GetMapping("/getAllInvitees")
    public R getAllInvitees(Integer userId){
        return invitationService.getAllInvitees(userId);
    }

    @ApiOperation("根据用户id查看推广订单")
    @GetMapping("/getOrders")
    public R getOrders(Integer userId){
        return invitationService.getOrders(userId);
    }

    @ApiOperation("管理员获取邀请人列表")
    @GetMapping("/getAllUser")
    public R getAllUser(Page page){
        return invitationService.getAllUser(page);
    }




}
