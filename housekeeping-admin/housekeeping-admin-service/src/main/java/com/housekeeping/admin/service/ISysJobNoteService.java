package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.AddJobContendDTO;
import com.housekeeping.admin.entity.SysJobContend;
import com.housekeeping.admin.entity.SysJobNote;
import com.housekeeping.common.utils.R;

import java.util.List;


public interface ISysJobNoteService extends IService<SysJobNote> {

    R add(List<SysJobNote> dos);
    R getAll(List<Integer> ids);

    R cusRemove(List<Integer> ids);

    R getAllByContent(Integer contentId);
}
