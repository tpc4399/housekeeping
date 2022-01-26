package com.housekeeping.admin.vo;

import lombok.Data;

@Data
public class SmsDTO {

    private String dept;
    private String phone_prefix;
    private String phone;
    private String code;
}
