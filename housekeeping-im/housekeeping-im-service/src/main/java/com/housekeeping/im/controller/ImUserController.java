package com.housekeeping.im.controller;



import com.housekeeping.common.utils.R;
import com.housekeeping.im.service.IImUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


@Api(tags = "用户")
@RestController
@RequestMapping("/user")
public class ImUserController {

    private final Logger logger = LoggerFactory.getLogger(ImUserController.class);

    @Resource
    @Qualifier(value = "imUserService")
    private IImUserService imUserService;

    /**
     * 用户信息初始化
     * @return json
     */
    @ApiOperation("初始化用户信息")
    @GetMapping("/init")
    public R list() {
        return imUserService.init();
    }


    /**
     * 获取群组的用户
     *
     * @param chatId 群组id
     * @return 用户List
     */
    @ApiOperation("获取群组的用户")
    @GetMapping("/chatUserList")
    public R chatUserList(@RequestParam String chatId) {
        return imUserService.getChatUserList(chatId);
    }

    /**
     * 发送信息给用户
     * 注意：目前仅支持发送给在线用户
     *
     * @param userId 接收方id
     * @param msg    消息内容
     */
    @ApiOperation("系统发送信息给用户")
    @PostMapping("/sendMsg")
    public R sendMsg(String userId, String msg, HttpServletRequest request) throws Exception {
        return imUserService.sendMsg(userId,msg,request);
    }

    @GetMapping("/addGroupByEmp")
    @ApiOperation("客户对保洁员发起群聊")
    public R createGroupByEmp(@RequestParam String toId){
        return imUserService.createGroup(toId);
    }

    @GetMapping("/addGroupByCompany")
    @ApiOperation("客户对公司发起群聊")
    public R createGroupByCompany(@RequestParam String toId){
        return imUserService.createGroupByCompany(toId);
    }

    @GetMapping("/addGroupByCus")
    @ApiOperation("公司或经理对客户发起群聊(需要匹配到的员工id)")
    public R createGroupByCus(@RequestParam String toId,
                              @RequestParam String empId){
        return imUserService.createGroupByCus(toId,empId);
    }

}
