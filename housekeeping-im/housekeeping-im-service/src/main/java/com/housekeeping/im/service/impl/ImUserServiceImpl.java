package com.housekeeping.im.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
import com.housekeeping.im.common.utils.ChatUtils;
import com.housekeeping.im.controller.ImUserController;
import com.housekeeping.im.entity.*;
import com.housekeeping.im.mapper.ImUserMapper;
import com.housekeeping.im.service.IImMessageService;
import com.housekeeping.im.service.IImUserService;
import com.housekeeping.im.tio.StartTioRunner;
import com.housekeeping.im.tio.TioServerConfig;
import com.housekeeping.im.tio.WsOnlineContext;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.server.ServerGroupContext;
import org.tio.websocket.common.WsResponse;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Qualifier(value = "imUserService")
public class ImUserServiceImpl extends ServiceImpl<ImUserMapper, ImUser> implements IImUserService {

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

    @Override
    public List<ImChatGroup> getChatGroups(String userId) {
        return baseMapper.getUserGroups(userId);
    }

    @Override
    public R getChatUserList(String chatId) {
        List<ImUserInfo> chatUserList = baseMapper.getChatUserList(chatId);
        chatUserList.forEach(x -> x.setHeadUrl(CommonUtils.isEmpty(getHeadUrl(x.getId(), x.getDeptId()))?"":getHeadUrl(x.getId(), x.getDeptId())));
        return R.ok(chatUserList);
    }

    @Override
    public R createGroup(String toId) {
        String currentUserId = TokenUtils.getCurrentUserId().toString();
        ImChatGroup imChatGroup = new ImChatGroup();
        imChatGroup.setName("临时群聊"+ CommonUtils.getRandomSixCode());
        imChatGroup.setCreateDate(LocalDateTime.now());
        Integer companyId = baseMapper.getCompanyId(Integer.parseInt(toId));
        Integer userId = baseMapper.getUserIdByCom(companyId);
        imChatGroup.setCompanyId(companyId);
        imChatGroupService.save(imChatGroup);
        Integer maxId = ((ImChatGroup) CommonUtils.getMaxId("im_chat_group", imChatGroupService)).getId();
        Integer empId = baseMapper.getEmpId(Integer.parseInt(toId));
        Set<String> ids = new HashSet<>();
        ids.add(currentUserId);
        ids.add(userId.toString());
        List<Integer> groups =  baseMapper.getGroupsById(Integer.parseInt(toId));
        if(CollectionUtils.isEmpty(groups)){
            ids.add(empId.toString());
        }else {
            ids.add(empId.toString());
            for (int i = 0; i < groups.size(); i++) {
                List<Integer> manIds = baseMapper.getMansByGroupId(groups.get(i));
                for (int j = 0; j < manIds.size(); j++) {
                    ids.add(baseMapper.getUserId(manIds.get(j)).toString());
                }
            }
        }
        for (String id : ids) {
            ImChatGroupUser imChatGroupUser = new ImChatGroupUser();
            imChatGroupUser.setChatGroupId(maxId);
            imChatGroupUser.setCreateDate(LocalDateTime.now());
            imChatGroupUser.setUserId(id);
            imChatGroupUserService.save(imChatGroupUser);
        }
        return R.ok("聊天组创建成功");
    }

    @Override
    public R sendMsg(String userId, String msg, HttpServletRequest request) throws JsonProcessingException {
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
        return R.ok("发送消息成功");
    }

    @Override
    public R init() {
        logger.debug("init");
        Map<String, Object> objectMap = new HashMap<>();
        String userId = TokenUtils.getCurrentUserId().toString();

        ImUser byId = imUserService.getById(userId);
        ImUserInfo user = new ImUserInfo();
        user.setId(byId.getId());
        user.setNumber(byId.getNumber());
        user.setDeptId(byId.getDeptId());
        user.setName(byId.getName());
        user.setNickname(byId.getNickname());
        String headUrl = getHeadUrl(user.getId(), user.getDeptId());
        user.setHeadUrl(headUrl);
        user.setDateOfBirth(byId.getDateOfBirth());

        QueryWrapper<ImUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", userId);
        user.setPassword(null);
        objectMap.put("me", user);

        //用户的群组信息
        objectMap.put("groups", imUserService.getChatGroups(userId.toString()));
        return R.ok(objectMap);
    }

