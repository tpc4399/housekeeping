package com.housekeeping.im.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.housekeeping.im.entity.ImMessage;

import java.util.List;

public interface ImMessageMapper extends BaseMapper<ImMessage> {

    List<Integer> getAllChatIds();
}
