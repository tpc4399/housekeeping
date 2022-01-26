package com.housekeeping.admin.pojo;

import lombok.Data;

/**
 * @Author su
 * @create 2021/5/9 12:10
 */
@Data
public class CompanyDetailsPOJO {

    private String companyId;      /* 公司id */
    private String companyName;     /* 公司名 */
    private String describes;    /* 公司描述 */
    private String logoUrl;         /* 公司logo */
    private Boolean certified;      /* 是否已认证 */
    private String companyProfile;  /* 公司简介 */
    private String address;         /* 公司地址 */

}
