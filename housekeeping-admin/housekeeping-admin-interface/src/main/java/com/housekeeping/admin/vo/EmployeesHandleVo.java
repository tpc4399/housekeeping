package com.housekeeping.admin.vo;

import com.housekeeping.admin.dto.AddressDetailsDTO;
import com.housekeeping.admin.dto.IndexQueryResultEmployees;
import com.housekeeping.admin.dto.QueryIndexDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @Author su
 * @Date 2021/2/20 11:13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeesHandleVo {

    Integer existEmployeesId;
    AddressDetailsDTO addressDetailsDTO;
    LocalDate start;
    LocalDate end;
    Integer indexId;
    List<TimeSlot> timeSlots;
    Integer type;
    List<Integer> contendId;


}
