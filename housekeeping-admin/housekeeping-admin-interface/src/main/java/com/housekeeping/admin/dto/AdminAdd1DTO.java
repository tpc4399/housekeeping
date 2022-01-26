package com.housekeeping.admin.dto;

import lombok.Data;

/**
 * 添加管理员、公司、家庭账户的DTO
 * @Author su
 * @Date 2021/2/7 17:34
 */
@Data
public class AdminAdd1DTO {

    private Integer deptId;         /* 部门_id */
    private String name;            /* 用户姓名 */
    private String nickName;        /* 用户昵称 */
    private String phonePrefix;     /* 区号 */
    private String phone;           /* 电话号码 */
    private String email;           /* 邮箱(选填)*/
    private String password;        /* 密码 */

}
