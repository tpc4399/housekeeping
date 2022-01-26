package com.housekeeping.admin.dto;

import lombok.Data;

@Data
public class PayToken {
    private String companyId;
    private Integer tokens;
    private Integer price;
    private String payType;
}
