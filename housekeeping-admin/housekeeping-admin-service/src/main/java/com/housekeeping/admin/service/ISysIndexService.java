package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.IndexQueryDTO;
import com.housekeeping.admin.entity.SysIndex;
import com.housekeeping.common.utils.R;

/**
 * @Author su
 * @Date 2021/1/12 14:47
 */
public interface ISysIndexService extends IService<SysIndex> {
    R getCusById(Integer id);
    R query(IndexQueryDTO indexQueryDTO);
}
