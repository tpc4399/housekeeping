package com.housekeeping.auth.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author su
 * @create 2020/10/28 18:21
 */
@RequestMapping("security")
public interface EncryptionApi {

    @GetMapping("encryption")
    public String getEncryption(@RequestParam String str);

    @GetMapping("decryption")
    public String getDecryption(@RequestParam String str);

}
