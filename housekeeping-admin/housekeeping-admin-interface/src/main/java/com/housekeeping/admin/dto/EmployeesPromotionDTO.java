package com.housekeeping.admin.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EmployeesPromotionDTO {

    private Integer id;
    private String name;
    private String headUrl;
    private String accountLine;
    private Float starRating;
    private Boolean promotion;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer days;
    private Integer tokens;/* 代币数 */

}
