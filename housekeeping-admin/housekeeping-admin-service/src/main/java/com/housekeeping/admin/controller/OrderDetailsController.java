package com.housekeeping.admin.controller;

import com.housekeeping.admin.dto.RequestToChangeAddressDTO;
import com.housekeeping.admin.service.IOrderDetailsService;
import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @Author su
 * @Date 2021/4/22 11:10
 */
@Api(tags={"【确认订单】接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/orderDetails")
public class OrderDetailsController {

    @Resource
    private IOrderDetailsService orderDetailsService;

    @ApiOperation("【客户】确认订单——请求修改服务地址(需要保洁员同意才能修改成功)")
    @Access(RolesEnum.USER_CUSTOMER)
    @PostMapping("/requestToChangeAddress")
    public R requestToChangeAddress(RequestToChangeAddressDTO dto){
        return R.ok();
    }

    @ApiOperation("【客户】确认订单界面——支付操作需要调用")
    @Access(RolesEnum.USER_CUSTOMER)
    @PutMapping(value = "/pay", headers = "content-type=multipart/form-data")
    public R pay(@RequestParam("number") Long number,
                 @RequestParam("employeesId") Integer employeesId,
                 @RequestParam("photos") MultipartFile[] photos,
                 @RequestParam("evaluates") String[] evaluates,
                 @RequestParam("payType") String payType,
                 @RequestParam("remarks") String remarks) throws Exception {
        orderDetailsService.pay(number, employeesId, photos, evaluates, payType, remarks);
        return R.ok(null, "上传成功");
    }

}
