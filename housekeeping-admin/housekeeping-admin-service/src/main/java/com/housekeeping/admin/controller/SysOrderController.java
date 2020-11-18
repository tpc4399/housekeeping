package com.housekeeping.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.housekeeping.admin.dto.SysOrderDTO;
import com.housekeeping.admin.entity.SysOrder;
import com.housekeeping.admin.service.ISysOrderService;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author su
 * @create 2020/11/17 21:22
 */
@Api(value="订单管理controller",tags={"【订单】订单管理接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/sysOrder")
public class SysOrderController {

    private final ISysOrderService sysOrderService;

    @GetMapping("pageOfSysOrder")
    public R page(IPage<SysOrder> page, SysOrderDTO sysOrderDTO){
        return sysOrderService.page(page, sysOrderDTO);
    }

}
