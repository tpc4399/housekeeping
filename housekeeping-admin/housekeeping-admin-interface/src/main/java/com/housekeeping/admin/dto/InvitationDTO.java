package com.housekeeping.admin.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class InvitationDTO {

    private Integer id;             /* 主键id */
    private String number;          /* 用户编号 */
    private String nickname;        /* 用户昵称 */
    private String name;            /* 用户姓名 */
    private LocalDate dateOfBirth;  /* 出生年月日 */
    private String phonePrefix;     /* 手机号前缀 */
    private String phone;           /* 手机号 */
    private String email;           /* 邮箱 */
    private String password;        /* 密码 */
    private Integer deptId;         /* 部门_id:1系统管理员、2公司人员、3顾客 */
    private LocalDateTime createTime;/* 创建时间 */
    private LocalDateTime updateTime;/* 更新时间 */
    private Integer lastReviserId;   /* 最后修改人_userId */
    private String headUrl;
    private BigDecimal bonus;      //佣金（台币）
}
