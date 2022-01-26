package com.housekeeping.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author su
 * @Date 2021/3/1 12:18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUpdate1DTO {

    private Integer id;             /* 账户的user_id */
    private Integer deptId;         /* 部门_id 不可切换部门 平台账户无法变为公司账户但是需要传这个参数 */
    private String name;            /* 用户姓名 */
    private String nickName;        /* 用户昵称 */
    private String phonePrefix;     /* 区号 */
    private String phone;           /* 电话号码 */
    private String email;           /* 邮箱(选填)*/
    private String password;        /* 密码 */

}
