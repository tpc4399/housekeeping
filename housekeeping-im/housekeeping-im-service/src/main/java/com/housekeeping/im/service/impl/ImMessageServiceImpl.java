package com.housekeeping.im.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.common.utils.CommonUtils;
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
            message.setFromid(imMessage.getFromId());
            message.setCid(String.valueOf(imMessage.getId()));
            message.setContent(imMessage.getContent());
            message.setTimestamp(new Date().getTime());
            messageList.add(message);
        }
        Page pages = getPages((int)page.getCurrent(), (int)page.getSize(), messageList);
        return R.ok(pages);
    }

    private Page getPages(Integer currentPage, Integer pageSize, List list) {
        Page page = new Page();
        int size = list.size();

        if(pageSize > size) {
            pageSize = size;
        }

        // 求出最大页数，防止currentPage越界
        int maxPage = size % pageSize == 0 ? size / pageSize : size / pageSize + 1;

        if(currentPage > maxPage) {
            currentPage = maxPage;
        }

        // 当前页第一条数据的下标
        int curIdx = currentPage > 1 ? (currentPage - 1) * pageSize : 0;

        List pageList = new ArrayList();

        // 将当前页的数据放进pageList
        for(int i = 0; i < pageSize && curIdx + i < size; i++) {
            pageList.add(list.get(curIdx + i));
        }

        page.setCurrent(currentPage).setSize(pageSize).setTotal(list.size()).setRecords(pageList);
        return page;
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
