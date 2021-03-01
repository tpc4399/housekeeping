package com.housekeeping.admin.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @Author su
 * @Date 2021/2/7 15:51
 */
@Data
public class PageOfUserDTO {

    private String number;                  /* 用户编号 */
    private String nickname;                /* 用户昵称 */
    private String name;                    /* 用户姓名 */
    private LocalDate dateOfBirthStart;     /* 出生年月日界限1 */
    private LocalDate dateOfBirthEnd;       /* 出生年月日界限2 */
    private String phonePrefix;             /* 手机号前缀 */
    private String phone;                   /* 手机号 */
    private String email;                   /* 邮箱 */
    private Integer deptId;                 /* 部门_id:1系统管理员、2公司人员、3顾客 */
    private LocalDateTime createTimeStart;  /* 创建时间界限1 */
    private LocalDateTime createTimeEnd;    /* 创建时间界限2 */
    private LocalDateTime updateTimeStart;  /* 更新时间界限1 */
    private LocalDateTime updateTimeEnd;    /* 更新时间界限2 */
    private Integer lastReviserId;          /* 最后修改人 */

}
