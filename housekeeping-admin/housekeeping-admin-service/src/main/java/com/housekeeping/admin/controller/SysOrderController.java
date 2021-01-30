package com.housekeeping.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.housekeeping.admin.dto.SysOrderDTO;
import com.housekeeping.admin.entity.SysOrder;
import com.housekeeping.admin.service.ISysOrderService;
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

//    @ApiOperation("【公司】【经理】分頁查詢訂單")
//    @GetMapping("pageOfSysOrder")
//    public R page(IPage<SysOrder> page, SysOrderDTO sysOrderDTO){
//        return sysOrderService.page(page, sysOrderDTO);
//    }
//
//
//    @ApiOperation("【客户】评价发布接口")
//    @PostMapping(value = "/doEvaluation", headers = "content-type=multipart/form-data")
//    public R doEvaluation(@RequestParam(value = "file", required = false) MultipartFile[] file,
//                          @RequestParam("evaluationStar") Float evaluationStar,
//                          @RequestParam("evaluationContent") String evaluationContent,
//                          @RequestParam("orderId") Integer orderId){
//
//        return sysOrderService.doEvaluation(file, evaluationStar, evaluationContent, orderId);
//    }

}
