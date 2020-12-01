package com.housekeeping.admin.controller;

import com.housekeeping.admin.service.ICustomerDetailsService;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @Author su
 * @create 2020/11/23 11:28
 */
@Api(value="客戶controller",tags={"【客戶】客户详情接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/customerDetails")
public class CustomerDetailsController {

    private final ICustomerDetailsService customerDetailsService;

    @ApiOperation("【客户】设置为默认地址")
    @PutMapping("/toDefault")
    public R toDefault(Integer id){
        return customerDetailsService.toDefault(id);
    }

}
