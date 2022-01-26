package com.housekeeping.common.utils;

import com.housekeeping.admin.entity.WorkWeek;
import com.housekeeping.admin.vo.StatisticsWeekVO;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TimeUtils {

    /**
     * 获取固定间隔时刻集合
     * @param start 开始时间
     * @param end 结束时间
     * @param interval 时间间隔(单位：分钟)
     * @return
     */
    public static List<String> getIntervalTimeList(String start, String end, int interval){
        Date startDate = convertString2Date("HH:mm",start);
        Date endDate = convertString2Date("HH:mm",end);
        List<String> list = new ArrayList<>();
        while(startDate.getTime()<=endDate.getTime()){
            list.add(convertDate2String("HH:mm",startDate));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            calendar.add(Calendar.MINUTE,interval);
            if(calendar.getTime().getTime()>endDate.getTime()){
                if(!startDate.equals(endDate)){
                    list.add(convertDate2String("HH:mm",endDate));
                }
                startDate = calendar.getTime();
            }else{
                startDate = calendar.getTime();
            }

        }
        return list;
    }

    public static Date convertString2Date(String format, String dateStr) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            try {
                Date date = simpleDateFormat.parse(dateStr);
                return date;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
    }

    public static String convertDate2String(String format,Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    public static void main(String[] args) {
        List<String> list = getIntervalTimeList("08:00", "12:00", 30);
        for (String s : list) {
            System.out.println(s);
        }
    }

    public static List<StatisticsWeekVO> converWeekList(LocalDate startDate, LocalDate endDate) {
        List<StatisticsWeekVO> weekList = new ArrayList<>();
        //转换成joda-time的对象
        LocalDate firstDay = startDate.dayOfWeek().withMinimumValue();
        LocalDate lastDay = endDate.dayOfWeek().withMaximumValue();
        //计算两日期间的区间天数
        Period p = new Period(firstDay, lastDay, PeriodType.days());
        int days = p.getDays();
        if (days > 0) {
            int weekLength = 7;
            for (int i = 0; i < days; i = i + weekLength) {
                String monDay = firstDay.plusDays(i).toString("yyyy-MM-dd");
                String sunDay = firstDay.plusDays(i + 6).toString("yyyy-MM-dd");
                StatisticsWeekVO statisticsWeekVO = new StatisticsWeekVO();
                statisticsWeekVO.setStartDate(monDay);
                statisticsWeekVO.setEndDate(sunDay);
                weekList.add(statisticsWeekVO);
            }
        }
        return weekList;
    }

    public static List<WorkWeek> converWeek(LocalDate startDate, LocalDate endDate) {
        List<WorkWeek> weekList = new ArrayList<>();
        //转换成joda-time的对象
        LocalDate firstDay = startDate.dayOfWeek().withMinimumValue();
        LocalDate lastDay = endDate.dayOfWeek().withMaximumValue();
        //计算两日期间的区间天数
        Period p = new Period(firstDay, lastDay, PeriodType.days());
        int days = p.getDays();
        if (days > 0) {
            int weekLength = 7;
            for (int i = 0; i < days; i = i + weekLength) {
                String monDay = firstDay.plusDays(i).toString("yyyy-MM-dd");
                String sunDay = firstDay.plusDays(i + 6).toString("yyyy-MM-dd");
                WorkWeek statisticsWeekVO = new WorkWeek();
                statisticsWeekVO.setStartDate(monDay);
                statisticsWeekVO.setEndDate(sunDay);
                weekList.add(statisticsWeekVO);
            }
        }
        return weekList;
    }

}
