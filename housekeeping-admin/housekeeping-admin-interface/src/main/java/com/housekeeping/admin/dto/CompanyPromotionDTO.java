package com.housekeeping.admin.dto;

import com.housekeeping.admin.entity.CompanyPromotion;
import javafx.beans.binding.BooleanExpression;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CompanyPromotionDTO {

    private Integer id;
    private Integer companyId;
    private Integer days;
    private Boolean promotion;
    private LocalDateTime endTime;
    private Integer tokens; /* 代币数 */
    private String companyName; /* 公司名稱 */
}
