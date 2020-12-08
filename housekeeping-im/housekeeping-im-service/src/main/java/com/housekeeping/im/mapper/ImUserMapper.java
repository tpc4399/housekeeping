package com.housekeeping.im.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.housekeeping.im.entity.ImChatGroup;
import com.housekeeping.im.entity.ImUser;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Qualifier("imUserMapper")
public interface ImUserMapper extends BaseMapper<ImUser> {


    /**
     * 根据用户id 获取群组
     * @param userId id
     * @return List<ImGroup>
     */
    List<ImChatGroup> getUserGroups(String userId);


    /**
     * 获取群组的用户
     * @param chatId 群组id
     * @return 用户List
     */
    List<ImUser> getChatUserList(String chatId);
}
