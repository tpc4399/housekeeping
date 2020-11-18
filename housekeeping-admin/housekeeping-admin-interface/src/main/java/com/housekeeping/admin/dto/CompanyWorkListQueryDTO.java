package com.housekeeping.admin.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author su
 * @create 2020/11/18 17:05
 */
@Data
public class CompanyWorkListQueryDTO {
    private Integer groupId;    /* 分组_id */
    private Integer orderId;    /* 订单_id */
    private LocalDateTime createTimeStart;   /* 创建时间 */
    private LocalDateTime createTimeEnd;   /* 创建时间 */
    private Integer lastReviserId;      /* 最后修改人、创建人 */
}
