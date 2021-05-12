package com.housekeeping.admin.mapper;

import com.housekeeping.admin.entity.PaymentCallback;
import org.apache.ibatis.annotations.Param;

/**
 * @Author su
 * @create 2021/5/12 9:33
 */
public interface PaymentCallbackMapper {

    void savePCInfo(@Param("pc") PaymentCallback pc);

}
