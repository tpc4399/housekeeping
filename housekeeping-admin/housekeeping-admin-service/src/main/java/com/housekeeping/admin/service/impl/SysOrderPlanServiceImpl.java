package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.SysOrderPlanDTO;
import com.housekeeping.admin.entity.SysOrder;
import com.housekeeping.admin.entity.SysOrderPlan;
import com.housekeeping.admin.mapper.SysOrderPlanMapper;
import com.housekeeping.admin.service.ISysOrderPlanService;
import com.housekeeping.admin.service.ISysOrderService;
import com.housekeeping.admin.vo.RulesMonthlyVo;
import com.housekeeping.admin.vo.TimeSlotVo;
import com.housekeeping.common.entity.PeriodOfTime;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.OptionalBean;
import com.housekeeping.common.utils.R;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Author su
 * @create 2020/11/16 14:10
 */
@Service("sysOrderPlanService")
public class SysOrderPlanServiceImpl extends ServiceImpl<SysOrderPlanMapper, SysOrderPlan> implements ISysOrderPlanService {

    @Resource
    private ISysOrderService sysOrderService;
    /***
     * 顾客需求发布：先创建订单、再创建订单计划、查重
     * @param sysOrderPlanDTO
     * @return
     */
    @Override
    public R releaseOrderPlan(SysOrderPlanDTO sysOrderPlanDTO) throws BrokenBarrierException, InterruptedException {

        /** 包月規則查驗：工作日>=8h、休息日不做限制、跨度不少於30天 */
        LocalDate start0 = OptionalBean.ofNullable(sysOrderPlanDTO)
                .getBean(SysOrderPlanDTO::getRulesMonthlyVo)
                .getBean(RulesMonthlyVo::getStart).get();
        LocalDate end0 = OptionalBean.ofNullable(sysOrderPlanDTO)
                .getBean(SysOrderPlanDTO::getRulesMonthlyVo)
                .getBean(RulesMonthlyVo::getEnd).get();
        if (CommonUtils.isNotEmpty(start0) && CommonUtils.isNotEmpty(end0)){
            if (sysOrderPlanDTO.getRulesMonthlyVo().getStart().plusMonths(1).isAfter(sysOrderPlanDTO.getRulesMonthlyVo().getEnd())){
                return R.failed("包月服務少於一個月，無法發佈");
            }else {
                AtomicReference<Float> h = new AtomicReference<>(0f);
                sysOrderPlanDTO.getRulesMonthlyVo().getTimeSlotVoWorkingDays().forEach(x -> {
                    h.updateAndGet(v -> v + x.getTimeSlotLength());
                });
                if (h.get() < 8){
                    return R.failed("工作日每日不足八個小時，無法構成包月");
                }
            }
        }
        /** 包月規則查驗 */

        /** 创建订单 */
        SysOrder sysOrder = new SysOrder();
        sysOrder.setType(sysOrderPlanDTO.getTemp());
        if (sysOrder.getType()){
            sysOrder.setCompanyId(sysOrderPlanDTO.getCompanyId());
        }
        sysOrder.setAddressId(sysOrderPlanDTO.getAddressId());
        sysOrder.setJobContendIds(sysOrderPlanDTO.getJobContendIds());
        AtomicReference<Integer> maxId = new AtomicReference<>(0);
        synchronized (this){
            sysOrderService.releaseOrder(sysOrder);
            maxId.set(((SysOrder) CommonUtils.getMaxId("sys_order", sysOrderService)).getId());
        }
        /** 创建订单 */

        final CyclicBarrier barrier = new CyclicBarrier(3);

        /** 创建订单计划 */
        /* 包月規則存儲 */
        Thread thread1  = new Thread(()->{
            if (CommonUtils.isNotEmpty(sysOrderPlanDTO.getRulesMonthlyVo())){
                LocalDate start1 = sysOrderPlanDTO.getRulesMonthlyVo().getStart();
                LocalDate current1 = start1;
                LocalDate end1 = sysOrderPlanDTO.getRulesMonthlyVo().getEnd();
                do {
                    LocalDate finalCurrent = current1;
                    List<TimeSlotVo> timeSlotVoDays = null;
                    if (current1.getDayOfWeek().getValue() == 6 || current1.getDayOfWeek().getValue() == 7){
                        //節假日
                        timeSlotVoDays = sysOrderPlanDTO.getRulesMonthlyVo().getTimeSlotVoHolidayDays();
                    }else {
                        //工作日
                        timeSlotVoDays = sysOrderPlanDTO.getRulesMonthlyVo().getTimeSlotVoWorkingDays();
                    }
                    timeSlotVoDays.forEach(x -> {
                        SysOrderPlan sysOrderPlan = new SysOrderPlan();
                        sysOrderPlan.setOrderId(maxId.get());
                        sysOrderPlan.setDate(finalCurrent);
                        sysOrderPlan.setTimeSlotStart(x.getTimeSlotStart());
                        sysOrderPlan.setTimeSlotLength(x.getTimeSlotLength());
                        baseMapper.insert(sysOrderPlan);
                    });
                    current1 = current1.plusDays(1);
                }while (!current1.equals(end1));
            }
            try {
                barrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }, "rulesMonthlySave");
        /* 包月規則存儲 */

        /* 定期服務規則存儲 */
        Thread thread2  = new Thread(()->{
            if (CommonUtils.isNotEmpty(sysOrderPlanDTO.getRulesWeekVos())){
                sysOrderPlanDTO.getRulesWeekVos().forEach(x -> {
                    LocalDate start = x.getStart();
                    LocalDate current = start;
                    LocalDate end = x.getEnd();
                    do {
                        if (x.getWeek().contains(String.valueOf(current.getDayOfWeek().getValue()))){
                            LocalDate finalCurrent = current;
                            x.getTimeSlotVos().forEach(y -> {
                                SysOrderPlan sysOrderPlan = new SysOrderPlan();
                                sysOrderPlan.setOrderId(maxId.get());
                                sysOrderPlan.setDate(finalCurrent);
                                sysOrderPlan.setTimeSlotStart(y.getTimeSlotStart());
                                sysOrderPlan.setTimeSlotLength(y.getTimeSlotLength());
                                baseMapper.insert(sysOrderPlan);
                            });
                        }
                        current = current.plusDays(1);
                    }while (!current.equals(end));
                });
            }
            try {
                barrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }, "rulesWeekSave");
        /* 定期服務規則存儲 */

        /* 單次服務規則存儲 */
        Thread thread3  = new Thread(()->{
            if (CommonUtils.isNotEmpty(sysOrderPlanDTO.getRulesDateVos())){
                sysOrderPlanDTO.getRulesDateVos().forEach(x -> {
                    x.getTimeSlotVos().forEach(y -> {
                        SysOrderPlan sysOrderPlan = new SysOrderPlan();
                        sysOrderPlan.setOrderId(maxId.get());
                        sysOrderPlan.setDate(x.getDate());
                        sysOrderPlan.setTimeSlotStart(y.getTimeSlotStart());
                        sysOrderPlan.setTimeSlotLength(y.getTimeSlotLength());
                        baseMapper.insert(sysOrderPlan);
                    });
                });
            }
            try {
                barrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }, "rulesDateSave");
        /* 單次服務規則存儲 */
        /** 创建订单计划 */

        thread1.start();
        thread2.start();
        thread3.start();

        barrier.await();//等待三个线程执行完毕
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("order_id", maxId.get());

        /** 查重 */
        List<Map> res = new ArrayList<>();
        List<SysOrderPlan> sysOrderPlanList = baseMapper.selectList(queryWrapper);
        Map<Object, List<SysOrderPlan>> maps = new HashMap<>();
        sysOrderPlanList.forEach(x -> {
            List<SysOrderPlan> te = null;
            te = maps.getOrDefault(x.getDate(), new ArrayList<>());
            te.add(x);
            maps.put(x.getDate(), te);
        });
        maps.values().forEach(x -> {
            x.forEach(y -> {
                x.forEach(z -> {
                    if (y.getId() != z.getId()){
                        PeriodOfTime periodOfTime1 = new PeriodOfTime(y.getTimeSlotStart(), y.getTimeSlotLength());
                        PeriodOfTime periodOfTime2 = new PeriodOfTime(z.getTimeSlotStart(), z.getTimeSlotLength());
                        if (CommonUtils.doRechecking(periodOfTime1, periodOfTime2)){
                            Map<String, Object> entity = new HashMap<>();
                            entity.put("date", y.getDate());
                            entity.put("periodOfTime1", periodOfTime1);
                            entity.put("periodOfTime2", periodOfTime2);
                            res.add(entity);
                        }
                    }
                });
            });
        });
//        sysOrderPlanList.sort((SysOrderPlan s1, SysOrderPlan s2) ->
//            s1.getDate().compareTo(s2.getDate())
//        );
//        for (int i = 1; i < sysOrderPlanList.size(); i++) {
//            if (sysOrderPlanList.size() == 0 || sysOrderPlanList.size() == 1){
//                break;
//            }else {
//                for (int j = 0; j < i; j++) {
//                    if (sysOrderPlanList.get(i).getDate().equals(sysOrderPlanList.get(j).getDate())){
//                        PeriodOfTime periodOfTime1 = new PeriodOfTime(sysOrderPlanList.get(i).getTimeSlotStart(),sysOrderPlanList.get(i).getTimeSlotLength());
//                        PeriodOfTime periodOfTime2 = new PeriodOfTime(sysOrderPlanList.get(j).getTimeSlotStart(),sysOrderPlanList.get(j).getTimeSlotLength());
//                        if (CommonUtils.doRechecking(periodOfTime1, periodOfTime2)){
//                            Map<String, Object> entity = new HashMap<>();
//                            entity.put("date", sysOrderPlanList.get(i).getDate());
//                            entity.put("periodOfTime1", periodOfTime1);
//                            entity.put("periodOfTime2", periodOfTime2);
//                            res.add(entity);
//                        }
//                    }
//                }
//            }
//        }
        /** 查重 */

        /** 查重結果處理 */
        if (res.size() == 0){
            return R.ok("查重完成，並已上傳訂單");
        }else {
            baseMapper.delete(queryWrapper);
            return R.failed(res, "存在重複，清先解決時間段衝突");
        }
        /** 查重結果處理 */

    }

    @Override
    public R getAllByOrderId(Integer orderId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("order_id", orderId);
        return R.ok(baseMapper.selectList(queryWrapper), "獲取訂單" + orderId + "計劃成功");
    }
}
