package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.Serial;
import com.housekeeping.admin.entity.SerialPhotos;
import com.housekeeping.admin.entity.SerialWorks;
import com.housekeeping.admin.mapper.SerialMapper;
import com.housekeeping.admin.pojo.OrderDetailsPOJO;
import com.housekeeping.admin.service.ISerialPhotosService;
import com.housekeeping.admin.service.ISerialService;
import com.housekeeping.admin.service.ISerialWorksService;
import com.housekeeping.common.utils.R;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author su
 * @create 2021/5/26 9:44
 */
@Service("serialService")
public class SerialServiceImpl
        extends ServiceImpl<SerialMapper, Serial>
        implements ISerialService {

    @Resource
    private ISerialWorksService serialWorksService;
    @Resource
    private ISerialPhotosService serialPhotosService;

    @Override
    public R generatePipeline(OrderDetailsPOJO od) {
        Serial s = new Serial(od);
        List<SerialPhotos> serialPhotos = new ArrayList<>();
        List<SerialWorks> serialWorks = new ArrayList<>();

        serialWorksService.saveBatch(serialWorks);
        serialPhotosService.saveBatch(serialPhotos);
        return R.ok(null, "成功保存流水");
    }
}
