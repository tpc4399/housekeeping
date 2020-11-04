package com.housekeeping.common.entity;

import lombok.Data;

/**
 * @Author su
 * @create 2020/10/26 23:22
 */
@Data
public class HkUser {
    private Integer id;
    private String nickName;
    private String phone;
    private String email;
    private String password;
    private Integer authType; //登入方式
    private Integer deptId; //登入部门
}
