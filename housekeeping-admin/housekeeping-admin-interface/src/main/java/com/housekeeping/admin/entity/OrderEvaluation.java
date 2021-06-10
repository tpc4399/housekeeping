package com.housekeeping.admin.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Author su
 * @create 2021/6/3 17:19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderEvaluation {

    private String orderNumber; //訂單編號
    /* 客戶評價保潔員 */
    private Boolean yes1;        //是否已评价
    private Integer starRating1; //評分 1 2 3 4 5
    private String evaluation1;  //評價
    private String imageUrls1;   //多張照片urls
    private LocalDateTime createTime1; //評價時間
    /* 保潔員評價客戶 */
    private Boolean yes2;        //是否已评价
    private Integer starRating2; //評分 1 2 3 4 5
    private String evaluation2;  //評價
    private String imageUrls2;   //多張照片urls
    private LocalDateTime createTime2; //評價時間

}
