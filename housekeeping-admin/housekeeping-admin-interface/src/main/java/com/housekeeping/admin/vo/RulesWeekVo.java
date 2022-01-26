package com.housekeeping.admin.vo;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @Author su
 * @create 2020/11/16 14:35
 */
@Data
public class RulesWeekVo {
    private LocalDate start;     //定期服务开始时间
    private LocalDate end;      //定期服务结束时间
    private String week;      //周数如135表示每周一周三周五
    private List<TimeSlot> timeSlotVos;  //时间段
}
