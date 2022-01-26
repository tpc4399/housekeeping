package com.housekeeping.admin.dto;

import lombok.Data;


@Data
public class DemandDto {

    private String jobIds;          /* 工作内容ids */
    private String workTypeIds;     /* 工作类型ids */
    private String place;           /* 地址 */
    private String startDate;    /* 开始日期 */
    private String lowPrice;        /* 最低价格 */
    private String highPrice;       /* 最高价格 */

}
