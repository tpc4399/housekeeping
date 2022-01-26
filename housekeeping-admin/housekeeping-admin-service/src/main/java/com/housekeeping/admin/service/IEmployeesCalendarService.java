package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.*;
import com.housekeeping.admin.entity.EmployeesCalendar;
import com.housekeeping.admin.pojo.LocalTimeAndPricePOJO;
import com.housekeeping.admin.pojo.TodayDetailsPOJO;
import com.housekeeping.admin.pojo.WorkDetailsPOJO;
import com.housekeeping.admin.vo.TimeSlot;
import com.housekeeping.admin.vo.setSchedulingVO;
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

    /* 新版设置周规则 */
    R setCalendar2(SetEmployeesCalendar2DTO dto);
    /* 修改某条周规则 */
    R updateCalendar2(UpdateEmployeesCalendarDTO dto);
    /* 删除某条周规则 */
    R del(Integer id);
    R setJobs(SetEmployeesJobsDTO dto);
    /* 根据保洁员id获取技能标签 */
    R getSkillTags(Integer employeesId);
    /* 获取这段日期的时间表 時間表減去已排任務時間 */
    List<FreeDateTimeDTO> getFreeTimeByDateSlot2(GetCalendarByDateSlotDTO dto);
    /* 获取这个月的空闲时间表,自动补全 */
    R getFreeTimeByMonth(GetFreeTimeByMonthDTO dto);
    /* 获取这个月的无法出勤的日子,自动补全 */
    R getAbsenceDaysByMonth(GetFreeTimeByMonthDTO dto);
    /* 根据时间段获取无时间日期 */
    R getAbsenceDaysByDateSlot(GetCalendarByDateSlotDTO dto);
    /* 预约钟点工 */
    R makeAnAppointment(MakeAnAppointmentDTO dto);
    /* 根据calendarId获取排班信息 */
    R getByCalendarId(Integer id);
    /* 判断employeesId的时间表存在性 */
    Boolean judgmentOfExistenceByEmployeesId(Integer employeesId);
    /* 处理预约钟点流程，取消不能做的日子,是否需要 */
    List<WorkDetailsPOJO> makeAnAppointmentHandle(MakeAnAppointmentDTO dto, Boolean need);

    List<WorkDetailsPOJO> makeAnAppointmentHandle2(MakeAnAppointmentDTO dto, Boolean need,BigDecimal totalPrice);
    /* 判断今日能否做 */
    Boolean judgeToday(List<LocalTimeAndPricePOJO> table, List<LocalTime> item);
    /* 给时段带上价格 */
    List<TimeSlot> withPriceOfSlot(List<LocalTime> item, List<LocalTimeAndPricePOJO> enableTimeToday);
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
    List<WorkDetailsPOJO> makeAnAppointmentHandles(MakeAnAppointmentDTO dto, Boolean need,Integer jobId);
    /* 获取本公司所有保洁员的时间表 */
    R getAllInCompany();
    /* 統一設置本公司保潔時間表 */
    R setCalendarAll(SetCalendarAllDTO dto);
    /* 根据保洁员employeesId获取保洁员的排班 */
    R getSchedulingByEmployeesId(Integer employeesId);

    R getFreeTimeByMonth2(GetFreeTimePriceByMonthDTO dto);

    R getSchedulingById(Integer companyId);

    R setSchedulingByIds(setSchedulingVO vo);

    List<FreeDateTimePriceDTO> getFreeTime(List<FreeDateTimePriceDTO> calendarByDateSlot, GetCalendarByDateSlotDTO id);

    R confirmOrder(MakeAnAppointmentDTO dto);

    List<FreeDateTimeDTO> getFreeTimeByDateSlot4(GetCalendarByDateSlotDTO dto);

    R MakeAnAppointmentByDateDTO(MakeAnAppointmentByDateDTO dto);

    List<WorkDetailsPOJO> makeAnAppointmentHandleByDate(MakeAnAppointmentByDateDTO dto, boolean need);

    R confirmOrderByDate(MakeAnAppointmentByDateDTO dto);
}
