package com.housekeeping.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.housekeeping.admin.dto.Action1DTO;
import com.housekeeping.admin.dto.Action2DTO;
import com.housekeeping.admin.dto.SysOrderDTO;
import com.housekeeping.admin.entity.SysOrder;
import com.housekeeping.admin.service.IOrderIdService;
import com.housekeeping.admin.service.ISysOrderService;
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
 * @create 2020/11/17 21:22
 */
@Api(value="订单管理controller",tags={"【订单】管理接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/sysOrder")
public class SysOrderController {

    private final ISysOrderService sysOrderService;
    private final IOrderIdService orderIdService;

    @Access({RolesEnum.USER_CUSTOMER})
    @ApiOperation("【客户】评价发布接口")
    @PostMapping(value = "/doEvaluation", headers = "content-type=multipart/form-data")
    public R doEvaluation(@RequestParam(value = "file", required = false) MultipartFile[] file,
                          @RequestParam("evaluationStar") Float evaluationStar,
                          @RequestParam("evaluationContent") String evaluationContent,
                          @RequestParam("orderId") Integer orderId){

        return sysOrderService.doEvaluation(file, evaluationStar, evaluationContent, orderId);
    }

    @Access(RolesEnum.SYSTEM_ADMIN)
    @ApiOperation("【管理员】获取全部订单")
    @GetMapping
    public R list(){
        return R.ok();
    }

    @Access(RolesEnum.USER_CUSTOMER)
    @ApiOperation("【客户】预约钟点工操作，然后生成订单-->待付款")
    @PostMapping("/action1")
    public R action1(@RequestBody Action1DTO dto){
        return R.ok();
    }

    @Access(RolesEnum.USER_CUSTOMER)
    @ApiOperation("【客户】预约包工操作，然后生成订单-->待付款")
    @PostMapping("/action2")
    public R action2(@RequestBody Action2DTO dto){
        return R.ok();
    }

    @ApiOperation("预约包工操作，然后生成订单-->待付款")
    @GetMapping("/test")
    public R test(){
        System.out.println(orderIdService.generateId());
        return R.ok();
    }

}
