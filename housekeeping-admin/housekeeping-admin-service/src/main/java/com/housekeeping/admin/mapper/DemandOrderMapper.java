package com.housekeeping.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.housekeeping.admin.entity.DemandOrder;
import com.housekeeping.admin.vo.TimeSlot;

import java.util.List;

/**
 * @Author su
 * @Date 2021/3/2 16:39
 */
public interface DemandOrderMapper extends BaseMapper<DemandOrder> {
    List<TimeSlot> getTimes(Integer demandId);
}
