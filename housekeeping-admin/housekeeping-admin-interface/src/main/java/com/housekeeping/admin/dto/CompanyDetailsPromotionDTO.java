package com.housekeeping.admin.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CompanyDetailsPromotionDTO {

    private Integer companyId;
    private String number;
    private String companyName;
    private String companyProfile;
    private String logoUrl;
    private Integer tokens;
    private Boolean promotion;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
