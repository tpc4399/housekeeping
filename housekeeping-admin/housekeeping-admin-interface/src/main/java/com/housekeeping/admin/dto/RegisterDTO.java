package com.housekeeping.admin.dto;

import lombok.Data;


@Data
public class RegisterDTO {

    /* 用户姓名 */
    private String name;

    /* 区号 */
    private String phonePrefix;

    /* 电话号码 */
    private String phone;

    /* 验证码 */
    private String code;

    /* 密码 */
    private String password;

    /* 确认密码 */
    private String repassword;

}
