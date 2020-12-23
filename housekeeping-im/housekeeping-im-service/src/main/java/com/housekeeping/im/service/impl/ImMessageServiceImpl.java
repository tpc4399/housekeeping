package com.housekeeping.im.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
    public R listMessage(String chatId, String fromId, String chatType, Long pageNo) {
        if (StringUtils.isEmpty(chatId) || StringUtils.isEmpty(fromId)) {
            return R.ok(null);
        }
        Page<ImMessage> page = new Page<>();
        page.setSize(PAGE_SIZE);
        if (pageNo == null) {
            pageNo = 0L;
        }
        page.setCurrent(pageNo);
        page.setDesc("send_time");
        QueryWrapper<ImMessage> wrapper = new QueryWrapper<>();
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
            message.setFromid(imMessage.getFromId());
            message.setCid(String.valueOf(imMessage.getId()));
            message.setContent(imMessage.getContent());
            message.setTimestamp(new Date().getTime());
            messageList.add(message);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("messageList", messageList);
        map.put("pageNo", pageNo);
        map.put("count", messageIPage.getTotal());
        map.put("pageSize", messageIPage.getSize());
        return R.ok(map);
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
