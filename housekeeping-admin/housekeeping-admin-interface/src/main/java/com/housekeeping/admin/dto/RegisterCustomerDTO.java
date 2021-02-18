package com.housekeeping.admin.dto;

import lombok.Data;

/**
 * @Author su
 * @Date 2021/2/18 11:09
 */
@Data
public class RegisterCustomerDTO {

    private String name;            /* 公司賬戶名稱 */
    private String phonePrefix;     /* 区号 */
    private String phone;           /* 电话号码 */
    private String code;            /* 验证码 */
    private String password;        /* 密码 */
    private String rePassword;      /* 确认密码 */
    private String address;         /* 客戶地址 */
    private String lat;             /* 经度 */
    private String lng;             /* 纬度 */

}
