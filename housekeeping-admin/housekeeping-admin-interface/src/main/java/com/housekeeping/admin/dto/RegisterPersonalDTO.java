package com.housekeeping.admin.dto;

import lombok.Data;

@Data
public class RegisterPersonalDTO {

    private String name;            /* 公司賬戶名稱 */
    private String phonePrefix;     /* 区号 */
    private String phone;           /* 电话号码 */
    private String password;        /* 密码 */
}
