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
@TableName("company_details")
public class Company extends Model<Company> {

    private static final long serialVersionUID = 1L;

    /* 主键id */
    @TableId
    private Integer id;

    /* 公司编号 */
    private String number;

    /* 公司名称 */
    private String companyName;

    /* 公司规模id */
    private String companySizeId;

    /* '公司简介 */
    private LocalDate companyProfile;

    /* 主打业务 */
    private String mainBusiness;

    /* 优惠 */
    private String preferential;

    /* 活动 */
    private String activity;

    /* 公司架构 */
    private String companyStructure;

    /* 法人、创始人 */
    private Integer legalPerson;

    /* 是否验证企业信息 */
    private LocalDateTime isValidate;

    /* 工商注册编号 */
    private LocalDateTime industrialNumber;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer lastReviserId;

    /* 删除标志 */
    private Integer delFlag;

    /* logo的url */
    private String logo_url;

    /* 地区 */
    private String region;

    /* 详细地址 */
    private String detailAddress;

    /* 公司架构 */
    private String 服务热线;

    /* 邮箱 */
    private String email;

    /* 网页 */
    private String webPages;

    /* LINE账号 */
    private String accountLine;

    /* facebook连接 */
    private String connectionFacebook;

    /* Instagram连接 */
    private String connectionInstagram;

    /* 公司描述 */
    private String describe;

    /* 支付方式 */
    private String methodPayment;
}
