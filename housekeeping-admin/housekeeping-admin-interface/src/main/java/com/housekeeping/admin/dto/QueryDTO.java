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
    private Integer priorityType;   /* 优先类型
                                         0 | null 无优先选择，默认权重   默认排序优先级
                                         1    距離權重                 距离近优先
                                         2    地區權重                 地区合适优先
                                         3    價格權重                 价格合适优先
                                         4    出勤率權重               匹配度高优先
                                         5    評價權重                 评分高优先
                                         6    員工推廣權重              推广优先
                                         7    接单次数优先              单次优先
                                    */

    private List<Boolean> certified;  /* 0：已认证企业  1：个体户  */

}
