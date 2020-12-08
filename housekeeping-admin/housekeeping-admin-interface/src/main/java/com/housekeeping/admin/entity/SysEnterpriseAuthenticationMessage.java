package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 企业认证信息
 * @Author su
 * @Date 2020/12/8 14:01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_enterprise_authentication_message")
public class SysEnterpriseAuthenticationMessage extends Model<SysEnterpriseAuthenticationMessage> {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;    /* 主鍵id */
    private String number; /* 消息编号 */
    private Integer companyId; /* 公司_id */
    private String companyName; /* 公司名 */
    private String companyNumber; /* 注册编号 */
    private String legalName; /* 负责人、法人 */
    private String phonePrefix; /* 电话号码前缀 */
    private String phone; /* 电话号码 */
    private String RegisterAddress; /* 注册地址 */

    private LocalDateTime createTime;  /* 创建时间 */
    private LocalDateTime updateTime;  /* 修改时间 */
    private Integer lastReviserId;  /* 最后修改人 */
    /***
     * 审核状态:
     * 0->编辑中的草稿、或者撤銷後的申請單
     * 1->已发布审核申请、审核中
     * 3->已审核通过
     * 4->已审核未通过
     */
    private Integer auditStatus;  /* 审核状态 */

}
