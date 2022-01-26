package com.housekeeping.admin.dto;

import lombok.Data;

/**
 * @Author su
 * @create 2021/5/8 15:48
 */
@Data
public class CompanyDetailsUpdateDTO {

    private Integer id;    /* 主键 */
    private String number;
    private String noCertifiedCompany;        /* 未认证公司名 */
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
    private String fivePhotoUrls;   /* 五張圖的鏈接 */
    private String bankName;
    private String bankAccount;

}
