package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.housekeeping.admin.dto.CompanyDetailsDTO;
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

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;    /* 主键 */
    private String number;    /* 公司编号 */
    private Integer userId;    /* 所屬用戶 */
    private String companyName;    /* 公司全名 */
    private Integer companySizeId;    /* 公司规模_id */
    private String companyProfile;    /* 公司简介 */
    private String mainBusiness;    /* 主打业务 */
    private String preferential;    /* 优惠 */
    private String activity;    /* 活动 */
    private String companyStructure;    /* 公司架构 */
    private String legalPerson;    /* 法人创始人 */
    private Integer isValidate;    /* 是否验证企业信息 */
    private String industrialNumber;    /* 工商注册编号 */
    private String logoUrl;    /* logo url */
    private String photos;    /* 五张照片url拼接 */
    private String region;    /* 地区 */
    private String detailAddress;    /* 详细地址 */
    private String serviceHotline;    /* 服务热线 */
    private String email;    /* 公司邮箱 */
    private String webPages;    /* 网页 */
    private String accountLine;    /* LINE账号 */
    private String connectionFacebook;    /* facebook链接 */
    private String connectionInstagram;    /* Instagram链接 */
    private String describes;    /* 公司描述 */
    private String methodPayment;    /* 支付方式 */
    private LocalDateTime createTime;    /* 创建时间 */
    private LocalDateTime updateTime;    /* 修改时间 */
    private Integer lastReviserId;    /* 最后修改人 */

}