    @Override
    public R createGroupByCompany(String toId) {

        String currentUserId = TokenUtils.getCurrentUserId().toString();
        ImChatGroup imChatGroup = new ImChatGroup();
        imChatGroup.setName("临时群聊"+ CommonUtils.getRandomSixCode());
        imChatGroup.setCreateDate(LocalDateTime.now());
        Integer companyId = baseMapper.getCompanyId(Integer.parseInt(toId));
        imChatGroup.setCompanyId(companyId);
        imChatGroupService.save(imChatGroup);

        ArrayList<String> strings = new ArrayList<>();
        strings.add(currentUserId);
        strings.add(toId);
        Integer maxId = ((ImChatGroup) CommonUtils.getMaxId("im_chat_group", imChatGroupService)).getId();
        for (int i = 0; i < strings.size(); i++) {
            ImChatGroupUser imChatGroupUser = new ImChatGroupUser();
            imChatGroupUser.setChatGroupId(maxId);
            imChatGroupUser.setCreateDate(LocalDateTime.now());
            imChatGroupUser.setUserId(strings.get(i));
            imChatGroupUserService.save(imChatGroupUser);
        }
        return R.ok("聊天组创建成功");
    }

    @Override
    public R createGroupByCus(String toId, String empId) {
        String currentUserId = TokenUtils.getCurrentUserId().toString();
        ImChatGroup imChatGroup = new ImChatGroup();
        imChatGroup.setName("临时群聊"+ CommonUtils.getRandomSixCode());
        imChatGroup.setCreateDate(LocalDateTime.now());
        Integer companyId = baseMapper.getCompanyId(Integer.parseInt(toId));
        imChatGroup.setCompanyId(companyId);
        imChatGroupService.save(imChatGroup);

        ArrayList<String> strings = new ArrayList<>();
        strings.add(currentUserId);
        strings.add(toId);
        strings.add(empId);
        Integer maxId = ((ImChatGroup) CommonUtils.getMaxId("im_chat_group", imChatGroupService)).getId();
        for (int i = 0; i < strings.size(); i++) {
            ImChatGroupUser imChatGroupUser = new ImChatGroupUser();
            imChatGroupUser.setChatGroupId(maxId);
            imChatGroupUser.setCreateDate(LocalDateTime.now());
            imChatGroupUser.setUserId(strings.get(i));
            imChatGroupUserService.save(imChatGroupUser);
        }
        return R.ok("聊天组创建成功");
    }


    private void saveMessage(Message message, String readStatus, String userId) {
        ImMessage imMessage = new ImMessage();
        imMessage.setToId(userId);
        imMessage.setFromId(message.getFromid());
        imMessage.setSendTime(System.currentTimeMillis());
        imMessage.setContent(message.getContent());
        imMessage.setReadStatus(readStatus);
        imMessage.setType(message.getType());
        imMessage.setMsgType(message.getMsgtype());
        iImMessageService.saveMessage(imMessage);
    }

    public String getHeadUrl(Integer userId,Integer deptId){
        String headUrl = null;
        if(deptId.equals(2)){
            headUrl =  baseMapper.getCompanyLogo(userId);
        }
        if(deptId.equals(3)){
            headUrl =  baseMapper.customerHeadUrl(userId);
        }
        if(deptId.equals(4)){
            headUrl =  baseMapper.managerHeadUrl(userId);
        }
        if(deptId.equals(5)){
            headUrl =  baseMapper.employeesHeadUrl(userId);
        }
        return headUrl;
    }

}
