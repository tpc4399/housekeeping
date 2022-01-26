package com.housekeeping.im.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.housekeeping.common.utils.R;
import com.housekeeping.im.entity.ImChatGroup;
import com.housekeeping.im.entity.ImChatGroupVo;
import com.housekeeping.im.entity.ImUser;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface IImUserService extends IService<ImUser> {

    /**
     * 根据用户id 获取用户所有的群
     * @param userId 用户
     * @return 群List
     */
    List<ImChatGroupVo> getChatGroups(String userId);

    /**
     * 获取群组的用户
     * @param chatId 群组id
     * @return 用户List
     */
    R getChatUserList(String chatId);

    R createGroup(String toId);

    R sendMsg(String userId, String msg, HttpServletRequest request) throws JsonProcessingException;

    R init();

    R createGroupByCus(String demandId, String empId);

    public String getHeadUrl(Integer userId,Integer deptId);

    R removeGroup(Integer id);

    R getChatGroupById(Integer empId);

    R getManChat(Integer manId);

    R addGroupByCom(String empId, String cusId);
}
