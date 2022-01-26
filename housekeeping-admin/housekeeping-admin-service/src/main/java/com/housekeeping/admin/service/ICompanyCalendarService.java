package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.SetCompanyCalendarDTO;
import com.housekeeping.admin.dto.UpdateCompanyCalendarDTO;
import com.housekeeping.admin.entity.CompanyCalendar;
import com.housekeeping.admin.entity.EmployeesCalendar;
import com.housekeeping.common.utils.R;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Author su
 * @create 2021/5/31 8:55
 */
public interface ICompanyCalendarService extends IService<CompanyCalendar> {

    R setCalendar(SetCompanyCalendarDTO dto);
    R updateCalendar(UpdateCompanyCalendarDTO dto);
    List<EmployeesCalendar> initEmpCalendar(Integer companyId, Integer employeesId);
    R mineCalendar();
    List<CompanyCalendar> getCalendar(Integer companyId);
    R del(Integer id);

}
