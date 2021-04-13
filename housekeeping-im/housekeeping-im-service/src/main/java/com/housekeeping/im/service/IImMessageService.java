package com.housekeeping.im.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.common.utils.R;
import com.housekeeping.im.entity.ImMessage;

import java.util.List;
import java.util.Map;

public interface IImMessageService extends IService<ImMessage> {


    R listMessage(String chatId, String fromId, String chatType, Page page);

    /**
     * 保存消息
     *
     * @param imMessage 消息
     */
    void saveMessage(ImMessage imMessage);

    /**
     * 获取未读消息根据接收人的ID
     *
     * @param toId 接收人的Id
     */
    List<ImMessage> getUnReadMessage(String toId);


    R getAllMessage(Page page);
}
