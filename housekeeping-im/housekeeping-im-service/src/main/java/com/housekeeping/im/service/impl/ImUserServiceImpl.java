package com.housekeeping.im.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.im.entity.ImChatGroup;
import com.housekeeping.im.entity.ImUser;
import com.housekeeping.im.mapper.ImUserMapper;
import com.housekeeping.im.service.IImChatGroupUserService;
import com.housekeeping.im.service.IImUserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Qualifier(value = "imUserService")
public class ImUserServiceImpl extends ServiceImpl<ImUserMapper, ImUser> implements IImUserService {


    @Resource
    @Qualifier(value = "imChatGroupUserService")
    private IImChatGroupUserService imChatGroupUserService;


    @Override
    public List<ImChatGroup> getChatGroups(String userId) {
        return baseMapper.getUserGroups(userId);
    }

    @Override
    public List<ImUser> getChatUserList(String chatId) {
        return baseMapper.getChatUserList(chatId);
    }


}
