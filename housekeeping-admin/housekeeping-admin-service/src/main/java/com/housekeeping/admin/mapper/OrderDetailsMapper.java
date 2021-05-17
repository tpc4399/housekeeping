package com.housekeeping.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.housekeeping.admin.entity.OrderDetails;
import com.housekeeping.admin.entity.WorkDetails;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * @Author su
 * @Date 2021/4/19 9:47
 */
public interface OrderDetailsMapper extends BaseMapper<OrderDetails> {

    Integer orderRetentionTime(Integer employeesId);

    void statusAndTime(@Param("number") Long number,
                       @Param("status") Integer status,
                       @Param("time") LocalDateTime time);

}
