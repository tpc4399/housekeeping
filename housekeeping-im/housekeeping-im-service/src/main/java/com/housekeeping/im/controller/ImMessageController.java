package com.housekeeping.im.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.housekeeping.im.entity.ImMessage;
import com.housekeeping.im.entity.ImUser;
import com.housekeeping.im.entity.Message;
import com.housekeeping.im.service.IImMessageService;
import com.housekeeping.im.service.IImUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;


@RestController
@RequestMapping("/message")
@Api(tags = "消息")
public class ImMessageController {

    public static final int PAGE_SIZE = 20;

    public static final String FRIEND = "0";

    @Resource
    @Qualifier(value = "iImMessageService")
    private IImMessageService iImMessageService;

    @Resource
    @Qualifier(value = "imUserService")
    private IImUserService imUserService;


    /**
     * 获取聊天记录
     *
     * @param chatId 如果是单聊，是用户的ID，如果是多聊，是chat id
     * @return json
     */
    @ApiOperation("获取聊天记录")
    @ResponseBody
    @GetMapping("list")
    public Map<String, Object> list(String chatId, String fromId, String chatType, Long pageNo) {
        if (StringUtils.isEmpty(chatId) || StringUtils.isEmpty(fromId)) {
            return new HashMap<>();
        }
        Page<ImMessage> page = new Page<>();
        page.setSize(PAGE_SIZE);
        if (pageNo == null) {
            pageNo = 0L;
        }
        page.setCurrent(pageNo);
        page.setDesc("send_time");
        QueryWrapper<ImMessage> wrapper = new QueryWrapper<>();
        System.out.println(FRIEND);
        if(FRIEND.equals(chatType)){
            wrapper.and(wrapper1 -> wrapper1.eq("to_id", chatId)
                    .eq("from_id", fromId));
            wrapper.or(wrapper2 -> wrapper2.eq("from_id", chatId)
                    .eq("to_id", fromId));
        }else {
            wrapper.eq("to_id",chatId);
        }
        IPage<ImMessage> messageIPage = iImMessageService.page(page, wrapper);

        List<ImMessage> imMessageList = messageIPage.getRecords();
        List<Message> messageList = new ArrayList<>();
        for (ImMessage imMessage : imMessageList) {
            Message message = new Message();
            message.setId(imMessage.getToId());
            message.setMine(fromId.equals(imMessage.getFromId()));
            message.setType(imMessage.getType());
            ImUser imUser = imUserService.getById(imMessage.getFromId());
            message.setUsername(imUser.getName());
            message.setFromid(imMessage.getFromId());
            message.setCid(String.valueOf(imMessage.getId()));
            message.setContent(imMessage.getContent());
            message.setTimestamp(new Date().getTime());
            messageList.add(message);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("messageList", messageList);
        map.put("pageNo", pageNo);
        map.put("count", messageIPage.getTotal());
        map.put("pageSize", messageIPage.getSize());
        return map;
    }

}
