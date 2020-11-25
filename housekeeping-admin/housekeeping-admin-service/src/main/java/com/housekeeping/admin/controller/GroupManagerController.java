package com.housekeeping.admin.controller;

import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author su
 * @create 2020/11/25 14:58
 */
@Api(value="分組controller",tags={"【公司】分組经理接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/groupManager")
public class GroupManagerController {
}
