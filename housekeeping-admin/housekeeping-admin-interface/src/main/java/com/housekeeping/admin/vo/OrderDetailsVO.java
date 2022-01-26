package com.housekeeping.admin.vo;

import com.housekeeping.admin.entity.OrderDetails;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class OrderDetailsVO {

    private LocalDate date;
    private List<OrderDetails> orderDetails;
    private Integer orderTotal;
    private BigDecimal priceTotal;
}
