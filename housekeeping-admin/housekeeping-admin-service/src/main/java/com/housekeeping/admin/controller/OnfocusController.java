package com.housekeeping.admin.controller;

import com.housekeeping.admin.dto.KeyWorkReturnDTO;
import com.housekeeping.admin.service.IOrderPhotosService;
import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author su
 * @create 2021/6/16 10:12
 */
@Api(tags={"【订单工作重点】接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/onfocus")
public class OnfocusController {

    private final IOrderPhotosService orderPhotosService;

    @ApiOperation("【保洁员】获取订单的工作重点")
    @Access({RolesEnum.USER_EMPLOYEES})
    @GetMapping("/onfocus")
    public R getByOrderNumber(String orderNumber){
        return orderPhotosService.getByOrderNumber(orderNumber);
    }

}
