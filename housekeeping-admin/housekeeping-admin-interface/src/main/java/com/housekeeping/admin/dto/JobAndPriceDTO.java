package com.housekeeping.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author su
 * @Date 2021/1/29 17:14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobAndPriceDTO {

    private Integer jobId;  /* 工作內容_id */
    private Float price;    /* 價格 */
    private String code;    /* 貨幣代碼 */

}
