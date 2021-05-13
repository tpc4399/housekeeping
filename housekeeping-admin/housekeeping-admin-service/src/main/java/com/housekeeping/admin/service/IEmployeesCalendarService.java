package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.*;
import com.housekeeping.admin.entity.EmployeesCalendar;
import com.housekeeping.admin.pojo.LocalTimeAndPricePOJO;
import com.housekeeping.admin.pojo.TodayDetailsPOJO;
import com.housekeeping.admin.pojo.WorkDetailsPOJO;
import com.housekeeping.common.utils.R;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * @Author su
 * @create 2020/11/12 16:21
 */
public interface IEmployeesCalendarService extends IService<EmployeesCalendar> {
    /* 設置通用規則 */
    R setCalendar(SetEmployeesCalendarDTO dto);
    /* 添加一條周規則 */
    R addCalendarWeek(SetEmployeesCalendarWeekDTO dto);
    /* 添加一條日規則 */
    R addCalendarDate(SetEmployeesCalendarDateDTO dto);
    /* 新版设置周规则 */
    R setCalendar2(SetEmployeesCalendar2DTO dto);
    /* 修改某条周规则 */
    R updateCalendar2(UpdateEmployeesCalendarDTO dto);
    /* 删除某条周规则 */
    R del(Integer id);
    R setJobs(SetEmployeesJobsDTO dto);
    /* 获取这段日期的时间表 */
    Map<LocalDate, List<TimeSlotDTO>> getCalendarByDateSlot(DateSlot dateSlot, Integer employeesId, String toCode);
    /* 获取这段日期的时间表 時間表減去已排任務時間 */
    Map<LocalDate, List<TimeSlotDTO>> getFreeTimeByDateSlot(DateSlot dateSlot, Integer employeesId, String toCode);
    /* 根据保洁员id获取技能标签 */
    R getSkillTags(Integer employeesId);

    /* 获取这段日期的时间表 時間表減去已排任務時間 */
    List<FreeDateTimeDTO> getFreeTimeByDateSlot2(GetCalendarByDateSlotDTO dto);

    /* 预约钟点工 */
    R makeAnAppointment(MakeAnAppointmentDTO dto);

    /* 根据保洁员userId获取保洁员的排班 */
    R getSchedulingByUserId(Integer userId);

    /* 根据保洁员employeesId获取保洁员的排班 */
    R getSchedulingByEmployeesId(Integer employeesId);

    /* 根据calendarId获取排班信息 */
    R getByCalendarId(Integer id);

    /* 判断employeesId的时间表存在性 */
    Boolean judgmentOfExistenceByEmployeesId(Integer employeesId);

    /* 处理预约钟点流程，取消不能做的日子 */
    List<WorkDetailsPOJO> makeAnAppointmentHandle(MakeAnAppointmentDTO dto);

    /* 判断今日能否做 */
    Boolean judgeToday(List<LocalTimeAndPricePOJO> table, List<LocalTime> item);

    /* 计算订单总价格 */
    BigDecimal totalPrice(List<WorkDetailsPOJO> workDetails);

    /* 计算可工作的天数 */
    Integer days(List<WorkDetailsPOJO> workDetails);

    /* 计算每天工作小时数 */
    Float hOfDay(MakeAnAppointmentDTO dto);

    /* 獲取時間表 */
    Map<LocalDate, TodayDetailsPOJO> getCalendar(GetCalendarByDateSlotDTO dto);

    /* 獲取時間表空閒時間 */
    Map<LocalDate, TodayDetailsPOJO> getCalendarFreeTime(GetCalendarByDateSlotDTO dto);

}
