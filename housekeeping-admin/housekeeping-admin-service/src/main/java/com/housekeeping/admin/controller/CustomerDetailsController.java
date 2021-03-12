package com.housekeeping.admin.controller;

import com.housekeeping.admin.service.ICustomerDetailsService;
import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @Author su
 * @create 2020/11/23 11:28
 */
@Api(value="客戶controller",tags={"【客户详情】接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/customerDetails")
public class CustomerDetailsController {

    private final ICustomerDetailsService customerDetailsService;

    @Access({RolesEnum.USER_CUSTOMER})
    @ApiOperation("【客户】设置为默认地址")
    @PutMapping("/toDefault")
    public R toDefault(Integer id){
        return customerDetailsService.toDefault(id);
    }

    @Access({RolesEnum.USER_CUSTOMER})
    @ApiOperation("【客户】上传头像")
    @PostMapping("/uploadHead")
    public R uploadHead(@RequestParam("file") MultipartFile file) throws IOException {
        Integer reviserId = TokenUtils.getCurrentUserId();
        //服务器存储head
        String fileName = customerDetailsService.uploadHead(file, reviserId);
        //数据库存储headUrl
        customerDetailsService.updateHeadUrlByUserId(fileName, reviserId);
        return R.ok("頭像保存成功");
    }

    @Access({RolesEnum.USER_COMPANY})
    @ApiOperation("【公司】获取客户列表")
    @GetMapping("/getCustomerList")
    public R getCustomerList(Integer cid,String name){
        return customerDetailsService.getCustomerList(cid,name);
    }

}
