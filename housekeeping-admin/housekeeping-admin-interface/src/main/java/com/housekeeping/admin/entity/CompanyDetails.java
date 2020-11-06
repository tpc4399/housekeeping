package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author su
 * @create 2020/11/5 10:52
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("company_details")
public class CompanyDetails extends Model<Log> implements Serializable {

    /* 主键 */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /* 公司编号 */
    private String number;

    /* 公司全名 */
    private String companyName;

    /* 公司规模_id */
    private Integer companySizeId;

    /* 公司简介 */
    private String companyProfile;

    /* 主打业务 */
    private String mainBusiness;

    /* 优惠 */
    private String preferential;

    /* 活动 */
    private String activity;

    /* 公司架构 */
    private String companyStructure;

    /* 法人创始人 */
    private String legalPerson;

    /* 是否验证企业信息 */
    private Integer isValidate;

    /* 工商注册编号 */
    private String industrialNumber;

    /* logo url */
    private String logoUrl;

    /* 五张照片url拼接 */
    private String photos;

    /* 地区 */
    private String region;

    /* 详细地址 */
    private String detailAddress;

    /* 服务热线 */
    private String serviceHotline;

    /* 公司邮箱 */
    private String email;

    /* 网页 */
    private String webPages;

    /* LINE账号 */
    private String accountLine;

    /* facebook链接 */
    private String connectionFacebook;

    /* Instagram链接 */
    private String connectionInstagram;

    /* 公司描述 */
    private String describe;

    /* 支付方式 */
    private String methodPayment;

    /* 创建时间 */
    private LocalDateTime createTime;

    /* 修改时间 */
    private LocalDateTime updateTime;

    /* 最后修改人 */
    private Integer lastReviserId;

}
