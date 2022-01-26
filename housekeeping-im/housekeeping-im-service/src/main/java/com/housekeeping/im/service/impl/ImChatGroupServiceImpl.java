package com.housekeeping.im.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.im.entity.ImChatGroup;
import com.housekeeping.im.mapper.ImChatGroupMapper;
import com.housekeeping.im.service.IImChatGroupService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("imChatGroupServiceImpl")
public class ImChatGroupServiceImpl extends ServiceImpl<ImChatGroupMapper, ImChatGroup> implements IImChatGroupService {

}
