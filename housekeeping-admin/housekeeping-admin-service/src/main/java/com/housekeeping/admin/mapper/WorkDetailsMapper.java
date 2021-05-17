package com.housekeeping.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.housekeeping.admin.entity.WorkDetails;
import org.apache.ibatis.annotations.Param;

/**
 * @Author su
 * @Date 2021/4/28 16:08
 */
public interface WorkDetailsMapper extends BaseMapper<WorkDetails> {
    void add(@Param("wd") WorkDetails wd);
}
