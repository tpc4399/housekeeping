package com.housekeeping.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.housekeeping.admin.entity.CustomerDemandPlan;
import org.apache.ibatis.annotations.Param;

/**
 * @Author su
 * @Date 2020/12/28 14:24
 */
public interface CustomerDemandPlanMapper extends BaseMapper<CustomerDemandPlan> {

    void setJobContends(@Param("id") Integer id,
                        @Param("jobsId") Integer jobsId);

}
