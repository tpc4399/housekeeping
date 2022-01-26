package com.housekeeping.admin.vo;

import lombok.Data;


@Data
public class AdvertisingVo {

    private Integer id;
    private String title;           /* 標題 */
    private String link;            /* 鏈接 */
    private String content;         /* 内容 */
    private String photo;           /* 圖片 */
}
