package com.housekeeping.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.housekeeping.admin.entity.Index;
import com.housekeeping.common.utils.R;

import java.util.List;

public interface IndexMapper extends BaseMapper<Index> {
    List<Integer> getContentIds(Integer id);
}
