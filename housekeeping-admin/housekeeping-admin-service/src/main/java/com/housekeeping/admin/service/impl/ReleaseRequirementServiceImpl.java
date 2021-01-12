package com.housekeeping.admin.service.impl;

import com.housekeeping.admin.service.IReleaseRequirementService;
import com.housekeeping.admin.vo.RulesDateVo;
import com.housekeeping.admin.vo.RulesMonthlyVo;
import com.housekeeping.admin.vo.RulesWeekVo;
import com.housekeeping.admin.vo.TimeSlot;
import com.housekeeping.common.entity.PeriodOfTime;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author su
 * @Date 2021/1/12 9:06
 */
@Service("releaseRequirementService")
public class ReleaseRequirementServiceImpl implements IReleaseRequirementService {
    @Override
    public R jobContendRecheckingA(List<RulesDateVo> rulesDateVos) {
        List<Map<String, String>> res = new ArrayList<>();
        Map<LocalDate, Object> dateTimeSlotMap = new HashMap();
        rulesDateVos.forEach(x -> {
            LocalDate key = x.getDate();
            List<TimeSlot> existTimeSlot = (List<TimeSlot>) dateTimeSlotMap.getOrDefault(key, new ArrayList<>());
            x.getTimeSlotVos().forEach(y -> {
                existTimeSlot.add(y);
            });
            dateTimeSlotMap.put(key, existTimeSlot);
        });
        dateTimeSlotMap.forEach((x, y) -> {
            List<TimeSlot> existTimeSlot = (List<TimeSlot>)y;
            for (int i = 0; i < existTimeSlot.size() - 1; i++) {
                PeriodOfTime a = new PeriodOfTime(
                        existTimeSlot.get(i).getTimeSlotStart(),
                        existTimeSlot.get(i).getTimeSlotLength()
                );
                PeriodOfTime b = new PeriodOfTime(
                        existTimeSlot.get(i+1).getTimeSlotStart(),
                        existTimeSlot.get(i+1).getTimeSlotLength()
                );
                if (CommonUtils.doRechecking(a, b)){
                    Map<String, String> map = new HashMap<>();
                    map.put("date", x.toString());
                    map.put("details", a.toString() + " " + b.toString());
                    res.add(map);
                }
            }
        });
        R ret = res.size() == 0 ? R.failed(res, "存在衝突") : R.ok("查重完成");
        return ret;
    }

    @Override
    public R jobContendRecheckingB(List<RulesWeekVo> rulesWeekVos) {
        List<Map<String, String>> res = new ArrayList<>();
        Map<Integer, Object> weekTimeSlotMap = new HashMap();
        rulesWeekVos.forEach(x -> {
            
        });
        R ret = res.size() == 0 ? R.failed(res, "存在衝突") : R.ok("查重完成");
        return ret;
    }

    @Override
    public R jobContendRecheckingC(RulesMonthlyVo rulesMonthlyVo) {
        return null;
    }

    @Override
    public R generateOrder(Integer[] sonIds, Integer serviceType) {
        return null;
    }

    @Override
    public R putInStorageA(List<RulesDateVo> rulesDateVos) {
        return null;
    }

    @Override
    public R putInStorageB(List<RulesWeekVo> rulesWeekVos) {
        return null;
    }

    @Override
    public R putInStorageC(RulesMonthlyVo rulesMonthlyVo) {
        return null;
    }
}
