package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.entity.SysJobContend;
import com.housekeeping.common.utils.R;

/**
 * @Author su
 * @Date 2020/12/11 16:07
 */
public interface ISysJobContendService extends IService<SysJobContend> {

    R getTreeByIds(Integer[] ids);
    R getTree();
    R getParents();

    /***
     * 包工: true
     * 钟点: false
     * 出意外: null
     * @param jobId
     * @return
     */
    Boolean getType(Integer jobId);

}
