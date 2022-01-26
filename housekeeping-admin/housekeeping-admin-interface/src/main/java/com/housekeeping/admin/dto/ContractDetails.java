package com.housekeeping.admin.dto;

import com.housekeeping.admin.vo.TimeSlot;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @Author su
 * @Date 2021/2/4 15:00
 */
@Data
public class ContractDetails {

    private Integer type;           /* 包工类型 */
    private String name;            /* 名称 */
    private String description;     /* 包工描述 */
    private String photoUrls;       /* 包工照片urls */
    private Float weekWage;         /* 周价格 */
    private String code;            /* 周价格货币编码 */
    private String activityIds;     /* 参与活动_ids */

    private Float totalPrice;       /* 总價格 */
    private Float attendance;       /* 总出勤率 */
    private Map<LocalDate, List<TimeSlot>> noAttendance;  /* 不能出勤的详细时间 */

}
