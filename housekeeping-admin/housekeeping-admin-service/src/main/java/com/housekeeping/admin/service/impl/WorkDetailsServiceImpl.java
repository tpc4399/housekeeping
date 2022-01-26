package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.OrderDetails;
import com.housekeeping.admin.entity.WorkClock;
import com.housekeeping.admin.entity.WorkDetails;
import com.housekeeping.admin.entity.WorkTimeDateVO;
import com.housekeeping.admin.mapper.WorkDetailsMapper;
import com.housekeeping.admin.service.IOrderDetailsService;
import com.housekeeping.admin.service.IWorkDetailsService;
import com.housekeeping.admin.service.WorkClockService;
import com.housekeeping.admin.vo.WorkTimeTableDateVO;
import com.housekeeping.admin.vo.WorkTimeTableVO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author su
 * @Date 2021/4/28 16:11
 */
@Service("workDetailsService")
public class WorkDetailsServiceImpl
        extends ServiceImpl<WorkDetailsMapper, WorkDetails>
        implements IWorkDetailsService {

    @Resource
    private IOrderDetailsService orderDetailsService;
    @Resource
    private WorkClockService workClockService;

    @Override
    public void add(WorkDetails wd) {
        baseMapper.add(wd);
    }

    @Override
    public List<WorkTimeTableDateVO> getWorkTables(List<Long> numbers, LocalDate start, LocalDate end,Integer month) {
        List<WorkTimeTableDateVO> workTimeTableDateVOS = new ArrayList<>();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)){

            //獲取date的訂單編號
            QueryWrapper<WorkDetails> qw = new QueryWrapper<>();
            qw.eq("date",date);
            List<Long> collect = this.list(qw).stream().map(x -> x.getNumber()).collect(Collectors.toList());

            //獲取既是今天的，又是客戶/員工的訂單編號
            collect.retainAll(numbers);

            WorkTimeTableDateVO workTimeTableDateVO = new WorkTimeTableDateVO();
            workTimeTableDateVO.setDate(date);
            workTimeTableDateVO.setWeek(date.getDayOfWeek().getValue());
            workTimeTableDateVO.setIsThisMonth(date.getMonth().getValue() == month);
            workTimeTableDateVO.setIsThisDay(date.equals(LocalDate.now()));
            workTimeTableDateVO.setIsAfter(date.isAfter(LocalDate.now())||date.equals(LocalDate.now()));
            if(CollectionUtils.isEmpty(collect)){
                workTimeTableDateVO.setHasWork(false);
            }else {
                workTimeTableDateVO.setHasWork(true);
            }
            ArrayList<WorkTimeTableVO> workTimeTableVOS = new ArrayList<>();
            for (int i = 0; i < collect.size(); i++) {

                QueryWrapper<WorkDetails> qw2 = new QueryWrapper<>();
                qw2.eq("number",collect.get(i));
                int total = this.count(qw2);
                int hasWork = baseMapper.countWork(collect.get(i));

                QueryWrapper<WorkDetails> wrapper = new QueryWrapper<>();
                wrapper.eq("number",collect.get(i));
                wrapper.eq("date",date);
                List<WorkDetails> list = this.list(wrapper);
                for (int i1 = 0; i1 < list.size(); i1++) {
                    WorkTimeTableVO workTimeTableVO = new WorkTimeTableVO();
                    WorkClock workClock = workClockService.getByWorkId(list.get(i1).getId());
                    workTimeTableVO.setId(workClock.getId());
                    workTimeTableVO.setWorkProgress(hasWork+"/"+total);
                    workTimeTableVO.setTimeSlots(list.get(i1).getTimeSlots());
                    workTimeTableVO.setTimeLength(list.get(i1).getTimeLength());
                    workTimeTableVO.setTimePrice(list.get(i1).getTimePrice());
                    workTimeTableVO.setCanBeOnDuty(list.get(i1).getCanBeOnDuty());
                    workTimeTableVO.setTodayPrice(list.get(i1).getTodayPrice());
                    workTimeTableVO.setOrderDetails(orderDetailsService.getByNumber(list.get(i1).getNumber().toString()));

                    workTimeTableVO.setWorkStatus(workClock.getWorkStatus());
                    workTimeTableVO.setToWorkStatus(workClock.getToWorkStatus());
                    workTimeTableVO.setToWorkTime(workClock.getToWorkTime());
                    workTimeTableVO.setOffWorkStatus(workClock.getOffWorkStatus());
                    workTimeTableVO.setOffWorkTime(workClock.getOffWorkTime());
                    workTimeTableVOS.add(workTimeTableVO);
                }
            }
            workTimeTableVOS.sort(Comparator.comparing(WorkTimeTableVO::getTimeSlots));
            workTimeTableDateVO.setWorkTimeTable(workTimeTableVOS);
            workTimeTableDateVOS.add(workTimeTableDateVO);
        }

        return workTimeTableDateVOS;
    }

    @Override
    public List<WorkTimeDateVO> getWorkTablesByEmp(Integer empId, LocalDate start, LocalDate end) {
        QueryWrapper<OrderDetails> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("employees_id",empId);
        List<Long> numbers = orderDetailsService.list(queryWrapper).stream().map(x -> {
            return x.getNumber();
        }).collect(Collectors.toList());

        List<WorkTimeDateVO> workTimeTableDateVOS = new ArrayList<>();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)){

            //獲取date的訂單編號
            QueryWrapper<WorkDetails> qw = new QueryWrapper<>();
            qw.eq("date",date);
            List<Long> collect = this.list(qw).stream().map(x -> x.getNumber()).collect(Collectors.toList());

            //獲取既是今天的，又是客戶/員工的訂單編號
            collect.retainAll(numbers);

            WorkTimeDateVO workTimeTableDateVO = new WorkTimeDateVO();
            workTimeTableDateVO.setDate(date);

            ArrayList<WorkTimeTableVO> workTimeTableVOS = new ArrayList<>();
            for (int i = 0; i < collect.size(); i++) {

                QueryWrapper<WorkDetails> qw2 = new QueryWrapper<>();
                qw2.eq("number",collect.get(i));
                int total = this.count(qw2);
                int hasWork = baseMapper.countWork(collect.get(i));

                QueryWrapper<WorkDetails> wrapper = new QueryWrapper<>();
                wrapper.eq("number",collect.get(i));
                wrapper.eq("date",date);
                List<WorkDetails> list = this.list(wrapper);
                for (int i1 = 0; i1 < list.size(); i1++) {
                    WorkTimeTableVO workTimeTableVO = new WorkTimeTableVO();
                    WorkClock workClock = workClockService.getByWorkId(list.get(i1).getId());
                    workTimeTableVO.setId(workClock.getId());
                    workTimeTableVO.setWorkProgress(hasWork+"/"+total);
                    workTimeTableVO.setTimeSlots(list.get(i1).getTimeSlots());
                    workTimeTableVO.setTimeLength(list.get(i1).getTimeLength());
                    workTimeTableVO.setTimePrice(list.get(i1).getTimePrice());
                    workTimeTableVO.setCanBeOnDuty(list.get(i1).getCanBeOnDuty());
                    workTimeTableVO.setTodayPrice(list.get(i1).getTodayPrice());
                    workTimeTableVO.setOrderDetails(orderDetailsService.getByNumber(list.get(i1).getNumber().toString()));

                    workTimeTableVO.setWorkStatus(workClock.getWorkStatus());
                    workTimeTableVO.setToWorkStatus(workClock.getToWorkStatus());
                    workTimeTableVO.setToWorkTime(workClock.getToWorkTime());
                    workTimeTableVO.setOffWorkStatus(workClock.getOffWorkStatus());
                    workTimeTableVO.setOffWorkTime(workClock.getOffWorkTime());
                    workTimeTableVOS.add(workTimeTableVO);
                }
            }
            workTimeTableVOS.sort(Comparator.comparing(WorkTimeTableVO::getTimeSlots));
            workTimeTableDateVO.setWorkTimeTable(workTimeTableVOS);
            workTimeTableDateVO.setWorkTotal(workTimeTableVOS.size());
            workTimeTableDateVOS.add(workTimeTableDateVO);
        }

        return workTimeTableDateVOS;

    }

    @Override
    public List<WorkTimeDateVO> getWorkTablesByCom(Integer comId, LocalDate start, LocalDate end) {
        QueryWrapper<OrderDetails> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("company_id",comId);
        List<Long> numbers = orderDetailsService.list(queryWrapper).stream().map(x -> {
            return x.getNumber();
        }).collect(Collectors.toList());

        List<WorkTimeDateVO> workTimeTableDateVOS = new ArrayList<>();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)){

            //獲取date的訂單編號
            QueryWrapper<WorkDetails> qw = new QueryWrapper<>();
            qw.eq("date",date);
            List<Long> collect = this.list(qw).stream().map(x -> x.getNumber()).collect(Collectors.toList());

            //獲取既是今天的，又是客戶/員工的訂單編號
            collect.retainAll(numbers);

            WorkTimeDateVO workTimeTableDateVO = new WorkTimeDateVO();
            workTimeTableDateVO.setDate(date);

            ArrayList<WorkTimeTableVO> workTimeTableVOS = new ArrayList<>();
            for (int i = 0; i < collect.size(); i++) {

                QueryWrapper<WorkDetails> qw2 = new QueryWrapper<>();
                qw2.eq("number",collect.get(i));
                int total = this.count(qw2);
                int hasWork = baseMapper.countWork(collect.get(i));

                QueryWrapper<WorkDetails> wrapper = new QueryWrapper<>();
                wrapper.eq("number",collect.get(i));
                wrapper.eq("date",date);
                List<WorkDetails> list = this.list(wrapper);
                for (int i1 = 0; i1 < list.size(); i1++) {
                    WorkTimeTableVO workTimeTableVO = new WorkTimeTableVO();
                    WorkClock workClock = workClockService.getByWorkId(list.get(i1).getId());
                    workTimeTableVO.setId(workClock.getId());
                    workTimeTableVO.setWorkProgress(hasWork+"/"+total);
                    workTimeTableVO.setTimeSlots(list.get(i1).getTimeSlots());
                    workTimeTableVO.setTimeLength(list.get(i1).getTimeLength());
                    workTimeTableVO.setTimePrice(list.get(i1).getTimePrice());
                    workTimeTableVO.setCanBeOnDuty(list.get(i1).getCanBeOnDuty());
                    workTimeTableVO.setTodayPrice(list.get(i1).getTodayPrice());
                    workTimeTableVO.setOrderDetails(orderDetailsService.getByNumber(list.get(i1).getNumber().toString()));

                    workTimeTableVO.setWorkStatus(workClock.getWorkStatus());
                    workTimeTableVO.setToWorkStatus(workClock.getToWorkStatus());
                    workTimeTableVO.setToWorkTime(workClock.getToWorkTime());
                    workTimeTableVO.setOffWorkStatus(workClock.getOffWorkStatus());
                    workTimeTableVO.setOffWorkTime(workClock.getOffWorkTime());
                    workTimeTableVOS.add(workTimeTableVO);
                }
            }
            workTimeTableVOS.sort(Comparator.comparing(WorkTimeTableVO::getTimeSlots));
            workTimeTableDateVO.setWorkTimeTable(workTimeTableVOS);
            workTimeTableDateVO.setWorkTotal(workTimeTableVOS.size());
            workTimeTableDateVOS.add(workTimeTableDateVO);
        }

        return workTimeTableDateVOS;
    }
}
