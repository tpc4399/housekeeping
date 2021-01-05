package com.housekeeping.admin.vo;

import lombok.Data;



@Data
public class CompanyAdvertisingVo {

    private String title;           /* 標題 */
    private String link;            /* 鏈接 */
    private String content;         /* 内容 */
    private String photo;           /* 圖片 */
    private Integer day;            /* 選擇推廣天數 */
    private Integer tokens;         /* 支付代幣 */
}
