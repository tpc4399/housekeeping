package com.housekeeping.admin.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * @Author su
 * @create 2020/10/28 17:04
 */
@Data
public class UserDTO {

    /* 用户昵称 */
    private String nickname;

    /* 用户姓名 */
    private String name;

    /* 出生年月日 */
    private LocalDate dateOfBirth;

    /* 手机号前缀 */
    private String phonePrefix;

    /* 手机号 */
    private String phone;

    /* 邮箱 */
    private String email;

    /* 密码 */
    private String password;

    /* 部门_id */
    private Integer deptId;

}
