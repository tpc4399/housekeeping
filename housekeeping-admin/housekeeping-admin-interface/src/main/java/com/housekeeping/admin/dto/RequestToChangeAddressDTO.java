package com.housekeeping.admin.dto;

import lombok.Data;

/**
 * @Author su
 * @Date 2021/4/22 11:17
 */
@Data
public class RequestToChangeAddressDTO {

    private Integer addressId;  //地址id
    private Long number;        //订单id

}
