package com.housekeeping.admin.dto;

import lombok.Data;

/**
 * @Author su
 * @Date 2020/12/8 14:40
 */
@Data
public class SysEnterpriseAuthenticationMessagePostDTO {

    private String companyName; /* 公司名 */
    private String companyNumber; /* 注册编号 */
    private String legalName; /* 负责人、法人 */
    private String phonePrefix; /* 电话号码前缀 */
    private String phone; /* 电话号码 */
    private String RegisterAddress; /* 注册地址 */
    private Boolean isSend; /* 是否发送申请 true:发送申请 false:保存到草稿 */

}
