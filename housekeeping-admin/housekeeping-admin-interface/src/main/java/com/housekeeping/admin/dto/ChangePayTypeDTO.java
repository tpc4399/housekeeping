package com.housekeeping.admin.dto;

import lombok.Data;

/**
 * @Author su
 * @Date 2021/4/22 11:34
 */
@Data
public class ChangePayTypeDTO {

    private String payType;     //支付方式
    private Long number;        //订单id

}
