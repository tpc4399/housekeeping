package com.housekeeping.admin.dto;

import lombok.Data;

/**
 * @Author su
 * @create 2021/6/10 16:43
 */
@Data
public class SetOrderCustomerInformationDTO {

    private String orderNumber;     /* 訂單編號 */
    private String name;            /* 客户名 */
    private String address;         /* 详细地址 */
    private Float lng;              /* 经度 */
    private Float lat;              /* 纬度 */
    private String phonePrefix;     /* 手機號前綴 */
    private String phone;           /* 手機號 */

}
