package com.housekeeping.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.housekeeping.admin.entity.NotificationOfRequestForChangeOfAddress;
import org.apache.ibatis.annotations.Param;

/**
 * @Author su
 * @Date 2021/4/25 11:40
 */
public interface NotificationOfRequestForChangeOfAddressMapper
        extends BaseMapper<NotificationOfRequestForChangeOfAddress> {

    void requestToChangeAddressHandle(@Param("id") Integer id,
                                      @Param("result") Boolean result);

}
