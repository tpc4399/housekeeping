package com.housekeeping.admin.dto;

import com.housekeeping.admin.vo.TimeSlot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * @Author su
 * @Date 2021/4/13 9:24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MakeAnAppointmentDTO {

    private Integer employeesId;
    private Integer addressId; //地址id
    private LocalDate start;
    private LocalDate end;
    private List<Integer> weeks;
    private List<Integer> jobIds;   //工作內容
    private List<Integer> notes;    //工作筆記
    private List<TimeSlot> timeSlots;

}
