package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.housekeeping.admin.entity.OrderDetails;
import com.housekeeping.admin.entity.WorkMonth;
import com.housekeeping.admin.entity.WorkTimeDateVO;
import com.housekeeping.admin.entity.WorkWeek;
import com.housekeeping.admin.service.IOrderDetailsService;
import com.housekeeping.admin.service.IStatisticsService;
import com.housekeeping.admin.service.IWorkDetailsService;
import com.housekeeping.admin.vo.OrderDetailsVO;
import com.housekeeping.admin.vo.StatisticsWeekVO;
import com.housekeeping.admin.vo.StatisticsYearVO;
import com.housekeeping.common.utils.CommonConstants;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TimeUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service("statisticsService")
public class IStatisticsServiceImpl implements IStatisticsService {

    @Resource
    private IOrderDetailsService orderDetailsService;
    @Resource
    private IWorkDetailsService workDetailsService;

    @Override
    public R empOrder(Integer empId, Integer type, Integer year, Integer month, Integer day) {
        if(type.equals(3)){
            LocalDate date = LocalDate.of(year, month, day);
            QueryWrapper qw = new QueryWrapper();
            qw.eq("employees_id", empId);
            qw.like("start_date_time",date);
            List<OrderDetails> ods = orderDetailsService.list(qw);
            BigDecimal priceTotal = new BigDecimal(0);
            for (OrderDetails od : ods) {
                priceTotal = priceTotal.add(od.getPriceBeforeDiscount());
            }
            OrderDetailsVO orderDetailsVO = new OrderDetailsVO();
            orderDetailsVO.setDate(date);
            orderDetailsVO.setOrderDetails(ods);
            orderDetailsVO.setPriceTotal(priceTotal);
            orderDetailsVO.setOrderTotal(ods.size());
            return R.ok(orderDetailsVO);
        }
        if(type.equals(1)){
            ArrayList<OrderDetailsVO> orderDetailsVOS = new ArrayList<>();
            LocalDate thisMonthFirstDay = LocalDate.of(year, month, 1);//這個月第一天
            LocalDate thisMonthLastDay = thisMonthFirstDay.plusMonths(1).plusDays(-1);//這個月最后一天
            for (LocalDate date = thisMonthFirstDay; !date.isAfter(thisMonthLastDay); date = date.plusDays(1)){
                QueryWrapper qw = new QueryWrapper();
                qw.eq("employees_id", empId);
                qw.like("start_date_time",date);
                List<OrderDetails> ods = orderDetailsService.list(qw);
                BigDecimal priceTotal = new BigDecimal(0);
                for (OrderDetails od : ods) {
                    priceTotal = priceTotal.add(od.getPriceBeforeDiscount());
                }
                OrderDetailsVO orderDetailsVO = new OrderDetailsVO();
                orderDetailsVO.setDate(date);
                orderDetailsVO.setOrderDetails(ods);
                orderDetailsVO.setPriceTotal(priceTotal);
                orderDetailsVO.setOrderTotal(ods.size());
                orderDetailsVOS.add(orderDetailsVO);
            }
            return R.ok(orderDetailsVOS);
        }
        if(type.equals(0)){
            ArrayList<StatisticsYearVO> statisticsYearVOS = new ArrayList<>();
            for (int i = 1; i <= 12 ; i++) {
                LocalDate date = LocalDate.of(year, i, 1);
                String substring = date.toString().substring(0, 7);
                QueryWrapper qw = new QueryWrapper();
                qw.eq("employees_id", empId);
                qw.like("start_date_time",substring);
                List<OrderDetails> ods = orderDetailsService.list(qw);
                BigDecimal priceTotal = new BigDecimal(0);
                for (OrderDetails od : ods) {
                    priceTotal = priceTotal.add(od.getPriceBeforeDiscount());
                }
                StatisticsYearVO statisticsYearVO = new StatisticsYearVO();
                statisticsYearVO.setMonth(substring);
                statisticsYearVO.setOrderTotal(ods.size());
                statisticsYearVO.setPriceTotal(priceTotal);
                statisticsYearVOS.add(statisticsYearVO);
            }
            return R.ok(statisticsYearVOS);
        }
        if(type.equals(2)){
            QueryWrapper<OrderDetails> wrapper = new QueryWrapper<>();
            wrapper.eq("employees_id",empId);
            wrapper.orderByAsc("start_date_time");
            List<OrderDetails> list = orderDetailsService.list(wrapper);
            if(CollectionUtils.isEmpty(list)){
                return R.ok(null);
            }
            String localDate1 = list.get(0).getStartDateTime().toLocalDate().toString();
            String localDate2 = list.get(list.size() - 1).getStartDateTime().toLocalDate().toString();
            org.joda.time.LocalDate startDate = new org.joda.time.LocalDate(localDate1);
            org.joda.time.LocalDate endDate = new org.joda.time.LocalDate(localDate2);
            List<StatisticsWeekVO> statisticsWeekVOS = TimeUtils.converWeekList(startDate, endDate);

            for (StatisticsWeekVO statisticsWeekVO : statisticsWeekVOS) {

                LocalDate startDate2 = LocalDate.parse(statisticsWeekVO.getStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                LocalDate endDate2 = LocalDate.parse(statisticsWeekVO.getEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                BigDecimal priceTotal = new BigDecimal(0);
                Integer totalTotal = 0;
                for (LocalDate date = startDate2; !date.isAfter(endDate2); date = date.plusDays(1)){
                    QueryWrapper qw = new QueryWrapper();
                    qw.eq("employees_id", empId);
                    qw.like("start_date_time",date);
                    List<OrderDetails> ods = orderDetailsService.list(qw);
                    for (OrderDetails od : ods) {
                        priceTotal = priceTotal.add(od.getPriceBeforeDiscount());
                        totalTotal = totalTotal + 1;
                    }
                }
                statisticsWeekVO.setOrderTotal(totalTotal);
                statisticsWeekVO.setPriceTotal(priceTotal);
            }
            return R.ok(statisticsWeekVOS);
        }
        return R.ok();
    }

    @Override
    public R empOrderByDate(Integer empId, String startDate, String endDate) {
        LocalDate startDate2 = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate endDate2 = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        ArrayList<OrderDetailsVO> orderDetailsVOS = new ArrayList<>();
        for (LocalDate date = startDate2; !date.isAfter(endDate2); date = date.plusDays(1)){
            QueryWrapper qw = new QueryWrapper();
            qw.eq("employees_id", empId);
            qw.like("start_date_time",date);
            List<OrderDetails> ods = orderDetailsService.list(qw);
            BigDecimal priceTotal = new BigDecimal(0);
            for (OrderDetails od : ods) {
                priceTotal = priceTotal.add(od.getPriceBeforeDiscount());
            }
            OrderDetailsVO orderDetailsVO = new OrderDetailsVO();
            orderDetailsVO.setDate(date);
            orderDetailsVO.setOrderDetails(ods);
            orderDetailsVO.setPriceTotal(priceTotal);
            orderDetailsVO.setOrderTotal(ods.size());
            orderDetailsVOS.add(orderDetailsVO);
        }
        return R.ok(orderDetailsVOS);
    }

    @Override
    public R comOrder(Integer comId, Integer type, Integer year, Integer month, Integer day) {
        if(type.equals(3)){
            LocalDate date = LocalDate.of(year, month, day);
            QueryWrapper qw = new QueryWrapper();
            qw.eq("company_id", comId);
            qw.like("start_date_time",date);
            List<OrderDetails> ods = orderDetailsService.list(qw);
            BigDecimal priceTotal = new BigDecimal(0);
            for (OrderDetails od : ods) {
                priceTotal = priceTotal.add(od.getPriceBeforeDiscount());
            }
            OrderDetailsVO orderDetailsVO = new OrderDetailsVO();
            orderDetailsVO.setDate(date);
            orderDetailsVO.setOrderDetails(ods);
            orderDetailsVO.setPriceTotal(priceTotal);
            orderDetailsVO.setOrderTotal(ods.size());
            return R.ok(orderDetailsVO);
        }
        if(type.equals(1)){
            ArrayList<OrderDetailsVO> orderDetailsVOS = new ArrayList<>();
            LocalDate thisMonthFirstDay = LocalDate.of(year, month, 1);//這個月第一天
            LocalDate thisMonthLastDay = thisMonthFirstDay.plusMonths(1).plusDays(-1);//這個月最后一天
            for (LocalDate date = thisMonthFirstDay; !date.isAfter(thisMonthLastDay); date = date.plusDays(1)){
                QueryWrapper qw = new QueryWrapper();
                qw.eq("company_id", comId);
                qw.like("start_date_time",date);
                List<OrderDetails> ods = orderDetailsService.list(qw);
                BigDecimal priceTotal = new BigDecimal(0);
                for (OrderDetails od : ods) {
                    priceTotal = priceTotal.add(od.getPriceBeforeDiscount());
                }
                OrderDetailsVO orderDetailsVO = new OrderDetailsVO();
                orderDetailsVO.setDate(date);
                orderDetailsVO.setOrderDetails(ods);
                orderDetailsVO.setPriceTotal(priceTotal);
                orderDetailsVO.setOrderTotal(ods.size());
                orderDetailsVOS.add(orderDetailsVO);
            }
            return R.ok(orderDetailsVOS);
        }
        if(type.equals(0)){
            ArrayList<StatisticsYearVO> statisticsYearVOS = new ArrayList<>();
            for (int i = 1; i <= 12 ; i++) {
                LocalDate date = LocalDate.of(year, i, 1);
                String substring = date.toString().substring(0, 7);
                QueryWrapper qw = new QueryWrapper();
                qw.eq("company_id", comId);
                qw.like("start_date_time",substring);
                List<OrderDetails> ods = orderDetailsService.list(qw);
                BigDecimal priceTotal = new BigDecimal(0);
                for (OrderDetails od : ods) {
                    priceTotal = priceTotal.add(od.getPriceBeforeDiscount());
                }
                StatisticsYearVO statisticsYearVO = new StatisticsYearVO();
                statisticsYearVO.setMonth(substring);
                statisticsYearVO.setOrderTotal(ods.size());
                statisticsYearVO.setPriceTotal(priceTotal);
                statisticsYearVOS.add(statisticsYearVO);
            }
            return R.ok(statisticsYearVOS);
        }
        if(type.equals(2)){
            QueryWrapper<OrderDetails> wrapper = new QueryWrapper<>();
            wrapper.eq("company_id", comId);
            wrapper.orderByAsc("start_date_time");
            List<OrderDetails> list = orderDetailsService.list(wrapper);
            if(CollectionUtils.isEmpty(list)){
                return R.ok(null);
            }
            String localDate1 = list.get(0).getStartDateTime().toLocalDate().toString();
            String localDate2 = list.get(list.size() - 1).getStartDateTime().toLocalDate().toString();
            org.joda.time.LocalDate startDate = new org.joda.time.LocalDate(localDate1);
            org.joda.time.LocalDate endDate = new org.joda.time.LocalDate(localDate2);
            List<StatisticsWeekVO> statisticsWeekVOS = TimeUtils.converWeekList(startDate, endDate);

            for (StatisticsWeekVO statisticsWeekVO : statisticsWeekVOS) {

                LocalDate startDate2 = LocalDate.parse(statisticsWeekVO.getStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                LocalDate endDate2 = LocalDate.parse(statisticsWeekVO.getEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                BigDecimal priceTotal = new BigDecimal(0);
                Integer totalTotal = 0;
                for (LocalDate date = startDate2; !date.isAfter(endDate2); date = date.plusDays(1)){
                    QueryWrapper qw = new QueryWrapper();
                    qw.eq("company_id", comId);
                    qw.like("start_date_time",date);
                    List<OrderDetails> ods = orderDetailsService.list(qw);
                    for (OrderDetails od : ods) {
                        priceTotal = priceTotal.add(od.getPriceBeforeDiscount());
                        totalTotal = totalTotal + 1;
                    }
                }
                statisticsWeekVO.setOrderTotal(totalTotal);
                statisticsWeekVO.setPriceTotal(priceTotal);
            }
            return R.ok(statisticsWeekVOS);
        }
        return R.ok();
    }

    @Override
    public R comOrderByDate(Integer comId, String startDate, String endDate) {
        LocalDate startDate2 = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate endDate2 = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        ArrayList<OrderDetailsVO> orderDetailsVOS = new ArrayList<>();
        for (LocalDate date = startDate2; !date.isAfter(endDate2); date = date.plusDays(1)){
            QueryWrapper qw = new QueryWrapper();
            qw.eq("company_id", comId);
            qw.like("start_date_time",date);
            List<OrderDetails> ods = orderDetailsService.list(qw);
            BigDecimal priceTotal = new BigDecimal(0);
            for (OrderDetails od : ods) {
                priceTotal = priceTotal.add(od.getPriceBeforeDiscount());
            }
            OrderDetailsVO orderDetailsVO = new OrderDetailsVO();
            orderDetailsVO.setDate(date);
            orderDetailsVO.setOrderDetails(ods);
            orderDetailsVO.setPriceTotal(priceTotal);
            orderDetailsVO.setOrderTotal(ods.size());
            orderDetailsVOS.add(orderDetailsVO);
        }
        return R.ok(orderDetailsVOS);
    }

    @Override
    public R empIncome(Integer empId, Integer type, Integer year, Integer month, Integer day) {
        if(type.equals(3)){
            LocalDate date = LocalDate.of(year, month, day);
            QueryWrapper qw = new QueryWrapper();
            qw.eq("employees_id", empId);
            qw.like("start_date_time",date);
            qw.in("order_state", CommonConstants.ORDER_STATE_TO_BE_EVALUATED,CommonConstants.ORDER_STATE_COMPLETED);
            List<OrderDetails> ods = orderDetailsService.list(qw);
            BigDecimal priceTotal = new BigDecimal(0);
            for (OrderDetails od : ods) {
                priceTotal = priceTotal.add(od.getPriceBeforeDiscount());
            }
            OrderDetailsVO orderDetailsVO = new OrderDetailsVO();
            orderDetailsVO.setDate(date);
            orderDetailsVO.setOrderDetails(ods);
            orderDetailsVO.setPriceTotal(priceTotal);
            orderDetailsVO.setOrderTotal(ods.size());
            return R.ok(orderDetailsVO);
        }
        if(type.equals(1)){
            ArrayList<OrderDetailsVO> orderDetailsVOS = new ArrayList<>();
            LocalDate thisMonthFirstDay = LocalDate.of(year, month, 1);//這個月第一天
            LocalDate thisMonthLastDay = thisMonthFirstDay.plusMonths(1).plusDays(-1);//這個月最后一天
            for (LocalDate date = thisMonthFirstDay; !date.isAfter(thisMonthLastDay); date = date.plusDays(1)){
                QueryWrapper qw = new QueryWrapper();
                qw.eq("employees_id", empId);
                qw.like("start_date_time",date);
                qw.in("order_state", CommonConstants.ORDER_STATE_TO_BE_EVALUATED,CommonConstants.ORDER_STATE_COMPLETED);
                List<OrderDetails> ods = orderDetailsService.list(qw);
                BigDecimal priceTotal = new BigDecimal(0);
                for (OrderDetails od : ods) {
                    priceTotal = priceTotal.add(od.getPriceBeforeDiscount());
                }
                OrderDetailsVO orderDetailsVO = new OrderDetailsVO();
                orderDetailsVO.setDate(date);
                orderDetailsVO.setOrderDetails(ods);
                orderDetailsVO.setPriceTotal(priceTotal);
                orderDetailsVO.setOrderTotal(ods.size());
                orderDetailsVOS.add(orderDetailsVO);
            }
            return R.ok(orderDetailsVOS);
        }
        if(type.equals(0)){
            ArrayList<StatisticsYearVO> statisticsYearVOS = new ArrayList<>();
            for (int i = 1; i <= 12 ; i++) {
                LocalDate date = LocalDate.of(year, i, 1);
                String substring = date.toString().substring(0, 7);
                QueryWrapper qw = new QueryWrapper();
                qw.eq("employees_id", empId);
                qw.like("start_date_time",substring);
                qw.in("order_state", CommonConstants.ORDER_STATE_TO_BE_EVALUATED,CommonConstants.ORDER_STATE_COMPLETED);
                List<OrderDetails> ods = orderDetailsService.list(qw);
                BigDecimal priceTotal = new BigDecimal(0);
                for (OrderDetails od : ods) {
                    priceTotal = priceTotal.add(od.getPriceBeforeDiscount());
                }
                StatisticsYearVO statisticsYearVO = new StatisticsYearVO();
                statisticsYearVO.setMonth(substring);
                statisticsYearVO.setOrderTotal(ods.size());
                statisticsYearVO.setPriceTotal(priceTotal);
                statisticsYearVOS.add(statisticsYearVO);
            }
            return R.ok(statisticsYearVOS);
        }
        if(type.equals(2)){
            QueryWrapper<OrderDetails> wrapper = new QueryWrapper<>();
            wrapper.eq("employees_id",empId);
            wrapper.in("order_state", CommonConstants.ORDER_STATE_TO_BE_EVALUATED,CommonConstants.ORDER_STATE_COMPLETED);
            wrapper.orderByAsc("start_date_time");
            List<OrderDetails> list = orderDetailsService.list(wrapper);
            if(CollectionUtils.isEmpty(list)){
                return R.ok(null);
            }
            String localDate1 = list.get(0).getStartDateTime().toLocalDate().toString();
            String localDate2 = list.get(list.size() - 1).getStartDateTime().toLocalDate().toString();
            org.joda.time.LocalDate startDate = new org.joda.time.LocalDate(localDate1);
            org.joda.time.LocalDate endDate = new org.joda.time.LocalDate(localDate2);
            List<StatisticsWeekVO> statisticsWeekVOS = TimeUtils.converWeekList(startDate, endDate);

            for (StatisticsWeekVO statisticsWeekVO : statisticsWeekVOS) {

                LocalDate startDate2 = LocalDate.parse(statisticsWeekVO.getStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                LocalDate endDate2 = LocalDate.parse(statisticsWeekVO.getEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                BigDecimal priceTotal = new BigDecimal(0);
                Integer totalTotal = 0;
                for (LocalDate date = startDate2; !date.isAfter(endDate2); date = date.plusDays(1)){
                    QueryWrapper qw = new QueryWrapper();
                    qw.eq("employees_id", empId);
                    qw.like("start_date_time",date);
                    qw.in("order_state", CommonConstants.ORDER_STATE_TO_BE_EVALUATED,CommonConstants.ORDER_STATE_COMPLETED);
                    List<OrderDetails> ods = orderDetailsService.list(qw);
                    for (OrderDetails od : ods) {
                        priceTotal = priceTotal.add(od.getPriceBeforeDiscount());
                        totalTotal = totalTotal + 1;
                    }
                }
                statisticsWeekVO.setOrderTotal(totalTotal);
                statisticsWeekVO.setPriceTotal(priceTotal);
            }
            return R.ok(statisticsWeekVOS);
        }
        return R.ok();
    }

    @Override
    public R empIncomeByDate(Integer empId, String startDate, String endDate) {
        LocalDate startDate2 = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate endDate2 = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        ArrayList<OrderDetailsVO> orderDetailsVOS = new ArrayList<>();
        for (LocalDate date = startDate2; !date.isAfter(endDate2); date = date.plusDays(1)){
            QueryWrapper qw = new QueryWrapper();
            qw.eq("employees_id", empId);
            qw.like("start_date_time",date);
            qw.in("order_state", CommonConstants.ORDER_STATE_TO_BE_EVALUATED,CommonConstants.ORDER_STATE_COMPLETED);
            List<OrderDetails> ods = orderDetailsService.list(qw);
            BigDecimal priceTotal = new BigDecimal(0);
            for (OrderDetails od : ods) {
                priceTotal = priceTotal.add(od.getPriceBeforeDiscount());
            }
            OrderDetailsVO orderDetailsVO = new OrderDetailsVO();
            orderDetailsVO.setDate(date);
            orderDetailsVO.setOrderDetails(ods);
            orderDetailsVO.setPriceTotal(priceTotal);
            orderDetailsVO.setOrderTotal(ods.size());
            orderDetailsVOS.add(orderDetailsVO);
        }
        return R.ok(orderDetailsVOS);
    }

    @Override
    public R comIncome(Integer comId, Integer type, Integer year, Integer month, Integer day) {
        if(type.equals(3)){
            LocalDate date = LocalDate.of(year, month, day);
            QueryWrapper qw = new QueryWrapper();
            qw.eq("company_id", comId);
            qw.like("start_date_time",date);
            qw.in("order_state", CommonConstants.ORDER_STATE_TO_BE_EVALUATED,CommonConstants.ORDER_STATE_COMPLETED);
            List<OrderDetails> ods = orderDetailsService.list(qw);
            BigDecimal priceTotal = new BigDecimal(0);
            for (OrderDetails od : ods) {
                priceTotal = priceTotal.add(od.getPriceBeforeDiscount());
            }
            OrderDetailsVO orderDetailsVO = new OrderDetailsVO();
            orderDetailsVO.setDate(date);
            orderDetailsVO.setOrderDetails(ods);
            orderDetailsVO.setPriceTotal(priceTotal);
            orderDetailsVO.setOrderTotal(ods.size());
            return R.ok(orderDetailsVO);
        }
        if(type.equals(1)){
            ArrayList<OrderDetailsVO> orderDetailsVOS = new ArrayList<>();
            LocalDate thisMonthFirstDay = LocalDate.of(year, month, 1);//這個月第一天
            LocalDate thisMonthLastDay = thisMonthFirstDay.plusMonths(1).plusDays(-1);//這個月最后一天
            for (LocalDate date = thisMonthFirstDay; !date.isAfter(thisMonthLastDay); date = date.plusDays(1)){
                QueryWrapper qw = new QueryWrapper();
                qw.eq("company_id", comId);
                qw.like("start_date_time",date);
                qw.in("order_state", CommonConstants.ORDER_STATE_TO_BE_EVALUATED,CommonConstants.ORDER_STATE_COMPLETED);
                List<OrderDetails> ods = orderDetailsService.list(qw);
                BigDecimal priceTotal = new BigDecimal(0);
                for (OrderDetails od : ods) {
                    priceTotal = priceTotal.add(od.getPriceBeforeDiscount());
                }
                OrderDetailsVO orderDetailsVO = new OrderDetailsVO();
                orderDetailsVO.setDate(date);
                orderDetailsVO.setOrderDetails(ods);
                orderDetailsVO.setPriceTotal(priceTotal);
                orderDetailsVO.setOrderTotal(ods.size());
                orderDetailsVOS.add(orderDetailsVO);
            }
            return R.ok(orderDetailsVOS);
        }
        if(type.equals(0)){
            ArrayList<StatisticsYearVO> statisticsYearVOS = new ArrayList<>();
            for (int i = 1; i <= 12 ; i++) {
                LocalDate date = LocalDate.of(year, i, 1);
                String substring = date.toString().substring(0, 7);
                QueryWrapper qw = new QueryWrapper();
                qw.eq("company_id", comId);
                qw.like("start_date_time",substring);
                qw.in("order_state", CommonConstants.ORDER_STATE_TO_BE_EVALUATED,CommonConstants.ORDER_STATE_COMPLETED);
                List<OrderDetails> ods = orderDetailsService.list(qw);
                BigDecimal priceTotal = new BigDecimal(0);
                for (OrderDetails od : ods) {
                    priceTotal = priceTotal.add(od.getPriceBeforeDiscount());
                }
                StatisticsYearVO statisticsYearVO = new StatisticsYearVO();
                statisticsYearVO.setMonth(substring);
                statisticsYearVO.setOrderTotal(ods.size());
                statisticsYearVO.setPriceTotal(priceTotal);
                statisticsYearVOS.add(statisticsYearVO);
            }
            return R.ok(statisticsYearVOS);
        }
        if(type.equals(2)){
            QueryWrapper<OrderDetails> wrapper = new QueryWrapper<>();
            wrapper.eq("company_id", comId);
            wrapper.in("order_state", CommonConstants.ORDER_STATE_TO_BE_EVALUATED,CommonConstants.ORDER_STATE_COMPLETED);
            wrapper.orderByAsc("start_date_time");
            List<OrderDetails> list = orderDetailsService.list(wrapper);
            if(CollectionUtils.isEmpty(list)){
                return R.ok(null);
            }
            String localDate1 = list.get(0).getStartDateTime().toLocalDate().toString();
            String localDate2 = list.get(list.size() - 1).getStartDateTime().toLocalDate().toString();
            org.joda.time.LocalDate startDate = new org.joda.time.LocalDate(localDate1);
            org.joda.time.LocalDate endDate = new org.joda.time.LocalDate(localDate2);
            List<StatisticsWeekVO> statisticsWeekVOS = TimeUtils.converWeekList(startDate, endDate);

            for (StatisticsWeekVO statisticsWeekVO : statisticsWeekVOS) {

                LocalDate startDate2 = LocalDate.parse(statisticsWeekVO.getStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                LocalDate endDate2 = LocalDate.parse(statisticsWeekVO.getEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                BigDecimal priceTotal = new BigDecimal(0);
                Integer totalTotal = 0;
                for (LocalDate date = startDate2; !date.isAfter(endDate2); date = date.plusDays(1)){
                    QueryWrapper qw = new QueryWrapper();
                    qw.eq("company_id", comId);
                    qw.like("start_date_time",date);
                    qw.in("order_state", CommonConstants.ORDER_STATE_TO_BE_EVALUATED,CommonConstants.ORDER_STATE_COMPLETED);
                    List<OrderDetails> ods = orderDetailsService.list(qw);
                    for (OrderDetails od : ods) {
                        priceTotal = priceTotal.add(od.getPriceBeforeDiscount());
                        totalTotal = totalTotal + 1;
                    }
                }
                statisticsWeekVO.setOrderTotal(totalTotal);
                statisticsWeekVO.setPriceTotal(priceTotal);
            }
            return R.ok(statisticsWeekVOS);
        }
        return R.ok();
    }

    @Override
    public R comIncomeByDate(Integer comId, String startDate, String endDate) {
        LocalDate startDate2 = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate endDate2 = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        ArrayList<OrderDetailsVO> orderDetailsVOS = new ArrayList<>();
        for (LocalDate date = startDate2; !date.isAfter(endDate2); date = date.plusDays(1)){
            QueryWrapper qw = new QueryWrapper();
            qw.eq("company_id", comId);
            qw.like("start_date_time",date);
            qw.in("order_state", CommonConstants.ORDER_STATE_TO_BE_EVALUATED,CommonConstants.ORDER_STATE_COMPLETED);
            List<OrderDetails> ods = orderDetailsService.list(qw);
            BigDecimal priceTotal = new BigDecimal(0);
            for (OrderDetails od : ods) {
                priceTotal = priceTotal.add(od.getPriceBeforeDiscount());
            }
            OrderDetailsVO orderDetailsVO = new OrderDetailsVO();
            orderDetailsVO.setDate(date);
            orderDetailsVO.setOrderDetails(ods);
            orderDetailsVO.setPriceTotal(priceTotal);
            orderDetailsVO.setOrderTotal(ods.size());
            orderDetailsVOS.add(orderDetailsVO);
        }
        return R.ok(orderDetailsVOS);
    }


    @Override
    public R empWork(Integer empId, Integer type, Integer year, Integer month, Integer day) {
        if(type.equals(3)){
            LocalDate date = LocalDate.of(year, month, day);
            List<WorkTimeDateVO> workTablesByEmp = workDetailsService.getWorkTablesByEmp(empId, date, date);
            return R.ok(workTablesByEmp);
        }
        if(type.equals(1)){
            LocalDate thisMonthFirstDay = LocalDate.of(year, month, 1);//這個月第一天
            LocalDate thisMonthLastDay = thisMonthFirstDay.plusMonths(1).plusDays(-1);//這個月最后一天
            List<WorkTimeDateVO> workTablesByEmp = workDetailsService.getWorkTablesByEmp(empId, thisMonthFirstDay, thisMonthLastDay);
            return R.ok(workTablesByEmp);
        }
        if(type.equals(0)){
            ArrayList<WorkMonth> statisticsYearVOS = new ArrayList<>();
            for (int i = 1; i <= 12 ; i++) {
                LocalDate thisMonthFirstDay = LocalDate.of(year, i, 1);//這個月第一天
                LocalDate thisMonthLastDay = thisMonthFirstDay.plusMonths(1).plusDays(-1);//這個月最后一天
                List<WorkTimeDateVO> workTablesByEmp = workDetailsService.getWorkTablesByEmp(empId, thisMonthFirstDay, thisMonthLastDay);
                WorkMonth workMonth = new WorkMonth();
                workMonth.setMonth(thisMonthFirstDay.toString().substring(0,7));
                Integer workTotal = 0;
                for (WorkTimeDateVO workTimeDateVO : workTablesByEmp) {
                    workTotal = workTotal + workTimeDateVO.getWorkTotal();
                }
                workMonth.setWorkTotal(workTotal);
                statisticsYearVOS.add(workMonth);
            }
            return R.ok(statisticsYearVOS);
        }
        if(type.equals(2)){
            QueryWrapper<OrderDetails> wrapper = new QueryWrapper<>();
            wrapper.eq("employees_id",empId);
            wrapper.orderByAsc("start_date_time");
            List<OrderDetails> list = orderDetailsService.list(wrapper);
            if(CollectionUtils.isEmpty(list)){
                return R.ok(null);
            }
            String localDate1 = list.get(0).getStartDateTime().toLocalDate().toString();
            String localDate2 = list.get(list.size() - 1).getStartDateTime().toLocalDate().toString();
            org.joda.time.LocalDate startDate = new org.joda.time.LocalDate(localDate1);
            org.joda.time.LocalDate endDate = new org.joda.time.LocalDate(localDate2);
            List<WorkWeek> statisticsWeekVOS = TimeUtils.converWeek(startDate, endDate);

            for (WorkWeek statisticsWeekVO : statisticsWeekVOS) {

                LocalDate startDate2 = LocalDate.parse(statisticsWeekVO.getStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                LocalDate endDate2 = LocalDate.parse(statisticsWeekVO.getEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                List<WorkTimeDateVO> workTablesByEmp = workDetailsService.getWorkTablesByEmp(empId, startDate2, endDate2);
                Integer workTotal = 0;
                for (WorkTimeDateVO workTimeDateVO : workTablesByEmp) {
                    workTotal = workTotal + workTimeDateVO.getWorkTotal();
                }
                statisticsWeekVO.setWorkTotal(workTotal);
            }
            return R.ok(statisticsWeekVOS);
        }
        return R.ok();
    }

    @Override
    public R empWorkByDate(Integer empId, String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        List<WorkTimeDateVO> workTablesByEmp = workDetailsService.getWorkTablesByEmp(empId, start, end);
        return R.ok(workTablesByEmp);
    }

    @Override
    public R comWork(Integer comId, Integer type, Integer year, Integer month, Integer day) {
        if(type.equals(3)){
            LocalDate date = LocalDate.of(year, month, day);
            List<WorkTimeDateVO> workTablesByEmp = workDetailsService.getWorkTablesByCom(comId, date, date);
            return R.ok(workTablesByEmp);
        }
        if(type.equals(1)){
            LocalDate thisMonthFirstDay = LocalDate.of(year, month, 1);//這個月第一天
            LocalDate thisMonthLastDay = thisMonthFirstDay.plusMonths(1).plusDays(-1);//這個月最后一天
            List<WorkTimeDateVO> workTablesByEmp = workDetailsService.getWorkTablesByCom(comId, thisMonthFirstDay, thisMonthLastDay);
            return R.ok(workTablesByEmp);
        }
        if(type.equals(0)){
            ArrayList<WorkMonth> statisticsYearVOS = new ArrayList<>();
            for (int i = 1; i <= 12 ; i++) {
                LocalDate thisMonthFirstDay = LocalDate.of(year, i, 1);//這個月第一天
                LocalDate thisMonthLastDay = thisMonthFirstDay.plusMonths(1).plusDays(-1);//這個月最后一天
                List<WorkTimeDateVO> workTablesByEmp = workDetailsService.getWorkTablesByCom(comId, thisMonthFirstDay, thisMonthLastDay);
                WorkMonth workMonth = new WorkMonth();
                workMonth.setMonth(thisMonthFirstDay.toString().substring(0,7));
                Integer workTotal = 0;
                for (WorkTimeDateVO workTimeDateVO : workTablesByEmp) {
                    workTotal = workTotal + workTimeDateVO.getWorkTotal();
                }
                workMonth.setWorkTotal(workTotal);
                statisticsYearVOS.add(workMonth);
            }
            return R.ok(statisticsYearVOS);
        }
        if(type.equals(2)){
            QueryWrapper<OrderDetails> wrapper = new QueryWrapper<>();
            wrapper.eq("company_id",comId);
            wrapper.orderByAsc("start_date_time");
            List<OrderDetails> list = orderDetailsService.list(wrapper);
            if(CollectionUtils.isEmpty(list)){
                return R.ok(null);
            }
            String localDate1 = list.get(0).getStartDateTime().toLocalDate().toString();
            String localDate2 = list.get(list.size() - 1).getStartDateTime().toLocalDate().toString();
            org.joda.time.LocalDate startDate = new org.joda.time.LocalDate(localDate1);
            org.joda.time.LocalDate endDate = new org.joda.time.LocalDate(localDate2);
            List<WorkWeek> statisticsWeekVOS = TimeUtils.converWeek(startDate, endDate);

            for (WorkWeek statisticsWeekVO : statisticsWeekVOS) {

                LocalDate startDate2 = LocalDate.parse(statisticsWeekVO.getStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                LocalDate endDate2 = LocalDate.parse(statisticsWeekVO.getEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                List<WorkTimeDateVO> workTablesByEmp = workDetailsService.getWorkTablesByCom(comId, startDate2, endDate2);
                Integer workTotal = 0;
                for (WorkTimeDateVO workTimeDateVO : workTablesByEmp) {
                    workTotal = workTotal + workTimeDateVO.getWorkTotal();
                }
                statisticsWeekVO.setWorkTotal(workTotal);
            }
            return R.ok(statisticsWeekVOS);
        }
        return R.ok();
    }

    @Override
    public R comWorkByDate(Integer comId, String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        List<WorkTimeDateVO> workTablesByCom = workDetailsService.getWorkTablesByCom(comId, start, end);
        return R.ok(workTablesByCom);
    }

}
