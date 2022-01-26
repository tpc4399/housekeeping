package com.housekeeping.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.housekeeping.admin.dto.KeyWorkReturnDTO;
import com.housekeeping.admin.entity.OrderPhotos;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author su
 * @Date 2021/4/28 16:09
 */
public interface OrderPhotosMapper {

    List<OrderPhotos> listByNumber(String number);

    List<OrderPhotos> getByOrderNumber(String number);

    void saveBatch(@Param("list") List<OrderPhotos> ops);

}
