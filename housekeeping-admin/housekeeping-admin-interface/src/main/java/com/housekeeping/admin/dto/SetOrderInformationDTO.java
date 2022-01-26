package com.housekeeping.admin.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SetOrderInformationDTO {
    private String number;                        //订单编号
    private List<SetWorkDetailsDTO> workDetails;  //订单安排详情 (工作内容、时间安排)
    private List<Integer> jobIds;                 //工作内容
    private BigDecimal discountPrice;                 //折后价
    private String name;            /* 客户名 */
    private String address;         /* 详细地址 */
    private Float lng;              /* 经度 */
    private Float lat;              /* 纬度 */
    private String phonePrefix;     /* 手機號前綴 */
    private String phone;           /* 手機號 */
}
