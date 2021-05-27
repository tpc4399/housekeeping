package com.housekeeping.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.housekeeping.admin.service.ISerialService;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author su
 * @create 2021/5/27 16:56
 */
@Api(tags={"【流水】相关接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/serial")
public class SerialController {

    private final ISerialService serialService;

    @GetMapping("/pageOfSerial")
    public R pageOfSerial(Page page){
        return serialService.pageOfSerial(page);
    }

}
