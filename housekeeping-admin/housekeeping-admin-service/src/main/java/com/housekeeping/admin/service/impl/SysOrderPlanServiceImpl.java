package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.SysOrderPlanDTO;
import com.housekeeping.admin.entity.SysOrder;
import com.housekeeping.admin.entity.SysOrderPlan;
import com.housekeeping.admin.mapper.SysOrderPlanMapper;
import com.housekeeping.admin.service.ISysOrderPlanService;
import com.housekeeping.admin.service.ISysOrderService;
import com.housekeeping.admin.vo.TimeSlotVo;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
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
    public R releaseOrderPlan(SysOrderPlanDTO sysOrderPlanDTO) {

        /** 包月規則查驗：工作日>=8h、休息日不做限制、跨度不少於30天 */
        if (CommonUtils.isNotEmpty(sysOrderPlanDTO.getRulesMonthlyVo().getStart()) && CommonUtils.isNotEmpty(sysOrderPlanDTO.getRulesMonthlyVo().getEnd())){
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
        AtomicReference<Integer> maxId = new AtomicReference<>(0);
        synchronized (this){
            sysOrderService.releaseOrder(sysOrder);
            maxId.set(((SysOrder) CommonUtils.getMaxId("sys_order", this)).getId());
        }
        /** 创建订单 */

        /** 创建订单计划 */
        /* 包月規則存儲 */
        Thread thread1  = new Thread(()->{
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
                    sysOrderPlan.setData(finalCurrent);
                    sysOrderPlan.setTimeSlotStart(x.getTimeSlotStart());
                    sysOrderPlan.setTimeSlotLength(x.getTimeSlotLength());
                    baseMapper.insert(sysOrderPlan);
                });
                current1 = current1.plusDays(1);
            }while (!start1.equals(end1));
        }, "rulesMonthlySave");
        /* 包月規則存儲 */

        /* 定期服務規則存儲 */
        Thread thread2  = new Thread(()->{
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
                            sysOrderPlan.setData(finalCurrent);
                            sysOrderPlan.setTimeSlotStart(y.getTimeSlotStart());
                            sysOrderPlan.setTimeSlotLength(y.getTimeSlotLength());
                            baseMapper.insert(sysOrderPlan);
                        });
                    }
                    current = current.plusDays(1);
                }while (!start.equals(end));
            });
        }, "rulesWeekSave");
        /* 定期服務規則存儲 */

        /* 單次服務規則存儲 */
        Thread thread3  = new Thread(()->{
            sysOrderPlanDTO.getRulesDateVos().forEach(x -> {
                x.getTimeSlotVos().forEach(y -> {
                    SysOrderPlan sysOrderPlan = new SysOrderPlan();
                    sysOrderPlan.setOrderId(maxId.get());
                    sysOrderPlan.setData(x.getDate());
                    sysOrderPlan.setTimeSlotStart(y.getTimeSlotStart());
                    sysOrderPlan.setTimeSlotLength(y.getTimeSlotLength());
                    baseMapper.insert(sysOrderPlan);
                });
            });
        }, "rulesDateSave");
        /* 單次服務規則存儲 */
        /** 创建订单计划 */

        thread1.run();
        thread2.run();
        thread3.run();

        /** 查重 */
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("order_id", maxId.get());
        List<SysOrderPlan> sysOrderPlanList = baseMapper.selectList(queryWrapper);
        sysOrderPlanList.sort((SysOrderPlan s1, SysOrderPlan s2) ->
            s1.getData().compareTo(s2.getData())
        );

        /** 查重 */

        return null;
    }
}
