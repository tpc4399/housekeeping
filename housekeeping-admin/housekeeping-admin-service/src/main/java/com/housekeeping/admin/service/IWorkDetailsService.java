package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.entity.WorkDetails;
import com.housekeeping.admin.entity.WorkTimeDateVO;
import com.housekeeping.admin.vo.WorkTimeTableDateVO;

import java.time.LocalDate;
import java.util.List;

/**
 * @Author su
 * @Date 2021/4/28 16:10
 */
public interface IWorkDetailsService extends IService<WorkDetails> {

    void add(WorkDetails wd);

    List<WorkTimeTableDateVO> getWorkTables(List<Long> numbers, LocalDate startDate, LocalDate endDate,Integer month);

    List<WorkTimeDateVO> getWorkTablesByEmp(Integer empId, LocalDate startDate, LocalDate endDate);

    List<WorkTimeDateVO> getWorkTablesByCom(Integer comId,LocalDate startDate, LocalDate endDate);
}
