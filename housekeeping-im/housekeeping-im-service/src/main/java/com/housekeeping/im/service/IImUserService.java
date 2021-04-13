package com.housekeeping.im.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.housekeeping.common.utils.R;
import com.housekeeping.im.entity.ImChatGroup;
import com.housekeeping.im.entity.ImUser;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface IImUserService extends IService<ImUser> {

    /**
     * 根据用户id 获取用户所有的群
     * @param userId 用户
     * @return 群List
     */
    List<ImChatGroup> getChatGroups(String userId);

    /**
     * 获取群组的用户
     * @param chatId 群组id
     * @return 用户List
     */
    R getChatUserList(String chatId);

    R createGroup(String toId);

    R sendMsg(String userId, String msg, HttpServletRequest request) throws JsonProcessingException;

    R init();

    R createGroupByCompany(String toId);

    R createGroupByCus(String toId, String empId);

    public String getHeadUrl(Integer userId,Integer deptId);
}
