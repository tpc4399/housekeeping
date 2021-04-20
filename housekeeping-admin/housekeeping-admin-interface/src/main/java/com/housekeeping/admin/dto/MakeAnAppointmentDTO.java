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
    private LocalDate start;
    private LocalDate end;
    private List<Integer> weeks;
    private List<Integer> jobIds;
    private List<TimeSlot> timeSlots;

}
