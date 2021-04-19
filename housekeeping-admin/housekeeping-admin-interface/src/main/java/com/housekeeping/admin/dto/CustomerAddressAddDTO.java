package com.housekeeping.admin.dto;

import lombok.Data;

/**
 * @Author su
 * @create 2020/11/23 11:33
 */
@Data
public class CustomerAddressAddDTO {

    private String name;            /* 地址名 */
    private String address;         /* 详细地址 */
    private Float lng;              /* 经度 */
    private Float lat;              /* 纬度 */
    private String phonePrefix;     /* 手機號前綴 */
    private String phone;           /* 手機號 */

}
