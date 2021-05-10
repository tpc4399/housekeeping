package com.housekeeping.admin.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * @Author su
 * @create 2020/11/20 17:39
 */
@Data
public class ManagerDetailsDTO {

    private Integer id;    /* 主键 */
    private String name;    /* 經理姓名 */
    private LocalDate dateOfBirth;    /* 經理生日 */
    private String phonePrefix;     /* 手机区号 */
    private String phone;    /* 手機號 */
    private String address;    /* 地區 */
    private String describes;    /* 描述 */
    private Boolean sex;    /* 性別 */
    private String educationBackground; /* 学历 */
    private String headerUrl;   /* 头像地址 */

}
