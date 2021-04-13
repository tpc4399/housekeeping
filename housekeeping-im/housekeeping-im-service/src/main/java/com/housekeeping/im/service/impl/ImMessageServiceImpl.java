package com.housekeeping.im.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.PageUtils;
import com.housekeeping.common.utils.R;
import com.housekeeping.im.common.utils.ChatUtils;
import com.housekeeping.im.entity.ImMessage;
import com.housekeeping.im.entity.ImUser;
import com.housekeeping.im.entity.Message;
import com.housekeeping.im.mapper.ImMessageMapper;
import com.housekeeping.im.service.IImMessageService;
import com.housekeeping.im.service.IImUserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
@Qualifier(value = "iImMessageService")
public class ImMessageServiceImpl extends ServiceImpl<ImMessageMapper, ImMessage> implements IImMessageService {

    public static final int PAGE_SIZE = 20;

    public static final String FRIEND = "0";

    @Resource
    @Qualifier(value = "imUserService")
    private IImUserService imUserService;


    @Override
    public R listMessage(String chatId, String fromId, String chatType,Page page) {
        if (CommonUtils.isEmpty(chatId) || StringUtils.isEmpty(fromId)) {
            return R.ok(null);
        }
        QueryWrapper<ImMessage> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("id");
        System.out.println(FRIEND);
        if(FRIEND.equals(chatType)){
            wrapper.and(wrapper1 -> wrapper1.eq("to_id", chatId)
                    .eq("from_id", fromId));
            wrapper.or(wrapper2 -> wrapper2.eq("from_id", chatId)
                    .eq("to_id", fromId));
        }else {
            wrapper.eq("to_id",chatId);
        }
        IPage<ImMessage> messageIPage = this.page(page, wrapper);

        List<ImMessage> imMessageList = messageIPage.getRecords();
        List<Message> messageList = new ArrayList<>();
        for (ImMessage imMessage : imMessageList) {
            Message message = new Message();
            message.setId(imMessage.getToId());
            message.setMine(fromId.equals(imMessage.getFromId()));
            message.setType(imMessage.getType());

            ImUser imUser = imUserService.getById(imMessage.getFromId());
            message.setUsername(imUser.getName());

            String headUrl = imUserService.getHeadUrl(imUser.getId(), imUser.getDeptId());
            message.setAvatar(headUrl);

            message.setFromid(imMessage.getFromId());
            message.setCid(String.valueOf(imMessage.getId()));
            message.setContent(imMessage.getContent());
            message.setTimestamp(new Date().getTime());
            message.setMsgtype(imMessage.getMsgType());
            messageList.add(message);
        }
        Page pages = PageUtils.getPages((int)page.getCurrent(), (int)page.getSize(), messageList);
        return R.ok(pages);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveMessage(ImMessage imMessage) {
        new SaveChatMessageThread(imMessage).run();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ImMessage> getUnReadMessage(String toId) {
        QueryWrapper<ImMessage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("to_id", toId);
        queryWrapper.eq("read_status", "1");
        List<ImMessage> messageList = baseMapper.selectList(queryWrapper);
        for (ImMessage message : messageList) {
            message.setReadStatus(ChatUtils.READED);
            this.updateById(message);
        }
        return messageList;
    }

    @Override
    public R getAllMessage(Page page) {
        List<ImMessage> imMessages = new ArrayList<>();
        List<Integer> chatIds = baseMapper.getAllChatIds();
        for (int i = 0; i < chatIds.size(); i++) {
            QueryWrapper<ImMessage> qw = new QueryWrapper<>();
            qw.eq("to_id",chatIds.get(i));
            qw.orderByDesc("send_time");
            imMessages.addAll(this.list(qw));
        }

        List<Message> messageList = new ArrayList<>();
        for (ImMessage imMessage : imMessages) {
            Message message = new Message();
            message.setId(imMessage.getToId());
            message.setMine(false);
            message.setType(imMessage.getType());

            ImUser imUser = imUserService.getById(imMessage.getFromId());
            message.setUsername(imUser.getName());

            String headUrl = imUserService.getHeadUrl(imUser.getId(), imUser.getDeptId());
            message.setAvatar(headUrl);

            message.setFromid(imMessage.getFromId());
            message.setCid(String.valueOf(imMessage.getId()));
            message.setContent(imMessage.getContent());
            message.setTimestamp(new Date().getTime());
            message.setMsgtype(imMessage.getMsgType());
            messageList.add(message);
        }
        Page pages = PageUtils.getPages((int)page.getCurrent(), (int)page.getSize(), messageList);
        return R.ok(pages);
    }


    /**
     * 内部类
     */
    class SaveChatMessageThread implements Runnable {

        private ImMessage imMessage;

        public SaveChatMessageThread(ImMessage imMessage) {
            this.imMessage = imMessage;
        }

        @Override
        public void run() {
            save(imMessage);
        }
    }
}
