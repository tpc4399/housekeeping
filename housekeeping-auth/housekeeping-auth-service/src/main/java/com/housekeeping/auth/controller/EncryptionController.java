package com.housekeeping.auth.controller;

import com.housekeeping.auth.utils.DESEncryption;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author su
 * @create 2020/10/28 18:17
 */
@RestController
@AllArgsConstructor
@RequestMapping("/security")
public class EncryptionController {

    @GetMapping("/encryption")
    public String getEncryption(String str){
        return DESEncryption.getEncryptString(str);
    }

    @GetMapping("/decryption")
    public String getDecryption(String str){
        return DESEncryption.getDecryptString(str);
    }

}
