package com.housekeeping.admin.dto;

import com.housekeeping.admin.vo.TimeSlot;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @Author su
 * @create 2021/5/9 12:49
 */
@Data
public class QueryDTO {

    private Integer indexId;    /* 主页元素类型 */
    private List<Integer> jobs; /* 工作内容 */
    private Integer type;       /* 类型 1:钟点工   2：包工   3：全都要 */
    private LocalDate start;    /* 开始时间 */
    private LocalDate end;      /* 结束时间 */
    private List<TimeSlot> timeSlots;/* 时间段s */
    private BigDecimal lowHourlyWage;/* 時薪区间 低价 */
    private BigDecimal highHourlyWage;/* 時薪区间 高价 */
    private AddressDetailsDTO addressDetails;   /* 服务地址，经纬度 */
    private Integer priorityType;   /* 排序类型
                                         0 | null 默认排序
                                         1    价格合适排序
                                         2    距离排序
                                         3    评价排序
                                         4    钟点工作内容排序
                                    */

    private List<Boolean> certified;  /* 0：已认证企业  1：工作室  2：个体户  */

}
