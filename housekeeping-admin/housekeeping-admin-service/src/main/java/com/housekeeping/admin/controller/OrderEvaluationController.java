package com.housekeeping.admin.controller;

import com.housekeeping.admin.dto.OrderEvaluationDTO;
import com.housekeeping.admin.service.IOrderEvaluationService;
import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author su
 * @create 2021/6/7 9:26
 */
@Api(tags={"【订单双向评价】接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/orderEvaluation")
public class OrderEvaluationController {

    private final IOrderEvaluationService orderEvaluationService;

    @ApiOperation("【保洁员】【客户】评价某个订单")
    @Access({RolesEnum.USER_EMPLOYEES, RolesEnum.USER_CUSTOMER})
    @PostMapping
    public R evaluation(@RequestBody OrderEvaluationDTO dto){
        return R.ok();
    }

}
