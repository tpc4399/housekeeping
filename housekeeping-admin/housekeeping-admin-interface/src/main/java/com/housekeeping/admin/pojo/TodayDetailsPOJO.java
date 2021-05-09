package com.housekeeping.admin.pojo;

import lombok.Data;

import java.util.List;

/**
 * @Author su
 * @create 2021/5/9 10:24
 */
@Data
public class TodayDetailsPOJO {

    private Integer week;               //今天周几
    private List<TimeSlotPOJO> times;
    private Boolean hasTime;            //今天有沒有時間，覺得日期是否變灰色

}
