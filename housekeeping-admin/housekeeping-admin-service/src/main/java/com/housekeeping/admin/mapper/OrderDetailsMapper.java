package com.housekeeping.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.housekeeping.admin.entity.OrderDetails;
import com.housekeeping.admin.entity.TokenOrder;
import org.apache.ibatis.annotations.Param;
import org.springframework.scheduling.annotation.Async;

import java.time.LocalDateTime;

/**
 * @Author su
 * @Date 2021/4/19 9:47
 */
public interface OrderDetailsMapper extends BaseMapper<OrderDetails> {

    Integer orderRetentionTime(Integer employeesId);

    void statusAndTime(@Param("number") String number,
                       @Param("status") Integer status,
                       @Param("time") LocalDateTime time);

    void status(@Param("number") String number,
                @Param("status") Integer status);

    @Async
    void insertEvaluation(String orderNumber);

    void insertTokenOrder(@Param("odp") TokenOrder odp);
}
