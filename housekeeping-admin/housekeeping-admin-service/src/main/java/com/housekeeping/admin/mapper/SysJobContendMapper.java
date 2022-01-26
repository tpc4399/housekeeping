package com.housekeeping.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.housekeeping.admin.entity.SysJobContend;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author su
 * @Date 2020/12/11 16:06
 */
public interface SysJobContendMapper extends BaseMapper<SysJobContend> {
    void cusRemoveNote(Integer id);

    void cusRemoveJob(Integer id);

    void insertNote(@Param("maxIndexId") Integer maxIndexId,@Param("noteId") Integer noteId);

    List<Integer> getAll(Integer id);
}
