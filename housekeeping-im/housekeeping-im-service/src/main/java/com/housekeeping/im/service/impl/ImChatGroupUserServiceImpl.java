package com.housekeeping.im.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.im.entity.ImChatGroupUser;
import com.housekeeping.im.mapper.ImChatGroupUserMapper;
import com.housekeeping.im.service.IImChatGroupUserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("imChatGroupUserService")
public class ImChatGroupUserServiceImpl extends ServiceImpl<ImChatGroupUserMapper, ImChatGroupUser> implements IImChatGroupUserService {

}
