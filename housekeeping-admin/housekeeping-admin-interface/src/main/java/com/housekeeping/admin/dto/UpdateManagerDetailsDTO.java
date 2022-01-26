package com.housekeeping.admin.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @Author su
 * @create 2021/5/27 11:52
 */
@Data
public class UpdateManagerDetailsDTO {

    private Integer id;    /* 主键 */
    private String name;    /* 經理姓名 */
    private LocalDate dateOfBirth;    /* 經理生日 */
    private String phonePrefix;     /* 手机区号 */
    private String phone;    /* 手機號 */
    private String address;    /* 地區 */
    private String describes;    /* 描述 */
    private Boolean sex;    /* 性別 */
    private String educationBackground; /* 学历 */
    private String headUrl;   /* 头像地址 */
    private List<Integer> roles;    /* 经理权限 */

}
