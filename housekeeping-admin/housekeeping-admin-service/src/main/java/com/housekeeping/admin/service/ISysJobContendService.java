package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.entity.SysJobContend;
import com.housekeeping.admin.vo.SysJobContendVo;
import com.housekeeping.common.utils.R;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author su
 * @Date 2020/12/11 16:07
 */
public interface ISysJobContendService extends IService<SysJobContend> {

    R getTreeByIds(Integer[] ids);
    R getTree();
    R getParents();
    R add(SysJobContendVo vo);

}
