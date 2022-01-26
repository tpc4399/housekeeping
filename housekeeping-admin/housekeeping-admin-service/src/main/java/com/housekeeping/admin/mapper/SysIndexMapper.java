package com.housekeeping.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.housekeeping.admin.entity.SysIndex;

import java.util.List;

/**
 * @Author su
 * @Date 2021/1/12 14:43
 */
public interface SysIndexMapper extends BaseMapper<SysIndex> {
    List<Integer> getContentIds(Integer id);
}
