package com.housekeeping.admin.pojo;

import com.housekeeping.admin.entity.TokenOrder;
import com.housekeeping.admin.entity.TokenOrderParent;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderResult {
    private OrderDetailsParent orderDetailsParent;
    private TokenOrderParent tokenOrder;
    private Boolean type;           /* true預約訂單 false代幣訂單 */
    private LocalDateTime createTime;
    private Integer orderState;
    private String number;
}
