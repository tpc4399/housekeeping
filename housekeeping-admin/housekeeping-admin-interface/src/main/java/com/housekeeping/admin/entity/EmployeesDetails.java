package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("employees_details")
public class EmployeesDetails extends Model<EmployeesDetails> {

    private static final long serialVersionUID = 1L;

    /* 主鍵id */
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    /* 員工编号 */
    private String number;

    /* 員工姓名 */
    private String name;

    /* 員工生日 */
    private LocalDate dateOfBirth;

    /* 手機號 */
    private String phone;

    /* 郵箱 */
    private String email;

    /* 所屬公司id */
    private Integer companyId;

    /* 身份證 */
    private String idCard;

    private String address1;/* 省 */
    private String address2;/* 市 */
    private String address3;/* 區 */
    private String address4;/* 詳細地址 */

    /* 工作年限 */
    private String workYear;

    /* 學歷 */
    private String educationBackground;

    private String accountLine;    /* line賬號 */
    private String describes;    /* 描述 */
    private Integer scopeOfOrder;    /* 接單範圍 */
    private Integer numberOfOrders;    /* 接單次數 */


    /* 時薪 */
    private Integer hourlyWage;

    /* 时薪单位数字代码 */
    private String code;

    /* 时薪单位英文代码 */
    private String engName;

    /* 照片 */
    private String phoneUrl;

    private Boolean sex;    /* 性別 */
    private String tags;    /* 標簽 */
    private String recordOfFormalSchooling; /* 学历 */
    private Integer blacklistFlag;    /* 是否加入黑名單 */

    private LocalDateTime createTime;    /* 创建时间 */
    private LocalDateTime updateTime;    /* 修改时间 */
    private Integer lastReviserId;    /* 最后修改人 */
}
