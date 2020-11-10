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
@TableName("manager_details")
public class ManagerDetails extends Model<EmployeesDetails> {

    private static final long serialVersionUID = 1L;

    /* 主鍵id */
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    /* 經理编号 */
    private String number;

    /* 經理姓名 */
    private String name;

    /* 經理生日 */
    private LocalDate dateOfBirth;

    /* 手機號 */
    private Integer phone;

    /* 是否實名 */
    private Integer realName;

    /* 郵箱 */
    private String email;

    /* 時間表 */
    private String schedule;

    /* 所屬公司id */
    private Integer companyId;

    /* 身份證 */
    private String idCard;

    /* 地址 */
    private String location;

    /* 地區 */
    private String address;

    /* 工作年限 */
    private String workYear;

    /* 學歷 */
    private String educationBackground;

    /* line賬號 */
    private String accountLine;

    /* 描述 */
    private String describes;

    /* 照片 */
    private String phoneUrl;

    /* 性別 */
    private Integer sex;

    /* 登錄二維碼 */
    private String qrcode;

    /* 登錄鏈接 */
    private String link;

    /* 创建时间 */
    private LocalDateTime createTime;

    /* 修改时间 */
    private LocalDateTime updateTime;

    /* 最后修改人 */
    private Integer lastReviserId;
}
