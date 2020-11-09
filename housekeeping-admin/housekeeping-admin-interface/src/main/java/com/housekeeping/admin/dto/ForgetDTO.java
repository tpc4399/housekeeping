package com.housekeeping.admin.dto;

import lombok.Data;

@Data
public class ForgetDTO {

    private String phonePrefix;

    private String phone;

    private Integer deptId;

    private String code;

    private String password;

    private String rePassword;
}
