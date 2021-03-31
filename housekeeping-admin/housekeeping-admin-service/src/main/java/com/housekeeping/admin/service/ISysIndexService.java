package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.*;
import com.housekeeping.admin.entity.SysIndex;
import com.housekeeping.admin.vo.EmployeesHandleVo;
import com.housekeeping.common.utils.R;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Author su
 * @Date 2021/1/12 14:47
 */
public interface ISysIndexService extends IService<SysIndex> {
    R add(SysIndexAddDto sysIndexAddDto);
    R update(SysIndexUpdateDTO dto);
    R delete(Integer indexId);
    R getAll();
    R getCusById(Integer id);
    R query(QueryIndexDTO dto) throws InterruptedException;
    R tree();
    R defaultRecommendation(AddressDTO dto);
    R more1(AddressDTO dto);
    R more2(AddressDTO dto);
    R goon1(String credential);
    R goon2(String credential);
    R flush1(String credential);
    R flush2(String credential);

}
