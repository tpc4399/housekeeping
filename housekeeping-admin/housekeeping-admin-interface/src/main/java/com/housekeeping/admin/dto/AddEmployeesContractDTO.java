package com.housekeeping.admin.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author su
 * @Date 2021/2/1 10:12
 */
@Data
public class AddEmployeesContractDTO {

    private Integer employeesId;        /* 保洁员_id */
    private Integer type;               /* 包工类型 */
    private String name;                /* 名称 */
    private List<WeekAndTimeSlotsDTO> weekAndTimeSlotsList; /* 具体时间安排 */
    private String description;         /* 包工描述 */
    private Float dayWage;              /* 天价格 */
    private String code;                /* 天价格货币编码 */
    private List<Integer> activityIds;  /* 参与活动_ids */

}
