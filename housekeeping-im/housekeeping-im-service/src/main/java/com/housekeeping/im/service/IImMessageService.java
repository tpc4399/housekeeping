package com.housekeeping.im.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.im.entity.ImMessage;

import java.util.List;

public interface IImMessageService extends IService<ImMessage> {

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

}
