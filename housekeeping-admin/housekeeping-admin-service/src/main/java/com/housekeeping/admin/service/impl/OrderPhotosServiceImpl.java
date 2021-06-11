package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.KeyWorkReturnDTO;
import com.housekeeping.admin.entity.OrderPhotos;
import com.housekeeping.admin.mapper.OrderPhotosMapper;
import com.housekeeping.admin.service.IOrderPhotosService;
import com.housekeeping.common.utils.CommonConstants;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Author su
 * @Date 2021/4/28 16:12
 */
@Service("orderPhotosService")
public class OrderPhotosServiceImpl
        extends ServiceImpl<OrderPhotosMapper, OrderPhotos>
        implements IOrderPhotosService {
    @Override
    public Integer isCallback(String number) {
        QueryWrapper qw = new QueryWrapper();
        qw.eq("number", number);
        List<OrderPhotos> orderPhotos = this.list(qw);
        if (CommonUtils.isEmpty(orderPhotos)) return CommonConstants.ORDER_PHOTOS_STATUS_3; //没有工作重点,无需回传
        Long ok = new Long(0);//已回传的工作重点
        Long opLength = new Long(orderPhotos.size()); //需要回传的工作重点数量
        ok += orderPhotos.stream().filter(OrderPhotos::getYes).count();

        if (ok.equals(opLength)) return CommonConstants.ORDER_PHOTOS_STATUS_2;//已回传的，等于需要回传的，已经回传完成
        if (ok<opLength&&ok>0) return CommonConstants.ORDER_PHOTOS_STATUS_1;//已回传的，处于0-n之间,说明只有部分已经回传
        if (ok.equals(0)) return CommonConstants.ORDER_PHOTOS_STATUS_0;//已回传的，为0，说明还未回传
        return -1;//说明程序故障
    }

    @Override
    public R keyWorkReturn(List<KeyWorkReturnDTO> dto) {
        LocalDateTime now = LocalDateTime.now();
        dto.forEach(x -> {
            baseMapper.keyWorkReturn(x, now);
        });
        return R.ok(null, "回傳成功");
    }
}
