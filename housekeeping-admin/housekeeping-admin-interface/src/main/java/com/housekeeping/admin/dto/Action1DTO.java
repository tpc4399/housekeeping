package com.housekeeping.admin.dto;

import com.housekeeping.admin.vo.TimeSlot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * 预约钟点工的参数
 * @Author su
 * @Date 2021/4/15 9:18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Action1DTO {

    private List<Integer> jobs;     /* 工作内容 */
    private LocalDate start;        /* 开始日期 */
    private LocalDate end;          /* 结束日期 */
    private List<Integer> weeks;    /* 周数 */
    private List<TimeSlot> timeSlots;   /* 上门时间段 */

}
