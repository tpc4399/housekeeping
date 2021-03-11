package com.housekeeping.admin.dto;

import com.housekeeping.admin.vo.TimeSlot;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @Author su
 * @Date 2021/2/2 11:37
 */
@Data
public class QueryIndexDTO {

    private Integer indexId;    /* 主页元素类型 */
    private Integer type;       /* 类型 1:钟点工   2：包工   3：全都要 */
    private LocalDate start;    /* 开始时间 */
    private LocalDate end;      /* 结束时间 */
    private List<TimeSlot> timeSlots;/* 时间段s */
    private BigDecimal lowPrice;/* 价格区间 低价 */
    private BigDecimal highPrice;/* 价格区间 高价 */
    private AddressDetailsDTO addressDetails;   /* 服务地址，经纬度 */
    private Integer priorityType;   /* 优先类型
                                                       0 | null 无优先选择，默认权重
                                                       1    距離權重
                                                       2    地區權重
                                                       3    價格權重
                                                       4    出勤率權重
                                                       5    評價權重
                                                       6    員工推廣權重
                                                       */

}
