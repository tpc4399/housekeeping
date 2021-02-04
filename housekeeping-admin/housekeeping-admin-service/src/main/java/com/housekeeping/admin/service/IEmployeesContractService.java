package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.AddEmployeesContractDTO;
import com.housekeeping.admin.dto.DateSlot;
import com.housekeeping.admin.entity.EmployeesContract;
import com.housekeeping.admin.vo.TimeSlot;
import com.housekeeping.common.utils.R;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @Author su
 * @Date 2021/1/30 17:16
 */
public interface IEmployeesContractService extends IService<EmployeesContract> {

    R add(AddEmployeesContractDTO dto);
    R getByEmployeesId(Integer employeesId);
    /* 获取这段日期这个包工的时间表 */
    Map<LocalDate, List<TimeSlot>> getCalendarByContractId(DateSlot dateSlot, Integer contractId);
    /* 获取这段日期这个闲置时间表 */
    Map<LocalDate, List<TimeSlot>> getFreeTimeByContractId(DateSlot dateSlot, Integer contractId);

}
