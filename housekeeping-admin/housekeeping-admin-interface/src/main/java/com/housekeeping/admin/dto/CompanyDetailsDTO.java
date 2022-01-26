package com.housekeeping.admin.dto;

import lombok.Data;

/**
 * @Author su
 * @create 2020/11/9 14:20
 */
@Data
public class CompanyDetailsDTO {

    private Integer id;    /* 主键 */
    private String noCertifiedCompany;        /* 未认证公司名 */
    private Integer companySizeId;    /* 公司规模_id */
    private String companyName;    /* 公司全名 */
    private String address1;/* 省 */
    private String address2;/* 市 */
    private String address3;/* 區 */
    private String address4;/* 詳細地址 */
    private String serviceHotlinePrefix;
    private String serviceHotline;    /* 服务热线 */
    private String email;    /* 公司邮箱 */
    private String webPages;    /* 网页 */
    private String accountLine;    /* LINE账号 */
    private String connectionFacebook;    /* facebook链接 */
    private String connectionInstagram;    /* Instagram链接 */
    private String describes;    /* 公司描述 */
    private String methodPayment;    /* 支付方式 */
    private String bankName;
    private String bankAccount;

}
