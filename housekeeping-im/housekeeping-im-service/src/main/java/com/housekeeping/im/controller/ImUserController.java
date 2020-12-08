package com.housekeeping.im.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.TokenUtils;
import com.housekeeping.im.common.utils.ChatUtils;
import com.housekeeping.im.entity.*;
import com.housekeeping.im.service.IImMessageService;
import com.housekeeping.im.service.IImUserService;
import com.housekeeping.im.service.impl.ImChatGroupServiceImpl;
import com.housekeeping.im.service.impl.ImChatGroupUserServiceImpl;
import com.housekeeping.im.tio.StartTioRunner;
import com.housekeeping.im.tio.TioServerConfig;
import com.housekeeping.im.tio.WsOnlineContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.server.ServerGroupContext;
import org.tio.websocket.common.WsResponse;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "用户")
@RestController
@RequestMapping("/user")
public class ImUserController {

    private final Logger logger = LoggerFactory.getLogger(ImUserController.class);

    @Resource
    private StartTioRunner startTioRunner;

    @Resource
    @Qualifier(value = "imUserService")
    private IImUserService imUserService;


    @Resource
    @Qualifier(value = "iImMessageService")
    private IImMessageService iImMessageService;

    @Resource
    @Qualifier(value = "imChatGroupServiceImpl")
    private ImChatGroupServiceImpl imChatGroupService;

    @Resource
    @Qualifier(value = "imChatGroupUserService")
    private ImChatGroupUserServiceImpl imChatGroupUserService;
    /**
     * 用户信息初始化
     * @return json
     */
    @ApiOperation("初始化用户信息")
    @GetMapping("/init")
    public Map<String, Object> list() {
        logger.debug("init");
        Map<String, Object> objectMap = new HashMap<>();
        String userId = TokenUtils.getCurrentUserId().toString();

        ImUser user = imUserService.getById(userId);

        QueryWrapper<ImUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", userId);
        user.setPassword(null);
        objectMap.put("me", user);

        //用户的群组信息
        objectMap.put("groups", imUserService.getChatGroups(userId.toString()));
        return objectMap;
    }


    /**
     * 获取群组的用户
     *
     * @param chatId 群组id
     * @return 用户List
     */
    @ApiOperation("获取群组的用户")
    @GetMapping("/chatUserList")
    public List<ImUser> chatUserList(@RequestParam String chatId) {
        return imUserService.getChatUserList(chatId);
    }

    /**
     * 发送信息给用户
     * 注意：目前仅支持发送给在线用户
     *
     * @param userId 接收方id
     * @param msg    消息内容
     */
    @ApiOperation("发送信息给用户")
    @PostMapping("/sendMsg")
    public void sendMsg(String userId, String msg, HttpServletRequest request) throws Exception {
        String host = ChatUtils.getHost(request);
        ServerGroupContext serverGroupContext = startTioRunner.getAppStarter().getWsServerStarter().getServerGroupContext();

        SendInfo sendInfo = new SendInfo();
        sendInfo.setCode(ChatUtils.MSG_MESSAGE);
        Message message = new Message();
        message.setId("system");
        message.setFromid("system");
        message.setContent(msg);
        message.setMine(false);
        message.setTimestamp(System.currentTimeMillis());
        message.setType(ChatUtils.FRIEND);
        message.setUsername("系统消息");
        sendInfo.setMessage(message);

        ChannelContext cc = WsOnlineContext.getChannelContextByUser(userId);
        if (cc != null && !cc.isClosed) {
            WsResponse wsResponse = WsResponse.fromText(new ObjectMapper().writeValueAsString(sendInfo), TioServerConfig.CHARSET);
            Tio.sendToUser(serverGroupContext, userId, wsResponse);
        } else {
            saveMessage(message, ChatUtils.UNREAD, userId);
        }
    }


    private void saveMessage(Message message, String readStatus, String userId) {
        ImMessage imMessage = new ImMessage();
        imMessage.setToId(userId);
        imMessage.setFromId(message.getFromid());
        imMessage.setSendTime(System.currentTimeMillis());
        imMessage.setContent(message.getContent());
        imMessage.setReadStatus(readStatus);
        imMessage.setType(message.getType());
        iImMessageService.saveMessage(imMessage);
    }

    @GetMapping("/addGroup")
    @ApiOperation("发起群聊")
    public void createGroup(@RequestParam String toId){
        String currentUserId = TokenUtils.getCurrentUserId().toString();
        ImChatGroup imChatGroup = new ImChatGroup();
        imChatGroup.setName("临时群聊"+ CommonUtils.getRandomSixCode());
        imChatGroup.setCreateDate(LocalDateTime.now());
        imChatGroupService.save(imChatGroup);
        Integer maxId = ((ImChatGroup) CommonUtils.getMaxId("im_chat_group", imChatGroupService)).getId();
        List<String> ids = new ArrayList<>();
        ids.add(currentUserId);
        ids.add(toId);
        for (int i = 0; i < ids.size(); i++) {
            ImChatGroupUser imChatGroupUser = new ImChatGroupUser();
            imChatGroupUser.setChatGroupId(maxId);
            imChatGroupUser.setCreateDate(LocalDateTime.now());
            imChatGroupUser.setUserId(ids.get(i));
            imChatGroupUserService.save(imChatGroupUser);
        }
    }

}
