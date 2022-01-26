package com.housekeeping.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author su
 * @Date 2021/3/1 12:33
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminUpdate2DTO {

    private Integer id;             /* 账户的user_id */
    private Integer deptId;         /* 部门_id 不可变，但是要传 */
    private String name;            /* 用户姓名 */
    private String nickName;        /* 用户昵称 */
    private String phonePrefix;     /* 区号(选填) */
    private String phone;           /* 电话号码(选填) */
    private String email;           /* 邮箱(选填)*/
    private Integer companyId;      /* 所属公司_id 不可变，但是要传 */

}
