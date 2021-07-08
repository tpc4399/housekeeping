package com.housekeeping.admin.controller;

import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api("生意统计")
@RestController
@AllArgsConstructor
@RequestMapping("/statistics")
public class StatisticsController {


}
