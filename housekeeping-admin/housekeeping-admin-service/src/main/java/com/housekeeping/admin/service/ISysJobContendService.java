package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.AddJobContendDTO;
import com.housekeeping.admin.entity.SysIndexContent;
import com.housekeeping.admin.entity.SysJobContend;
import com.housekeeping.admin.vo.SysJobContendVo;
import com.housekeeping.common.utils.R;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Author su
 * @Date 2020/12/11 16:07
 */
public interface ISysJobContendService extends IService<SysJobContend> {

    R add(AddJobContendDTO dos);
    R getAll(List<Integer> ids);
    R getAll2(String ids);

    R cusRemove(List<Integer> ids);

    R cusUpdate(AddJobContendDTO sysJobContend);
}
