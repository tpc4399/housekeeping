package com.housekeeping.auth.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author su
 * @create 2020/10/28 18:21
 */
public interface AuthApi {

    /**
     * 用於用戶驗證時，查詢用戶存在性
     * @param email
     * @return
     */
    @GetMapping("/hkUser/byEmail")
    public Object getUserByEmail(@RequestParam("email") String email, @RequestParam("deptId") String deptId);

    /**
     * 用於用戶驗證時，查詢用戶存在性
     * @param phone
     * @return
     */
    @GetMapping("/hkUser/byPhone")
    public Object getUserByPhone(@RequestParam("phonePrefix") String phonePrefix,
                                 @RequestParam("phone") String phone,
                                 @RequestParam("deptId") String deptId);

}
