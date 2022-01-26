package com.housekeeping.admin.dto;

import lombok.Data;

/**
 * @Author su
 * @Date 2021/2/18 11:08
 */
@Data
public class RegisterAdminDTO {

    private String name;            /* 管理員姓名 */
    private String phonePrefix;     /* 区号 */
    private String phone;           /* 电话号码 */
    private String code;            /* 验证码 */
    private String password;        /* 密码 */
    private String rePassword;      /* 确认密码 */

}
