package com.housekeeping.im.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.im.entity.ImChatGroup;
import com.housekeeping.im.entity.ImUser;

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
    List<ImUser> getChatUserList(String chatId);

}
