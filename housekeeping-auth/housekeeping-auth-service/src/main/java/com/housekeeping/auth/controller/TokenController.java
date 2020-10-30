package com.housekeeping.auth.controller;

import com.housekeeping.auth.service.ITokenService;
import com.housekeeping.common.entity.HkUser;
import com.housekeeping.common.utils.R;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author su
 * @create 2020/10/27 2:51
 */
@RestController
@AllArgsConstructor
@RequestMapping("/token")
public class TokenController {
    private final ITokenService tokenService;

    @PostMapping("/getToken")
    public R getToken(@RequestBody HkUser hkUser){
        return R.ok(tokenService.getToken(hkUser));
    }

}
