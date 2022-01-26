package com.housekeeping.admin.dto;

import lombok.Data;

/**
 * @Author su
 * @Date 2021/2/2 15:17
 */
@Data
public class AddressDetailsDTO {

    private String address;
    private Float lng;/* 经度 */
    private Float lat;/* 纬度 */

    public AddressDetailsDTO() {
    }

    public AddressDetailsDTO(String address, Float lng, Float lat) {
        this.address = address;
        this.lng = lng;
        this.lat = lat;
    }
}
