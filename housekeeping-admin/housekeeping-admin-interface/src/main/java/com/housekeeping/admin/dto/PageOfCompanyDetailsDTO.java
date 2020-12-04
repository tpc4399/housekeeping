package com.housekeeping.admin.dto;

import com.housekeeping.interfaces.wrapper.Between;
import com.housekeeping.interfaces.wrapper.Wrapper;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author su
 * @Date 2020/12/4 17:11
 */
@Data
public class PageOfCompanyDetailsDTO {

    @Wrapper(field = "company_name", keyword = "like")      private String companyName;    /* 公司全名 */
    @Wrapper(field = "company_size_id")                     private Integer companySizeId;    /* 公司规模_id */
    @Wrapper(field = "company_profile", keyword = "like")   private String companyProfile;    /* 公司简介 */
    @Wrapper(field = "legal_person", keyword = "like")      private String legalPerson;    /* 法人创始人 */
    @Wrapper(field = "is_validate")                         private Boolean isValidate;    /* 是否验证企业信息 */
    @Wrapper(field = "industrial_number")                   private String industrialNumber;    /* 工商注册编号 */
    @Wrapper(field = "address1", keyword = "like")          private String address1;   /* 省 */
    @Wrapper(field = "address2", keyword = "like")          private String address2;/* 市 */
    @Wrapper(field = "address3", keyword = "like")          private String address3;/* 區 */
    @Wrapper(field = "address4", keyword = "like")          private String address4;/* 詳細地址 */
    @Wrapper(field = "service_hotline", keyword = "like")   private String serviceHotline;    /* 服务热线 */
    @Wrapper(field = "email", keyword = "like")             private String email;    /* 公司邮箱 */
    @Wrapper(field = "web_pages", keyword = "like")         private String webPages;    /* 网页 */
    @Wrapper(field = "account_line", keyword = "like")      private String accountLine;    /* LINE账号 */
    @Wrapper(field = "connection_facebook", keyword = "like") private String connectionFacebook;    /* facebook链接 */
    @Wrapper(field = "connection_instagram", keyword = "like") private String connectionInstagram;    /* Instagram链接 */
    @Wrapper(field = "describes", keyword = "like")         private String describes;    /* 公司描述 */
    @Wrapper(field = "method_payment")                      private String methodPayment;    /* 支付方式 */
    @Wrapper(field = "create_time", keyword = "between")    private Between<LocalDateTime> createTime;/* 创建时间 */

}
