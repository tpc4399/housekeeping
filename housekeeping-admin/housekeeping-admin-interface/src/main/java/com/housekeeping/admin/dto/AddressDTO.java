package com.housekeeping.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author su
 * @Date 2021/3/29 23:31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDTO {

    private Boolean getGPSSuccess;/* 是否成功获取GPS */
    private String address;
    private Float lng;/* 经度 */
    private Float lat;/* 纬度 */

}
