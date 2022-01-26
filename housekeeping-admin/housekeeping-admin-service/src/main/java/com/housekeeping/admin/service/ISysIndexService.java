package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.*;
import com.housekeeping.admin.entity.SysIndex;
import com.housekeeping.admin.vo.TimeSlot;
import com.housekeeping.common.utils.R;

import java.time.LocalTime;
import java.util.List;

/**
 * @Author su
 * @Date 2021/1/12 14:47
 */
public interface ISysIndexService extends IService<SysIndex> {
    R add(SysIndexAddDto sysIndexAddDto);
    R update(SysIndexUpdateDTO dto);
    R delete(Integer indexId);
    R getAll();
    R getCusById(Integer id);
    R tree();
    R defaultRecommendation(AddressDTO dto);
    R more1(AddressDTO dto);
    R more2(AddressDTO dto);
    R goon1(String credential);
    R goon2(String credential);
    R flush1(String credential);
    R flush2(String credential);
    /* 拆分时间段 */
    List<LocalTime> periodSplittingB(List<TimeSlot> slots);
    /* 输入关键词进行搜索 */
    R query(QueryParamsDTO dto);

    R add2(SysIndexAdd2DTO sysIndexAddDto);

    R update2(SysIndexUpdate2DTO dto);

    R getCal(Integer year, Integer month);
}
