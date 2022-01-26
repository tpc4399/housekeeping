package com.housekeeping.admin.controller;


import com.housekeeping.admin.entity.UserWithdrawal;
import com.housekeeping.admin.service.UserWithdrawalService;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(value="提现controller",tags={"【提现】接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/admin")
public class UserWithdrawalController {

    private final UserWithdrawalService userWithdrawalService;

    @ApiOperation("用户提交提现申请")
    @PostMapping("/addWithdrawal")
    public R addWithdrawal(@RequestBody UserWithdrawal userWithdrawal){
        return userWithdrawalService.addWithdrawal(userWithdrawal);
    }

    @ApiOperation("用户取消提现申请")
    @GetMapping("/cancelWithdrawal")
    public R addWithdrawal(Integer id){
        return R.ok(userWithdrawalService.removeById(id));
    }

    @ApiOperation("管理员处理提现申请")
    @PostMapping("/handleWithdrawal")
    public R handleWithdrawal(@RequestParam Integer id,
                              @RequestParam Integer status){
        return userWithdrawalService.handleWithdrawal(id,status);
    }

    @ApiOperation("查看提现申请列表")
    @GetMapping("/getAllWithdrawal")
    public R getAllWithdrawal(Integer id,Integer userId,String  bank,String collectionAccount,Integer status){
        return userWithdrawalService.getAllWithdrawal(id, userId, bank,collectionAccount,status);
    }




}
