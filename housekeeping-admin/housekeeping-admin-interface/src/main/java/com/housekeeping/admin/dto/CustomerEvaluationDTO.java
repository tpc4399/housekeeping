package com.housekeeping.admin.dto;

import lombok.Data;

@Data
public class CustomerEvaluationDTO {

    private Integer id;
    private Integer customerStarRating; //客戶打分
    private String customerPhoto;       //客戶圖片
    private String customerEvaluation;  //客戶評價
}
