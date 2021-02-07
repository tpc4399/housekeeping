package com.housekeeping.admin.dto;

import lombok.Data;

/**
 * 添加经理、保洁员的DTO
 * @Author su
 * @Date 2021/2/7 17:36
 */
@Data
public class AdminAdd2DTO {

    private Integer deptId;         /* 部门_id */
    private String name;            /* 用户姓名 */
    private String nickName;        /* 用户昵称 */
    private String phonePrefix;     /* 区号(选填) */
    private String phone;           /* 电话号码(选填) */
    private String email;           /* 邮箱(选填)*/
    private Integer companyId;      /* 所属公司_id */

}
