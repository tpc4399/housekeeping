package com.housekeeping.im.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.housekeeping.common.utils.R;
import com.housekeeping.im.entity.ImChatGroup;
import com.housekeeping.im.service.IImMessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
@RequestMapping("/message")
@Api(tags = "消息")
public class ImMessageController {

    @Resource
    @Qualifier(value = "iImMessageService")
    private IImMessageService iImMessageService;


    /**
     * 获取聊天记录
     *
     * @param chatId 如果是单聊，是用户的ID，如果是多聊，是chat id
     * @return json
     */
    @ApiOperation("获取聊天记录")
    @ResponseBody
    @GetMapping("list")
    public R listMessage(String chatId, String fromId, String chatType) {
       return iImMessageService.listMessage(chatId,fromId,chatType);
    }

    /**
     * 获取所有群组
     */
    @ApiOperation("【管理员】获取所有群组")
    @ResponseBody
    @GetMapping("listGroup")
    public R listGroup(ImChatGroup chatGroup,Page page) {
        return iImMessageService.listGroup(chatGroup,page);
    }



    @ApiOperation("【管理员】获取所有聊天记录")
    @ResponseBody
    @GetMapping("getAllMessage")
    public R getAllMessage(Page page) {
        return iImMessageService.getAllMessage(page);
    }



}
