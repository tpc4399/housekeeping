package com.housekeeping.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author su
 * @create 2021/6/7 9:42
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderEvaluationDTO {

    private String orderNumber; //訂單編號
    private Integer starRating; //評分 1 2 3 4 5
    private String evaluation;  //評價
    private String imageUrls;   //多張照片urls

}
