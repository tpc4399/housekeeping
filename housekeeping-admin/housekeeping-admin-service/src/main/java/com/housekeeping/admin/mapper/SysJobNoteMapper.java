package com.housekeeping.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.housekeeping.admin.entity.SysJobContend;
import com.housekeeping.admin.entity.SysJobNote;

import java.util.List;

/**
 * @Author su
 * @Date 2020/12/11 16:06
 */
public interface SysJobNoteMapper extends BaseMapper<SysJobNote> {
    List<Integer> getAllNoteByContent(Integer contentId);

    void cusRemove(Integer id);
}
