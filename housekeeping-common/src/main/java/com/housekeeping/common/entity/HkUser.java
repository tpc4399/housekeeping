package com.housekeeping.common.entity;

import lombok.Data;

/**
 * @Author su
 * @create 2020/10/26 23:22
 */
@Data
public class HkUser {

    /* 主键 */
    private Integer id;

    /* 昵称 */
    private String nickName;

    /* 手机号前缀 */
    private String phonePrefix;

    /* 手机号 */
    private String phone;

    /* 邮箱号 */
    private String email;

    /* 密码 */
    private String password;

    /* 登入方式：手机号+密码 手机号+验证码 邮箱+密码 */
    private Integer authType;

    /* 部门_id 系统管理员 公司 顾客 */
    private Integer deptId;
}
