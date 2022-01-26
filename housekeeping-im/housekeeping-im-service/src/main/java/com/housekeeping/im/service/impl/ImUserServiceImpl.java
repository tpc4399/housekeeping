package com.housekeeping.im.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
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
import org.checkerframework.checker.units.qual.A;
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
import java.util.stream.Collectors;

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
    public List<ImChatGroupVo> getChatGroups(String userId) {
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

        //员工userId
        Integer empId = baseMapper.getEmpId(Integer.parseInt(toId));

        //客户userId
        String currentUserId = TokenUtils.getCurrentUserId().toString();

        String check;
        if(empId<Integer.parseInt(currentUserId)){
            check = new StringBuilder().append(empId).append(",").append(currentUserId).append(",").toString();
        }else {
            check = new StringBuilder().append(currentUserId).append(",").append(empId).append(",").toString();
        }

        List<Integer> groupIds = baseMapper.getAllGroupId();
        for (int i = 0; i < groupIds.size(); i++) {
            QueryWrapper<ImChatGroupUser> qw = new QueryWrapper<>();
            qw.eq("chat_group_id",groupIds.get(i));
            qw.orderByAsc("user_id");
            List<ImChatGroupUser> list = imChatGroupUserService.getBaseMapper().selectList(qw);
            StringBuilder sb = new StringBuilder();
            for (int i1 = 0; i1 < list.size(); i1++) {
                sb.append(list.get(i1).getUserId()).append(",");
            }
            if(sb.toString().contains(empId.toString())&&sb.toString().contains(currentUserId)){
                return R.ok(groupIds.get(i));
            }
        }
        CustomerDetails customer = baseMapper.getCustomerByUser(currentUserId);
        EmployeesDetails employeesByUser = baseMapper.getEmployeesByUser(empId.toString());
        ImChatGroup imChatGroup = new ImChatGroup();
        imChatGroup.setEmployeesName(employeesByUser.getName());
        imChatGroup.setAvatarEmployees(employeesByUser.getHeadUrl());
        imChatGroup.setCustomerName(customer.getName());
        imChatGroup.setAvatarCustomer(customer.getHeadUrl());
        imChatGroup.setName("临时群聊"+ CommonUtils.getRandomSixCode());
        imChatGroup.setCreateDate(LocalDateTime.now());

        Integer userId = null;
        Integer companyId = baseMapper.getCompanyId(Integer.parseInt(toId));
        if(companyId!=null){
            userId  = baseMapper.getUserIdByCom(companyId);
            imChatGroup.setCompanyId(companyId);
        }
        imChatGroupService.save(imChatGroup);

        Integer maxId = ((ImChatGroup) CommonUtils.getMaxId("im_chat_group", imChatGroupService)).getId();
        Set<String> ids = new HashSet<>();

        //群聊添加客户
        ids.add(currentUserId);

        //群聊加入保洁员
        ids.add(empId.toString());

        //群聊添加公司账户
        if(companyId!=null){
            ids.add(userId.toString());
        }

        //群聊加入经理
        List<Integer> groups =  baseMapper.getGroupsById(Integer.parseInt(toId));
        if(!CollectionUtils.isEmpty(groups)){
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
            imChatGroupUser.setUserId(Integer.parseInt(id));
            imChatGroupUserService.save(imChatGroupUser);
        }
        return R.ok(maxId,"聊天组创建成功");
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

        List<ImChatGroupVo> imChatGroups = new ArrayList<>();

        //客户端群组信息
        if(byId.getDeptId().equals(3)){
            List<ImChatGroupVo> chatGroups = imUserService.getChatGroups(userId.toString());
            chatGroups.stream().map(x->{
                ImMessage message = baseMapper.getMessageByChatId(x.getId());
                x.setMessage(message);
                return x;
            }).collect(Collectors.toList());
            imChatGroups.addAll(chatGroups);
        }

        //员工的群组信息
        if(byId.getDeptId().equals(5)){
            List<ImChatGroupVo> chatGroups = imUserService.getChatGroups(userId.toString());
            chatGroups.stream().map(x->{
                ImMessage message = baseMapper.getMessageByChatId(x.getId());
                x.setMessage(message);
                return x;
            }).collect(Collectors.toList());
            imChatGroups.addAll(chatGroups);
        }

        //个体户的群组信息
        if(byId.getDeptId().equals(6)){
            List<ImChatGroupVo> chatGroups = imUserService.getChatGroups(userId.toString());
            chatGroups.stream().map(x->{
                ImMessage message = baseMapper.getMessageByChatId(x.getId());
                x.setMessage(message);
                return x;
            }).collect(Collectors.toList());
            imChatGroups.addAll(chatGroups);
        }

        //经理的群组信息
        if(byId.getDeptId().equals(4)){

            List<ImChatGroupVo> chatGroups = imUserService.getChatGroups(userId.toString());
            chatGroups.stream().map(x->{
                ImMessage message = baseMapper.getMessageByChatId(x.getId());
                x.setMessage(message);
                return x;
            }).collect(Collectors.toList());
            imChatGroups.addAll(chatGroups);
            /*Integer managerId = baseMapper.getMangerId(Integer.parseInt(userId));
            List<Integer> empIds = baseMapper.getAllEmp(managerId);
            List<String> userIds = new ArrayList<>();
            for (int i = 0; i < empIds.size(); i++) {
                userIds.add(baseMapper.getUSerIdByEmpId(empIds.get(i).toString()).toString());
            }
            for (int i = 0; i < userIds.size(); i++) {
                List<ImChatGroupVo> chatGroups1 = imUserService.getChatGroups(userIds.get(i).toString());
                chatGroups1.stream().map(x->{
                    ImMessage message = baseMapper.getMessageByChatId(x.getId());
                    x.setMessage(message);
                    return x;
                }).collect(Collectors.toList());
                imChatGroups.addAll(chatGroups1);
            }
            imChatGroups.addAll(chatGroups);*/
        }

        //公司的群组信息
        if(byId.getDeptId().equals(2)){
            Integer companyId = baseMapper.getCompanyIdByUser(Integer.parseInt(userId));
            List<ImChatGroupVo> chatGroups =  baseMapper.getAllGroupByCompany(companyId);
            chatGroups.stream().map(x->{
                ImMessage message = baseMapper.getMessageByChatId(x.getId());
                x.setMessage(message);
                return x;
            }).collect(Collectors.toList());
            imChatGroups.addAll(chatGroups);
        }
        objectMap.put("groups", imChatGroups);
        return R.ok(objectMap);
    }

    @Override
    public R createGroupByCus(String demandId, String empId) {

        Integer cusId = baseMapper.getCusIdByDemand(demandId);
        Integer userId = baseMapper.getUSerIdByEmpId(empId);

        List<Integer> groupIds = baseMapper.getAllGroupId();
        for (int i = 0; i < groupIds.size(); i++) {
            QueryWrapper<ImChatGroupUser> qw = new QueryWrapper<>();
            qw.eq("chat_group_id",groupIds.get(i));
            qw.orderByAsc("user_id");
            List<ImChatGroupUser> list = imChatGroupUserService.getBaseMapper().selectList(qw);
            StringBuilder sb = new StringBuilder();
            for (int i1 = 0; i1 < list.size(); i1++) {
                sb.append(list.get(i1).getUserId()).append(",");
            }
            if(sb.toString().contains(userId.toString())&&sb.toString().contains(cusId.toString())){
                return R.ok(groupIds.get(i));
            }
        }

        CustomerDetails customer = baseMapper.getCustomerByUser(cusId.toString());
        EmployeesDetails employees = baseMapper.getEmployeesByUser(empId);
        ImChatGroup imChatGroup = new ImChatGroup();
        imChatGroup.setCustomerName(customer.getName());
        imChatGroup.setAvatarCustomer(customer.getHeadUrl());
        imChatGroup.setEmployeesName(employees.getName());
        imChatGroup.setAvatarEmployees(employees.getHeadUrl());
        imChatGroup.setName("临时群聊"+ CommonUtils.getRandomSixCode());
        imChatGroup.setCreateDate(LocalDateTime.now());
        Integer companyId = baseMapper.getCompanyId(Integer.parseInt(empId));
        imChatGroup.setCompanyId(companyId);
        imChatGroupService.save(imChatGroup);

        Integer companyId1 = baseMapper.getCompanyId(Integer.parseInt(empId));
        Integer userId1 = baseMapper.getUserIdByCom(companyId1);

        List<Integer> strings = new ArrayList<>();

        //群聊加入客户
        strings.add(cusId);
        //群聊加入保洁员
        strings.add(userId);
        //群聊添加公司账户
        strings.add(userId1);
        //群聊加入经理
        List<Integer> groups =  baseMapper.getGroupsById(Integer.parseInt(empId));
        if(!CollectionUtils.isEmpty(groups)){
            for (int i = 0; i < groups.size(); i++) {
                List<Integer> manIds = baseMapper.getMansByGroupId(groups.get(i));
                for (int j = 0; j < manIds.size(); j++) {
                    strings.add(baseMapper.getUserId(manIds.get(j)));
                }
            }
        }

        Integer maxId = ((ImChatGroup) CommonUtils.getMaxId("im_chat_group", imChatGroupService)).getId();
        for (Integer string : strings) {
            ImChatGroupUser imChatGroupUser = new ImChatGroupUser();
            imChatGroupUser.setChatGroupId(maxId);
            imChatGroupUser.setCreateDate(LocalDateTime.now());
            imChatGroupUser.setUserId(string);
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

    @Override
    public R removeGroup(Integer id) {
        ImChatGroup byId = imChatGroupService.getById(id);
        byId.setDelFlag("1");
        imChatGroupService.updateById(byId);
        return R.ok("删除成功");
    }

    @Override
    public R getChatGroupById(Integer empId) {
        ArrayList<ImChatGroupVo> imChatGroups = new ArrayList<>();
        Integer userId = baseMapper.getUSerIdByEmpId(empId.toString());
        List<ImChatGroupVo> chatGroups = imUserService.getChatGroups(userId.toString());
        chatGroups.stream().map(x->{
            ImMessage message = baseMapper.getMessageByChatId(x.getId());
            x.setMessage(message);
            return x;
        }).collect(Collectors.toList());
        imChatGroups.addAll(chatGroups);
        return R.ok(imChatGroups);
    }

    @Override
    public R getManChat(Integer manId) {

        List<ImChatGroupVo> imChatGroups = new ArrayList<>();
        Integer userId = baseMapper.getUserId(manId);
        List<ImChatGroupVo> chatGroups = imUserService.getChatGroups(userId.toString());
        chatGroups.stream().map(x->{
            ImMessage message = baseMapper.getMessageByChatId(x.getId());
            x.setMessage(message);
            return x;
        }).collect(Collectors.toList());
        imChatGroups.addAll(chatGroups);
        /*Integer managerId = baseMapper.getMangerId(userId);
        List<Integer> empIds = baseMapper.getAllEmp(managerId);
        List<String> userIds = new ArrayList<>();
        for (int i = 0; i < empIds.size(); i++) {
            userIds.add(baseMapper.getUSerIdByEmpId(empIds.get(i).toString()).toString());
        }
        for (int i = 0; i < userIds.size(); i++) {
            List<ImChatGroupVo> chatGroups1 = imUserService.getChatGroups(userIds.get(i).toString());
            chatGroups1.stream().map(x->{
                ImMessage message = baseMapper.getMessageByChatId(x.getId());
                x.setMessage(message);
                return x;
            }).collect(Collectors.toList());
            imChatGroups.addAll(chatGroups1);
        }
        imChatGroups.addAll(chatGroups);*/

        return R.ok(imChatGroups);
    }

    @Override
    public R addGroupByCom(String empId, String cusId) {
        //员工userId
        Integer empUserId = baseMapper.getEmpId(Integer.parseInt(empId));

        //客户userId
        String cusUserId = baseMapper.getCustomerId(cusId).toString();

        String check;
        if(empUserId<Integer.parseInt(cusUserId)){
            check = new StringBuilder().append(empUserId).append(",").append(cusUserId).append(",").toString();
        }else {
            check = new StringBuilder().append(cusUserId).append(",").append(empUserId).append(",").toString();
        }

        List<Integer> groupIds = baseMapper.getAllGroupId();
        for (int i = 0; i < groupIds.size(); i++) {
            QueryWrapper<ImChatGroupUser> qw = new QueryWrapper<>();
            qw.eq("chat_group_id",groupIds.get(i));
            qw.orderByAsc("user_id");
            List<ImChatGroupUser> list = imChatGroupUserService.getBaseMapper().selectList(qw);
            StringBuilder sb = new StringBuilder();
            for (int i1 = 0; i1 < list.size(); i1++) {
                sb.append(list.get(i1).getUserId()).append(",");
            }
            if(sb.toString().contains(empUserId.toString())&&sb.toString().contains(cusUserId)){
                return R.ok(groupIds.get(i));
            }
        }
        CustomerDetails customer = baseMapper.getCustomerByUser(cusUserId);
        EmployeesDetails employeesByUser = baseMapper.getEmployeesByUser(empUserId.toString());
        ImChatGroup imChatGroup = new ImChatGroup();
        imChatGroup.setEmployeesName(employeesByUser.getName());
        imChatGroup.setAvatarEmployees(employeesByUser.getHeadUrl());
        imChatGroup.setCustomerName(customer.getName());
        imChatGroup.setAvatarCustomer(customer.getHeadUrl());
        imChatGroup.setName("临时群聊"+ CommonUtils.getRandomSixCode());
        imChatGroup.setCreateDate(LocalDateTime.now());

        Integer userId = null;
        Integer companyId = baseMapper.getCompanyId(Integer.parseInt(empId));
        if(companyId!=null){
            userId = baseMapper.getUserIdByCom(companyId);
            imChatGroup.setCompanyId(companyId);
        };
        imChatGroupService.save(imChatGroup);

        Integer maxId = ((ImChatGroup) CommonUtils.getMaxId("im_chat_group", imChatGroupService)).getId();
        Set<String> ids = new HashSet<>();

        //群聊添加客户
        ids.add(cusUserId);

        //群聊加入保洁员
        ids.add(empUserId.toString());

        //群聊添加公司账户
        if(companyId!=null){
            ids.add(userId.toString());
        }

        //群聊加入经理
        List<Integer> groups =  baseMapper.getGroupsById(Integer.parseInt(empId));
        if(!CollectionUtils.isEmpty(groups)){
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
            imChatGroupUser.setUserId(Integer.parseInt(id));
            imChatGroupUserService.save(imChatGroupUser);
        }
        return R.ok(maxId,"聊天组创建成功");
    }

}
