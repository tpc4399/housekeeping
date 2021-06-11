package com.housekeeping.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.housekeeping.admin.dto.KeyWorkReturnDTO;
import com.housekeeping.admin.entity.OrderPhotos;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * @Author su
 * @Date 2021/4/28 16:09
 */
public interface OrderPhotosMapper extends BaseMapper<OrderPhotos> {

    void keyWorkReturn(@Param("dto") KeyWorkReturnDTO dto,
                       @Param("now") LocalDateTime now);

}
