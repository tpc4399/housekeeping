package com.housekeeping.admin.controller;

import com.housekeeping.admin.dto.KeyWorkReturnDTO;
import com.housekeeping.admin.service.IOrderPhotosService;
import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author su
 * @create 2021/6/11 15:32
 */
@Api(tags={"【订单工作重点】相关接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/orderPhotos")
public class OrderPhotosController {

    @Resource
    private IOrderPhotosService orderPhotosService;

    @ApiOperation("【保洁员】获取订单的工作重点，以及回传信息")
    @Access({RolesEnum.USER_EMPLOYEES})
    @GetMapping
    private R getByOrderNumber(String orderNumber){
        return orderPhotosService.getByOrderNumber(orderNumber);
    }

    @ApiOperation("【保洁员】对订单进行工作重点回传")
    @Access({RolesEnum.USER_EMPLOYEES})
    @PostMapping
    private R keyWorkReturn(@RequestBody List<KeyWorkReturnDTO> dto){
        return orderPhotosService.keyWorkReturn(dto);
    }

}
