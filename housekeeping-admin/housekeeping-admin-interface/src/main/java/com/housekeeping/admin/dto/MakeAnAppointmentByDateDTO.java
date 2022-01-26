package com.housekeeping.admin.dto;

import com.housekeeping.admin.vo.TimeSlot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MakeAnAppointmentByDateDTO {

    private Integer employeesId;
    private Integer addressId; //地址id
    private LocalDate start;
    private LocalDate end;
    private List<LocalDate> dates;
    private List<Integer> jobIds;   //工作內容
    private List<Integer> notes;    //工作筆記
    private List<TimeSlot> timeSlots;
}
