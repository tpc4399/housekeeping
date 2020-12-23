package com.housekeeping.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.housekeeping.admin.entity.SysOrder;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * @Author su
 * @create 2020/11/16 14:42
 */
public interface SysOrderMapper extends BaseMapper<SysOrder> {

    void setEvaluationDeadTime(@Param("evaluationDeadTime") LocalDateTime evaluationDeadTime,
                               @Param("orderId") Integer orderId);

    void doEvaluation(@Param("evaluationImage") String evaluationImage,
                      @Param("evaluationType") Boolean evaluationType,
                      @Param("evaluationStar") Float evaluationStar,
                      @Param("evaluationContent") String evaluationContent,
                      @Param("orderId") Integer orderId,
                      @Param("evaluationTime") LocalDateTime evaluationTime);

}
