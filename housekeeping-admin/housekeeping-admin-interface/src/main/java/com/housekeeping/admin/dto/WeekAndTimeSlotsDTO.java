package com.housekeeping.admin.dto;

import com.housekeeping.admin.vo.TimeSlot;
import lombok.Data;

import java.util.List;

/**
 * @Author su
 * @Date 2021/2/1 13:09
 */
@Data
public class WeekAndTimeSlotsDTO {

    private List<Integer> week;         /* 周数 */
    private List<TimeSlot> timeSlot;    /* 时间段s */

}
