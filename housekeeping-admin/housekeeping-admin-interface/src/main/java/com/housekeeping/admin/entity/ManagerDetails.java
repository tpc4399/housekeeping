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

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;    /* 主鍵id */
    private Integer userId; //用户_id
    private String number;    /* 經理编号 */
    private String name;    /* 經理姓名 */
    private LocalDate dateOfBirth;    /* 經理生日 */
    private String phonePrefix; /* 手機號前綴 */
    private String phone;    /* 手機號 */
    private Boolean realName;    /* 是否實名 */
    private String email;    /* 郵箱 */
    private String headUrl; /* 头像 */
    private String schedule; /* 時間表 */
    private Integer companyId;    /* 所屬公司id */
    private String idCard;    /* 身份證 */
    private String location;    /* 地址 */
    private String address;    /* 地區 */
    private String workYear;    /* 工作年限 */
    private String educationBackground;    /* 學歷 */
    private String accountLine;    /* line賬號 */
    private String describes;    /* 描述 */
    private String phoneUrl;    /* 照片 */
    private Boolean sex;    /* 性別 */
    private String qrcode;    /* 登錄二維碼 */
    private String link;    /* 登錄鏈接 */
    private LocalDateTime createTime;    /* 创建时间 */
    private LocalDateTime updateTime;    /* 修改时间 */
    private Integer lastReviserId;    /* 最后修改人 */

}
