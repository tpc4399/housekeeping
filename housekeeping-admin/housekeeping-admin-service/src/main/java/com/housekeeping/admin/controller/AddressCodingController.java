package com.housekeeping.admin.controller;

import com.housekeeping.admin.service.IAddressCodingService;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author su
 * @Date 2021/1/12 16:30
 */
@Api(tags={"【地址编码】接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/addressCoding")
public class AddressCodingController {

    private final IAddressCodingService addressCodingService;

    @ApiOperation("地址编码")
    @GetMapping
    public R addressCoding(String address){
        return addressCodingService.addressCoding(address);
    }

}
