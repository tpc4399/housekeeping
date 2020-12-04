package com.housekeeping.admin.dto;

import lombok.Data;


@Data
public class RegisterDTO {

    private String name;            /* 用户姓名 */
    private String phonePrefix;     /* 区号 */
    private String phone;           /* 电话号码 */
    private String code;            /* 验证码 */
    private String password;        /* 密码 */
    private String repassword;      /* 确认密码 */
    private String address;         /* 客戶地址 */

}
