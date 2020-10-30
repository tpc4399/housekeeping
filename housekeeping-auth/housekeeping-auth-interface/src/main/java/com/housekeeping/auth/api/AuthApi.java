package com.housekeeping.auth.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author su
 * @create 2020/10/28 18:21
 */
public interface AuthApi {

    @GetMapping("/security/encryption")
    public String getEncryption(@RequestParam String str);

    @GetMapping("/security/decryption")
    public String getDecryption(@RequestParam String str);

    @GetMapping("/hkUser/byEmail")
    public Object getUserByEmail(@RequestParam String email);


    @GetMapping("/hkUser/byPhone")
    public Object getUserByPhone(@RequestParam String phone);

}
