package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class User extends Model<User> {
    /* 主键id */
    @TableId
    private Integer id;

    /* 用户编号 */
    private String number;

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

    /* 部门_id:1系统管理员、2公司人员、3顾客 */
    private Integer deptId;

    /* 创建时间 */
    private LocalDateTime createTime;

    /* 更新时间 */
    private LocalDateTime updateTime;

    /* 最后修改人 */
    private Integer lastReviserId;

    /* 删除标志位 */
    private Integer delFlag;
}
