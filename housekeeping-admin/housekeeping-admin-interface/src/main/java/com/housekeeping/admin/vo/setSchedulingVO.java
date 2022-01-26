package com.housekeeping.admin.vo;

import lombok.Data;

import java.util.List;

@Data
public class setSchedulingVO {

    private Integer empId;
    private List<Integer> calendarIds;
}
