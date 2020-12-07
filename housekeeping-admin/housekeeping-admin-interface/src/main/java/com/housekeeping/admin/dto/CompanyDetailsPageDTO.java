package com.housekeeping.admin.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author su
 * @Date 2020/12/7 15:38
 */
@Data
public class CompanyDetailsPageDTO {

    private String number;    /* 公司编号 */
    private String companyName;    /* 公司全名 */
    private Integer companySizeId;    /* 公司规模_id */
    private String legalPerson;    /* 法人创始人 */
    private Boolean isValidate;    /* 是否验证企业信息 */
    private String industrialNumber;    /* 工商注册编号 */
    private String address1;/* 省 */
    private String address2;/* 市 */
    private String address3;/* 區 */
    private String address4;/* 詳細地址 */
    private String serviceHotline;    /* 服务热线 */
    private String email;    /* 公司邮箱 */
    private String webPages;    /* 网页 */
    private String accountLine;    /* LINE账号 */
    private String connectionFacebook;    /* facebook链接 */
    private String connectionInstagram;    /* Instagram链接 */
    private String describes;    /* 公司描述 */
    private String methodPayment;    /* 支付方式 */
    private LocalDateTime createTimeStart;    /* 创建时间 */
    private LocalDateTime createTimeEnd;    /* 创建时间 */

}
