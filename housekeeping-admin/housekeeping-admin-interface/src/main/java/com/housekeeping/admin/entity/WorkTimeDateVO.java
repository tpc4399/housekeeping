package com.housekeeping.admin.entity;

import com.housekeeping.admin.vo.WorkTimeTableVO;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class WorkTimeDateVO {
    private LocalDate date;         //当前日
    private Integer workTotal;      //工作数量
    private List<WorkTimeTableVO> workTimeTable;        //工作详情
}
