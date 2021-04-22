package com.housekeeping.admin.controller;

import com.housekeeping.admin.dto.ChangePayTypeDTO;
import com.housekeeping.admin.dto.RequestToChangeAddressDTO;
import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author su
 * @Date 2021/4/22 11:10
 */
@Api(tags={"【确认订单】接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/orderDetails")
public class OrderDetailsController {

    @ApiOperation("【客户】确认订单——请求修改服务地址(需要保洁员同意才能修改成功)")
    @Access(RolesEnum.USER_CUSTOMER)
    @PostMapping("/requestToChangeAddress")
    public R requestToChangeAddress(RequestToChangeAddressDTO dto){
        return R.ok();
    }

    @ApiOperation("【客户】确认订单——上传照片")
    @Access(RolesEnum.USER_CUSTOMER)
    @PutMapping(value = "/pay", headers = "content-type=multipart/form-data")
    public R pay(@RequestParam("number") Integer number,
                 @RequestParam("photo") MultipartFile[] photo){
        return R.ok();
    }

    @ApiOperation("【客户】确认订单——修改支付方式")
    @Access(RolesEnum.USER_CUSTOMER)
    @PostMapping("/changePayType")
    public R changePayType(ChangePayTypeDTO dto){
        return R.ok();
    }

}
