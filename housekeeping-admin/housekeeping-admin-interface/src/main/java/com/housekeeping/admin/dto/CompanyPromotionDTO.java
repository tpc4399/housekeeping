package com.housekeeping.admin.dto;

import com.housekeeping.admin.entity.CompanyPromotion;
import lombok.Data;

@Data
public class CompanyPromotionDTO extends CompanyPromotion {

    private Integer tokens; /* 代币数 */
    private String lastReviserName; /* 最后修改人 */
}
