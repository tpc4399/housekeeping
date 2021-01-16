package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.IndexQueryDTO;
import com.housekeeping.admin.entity.Index;
import com.housekeeping.common.utils.R;

public interface IIndexService extends IService<Index> {
    R getCusById(Integer id);
    R query(IndexQueryDTO indexQueryDTO);
}
