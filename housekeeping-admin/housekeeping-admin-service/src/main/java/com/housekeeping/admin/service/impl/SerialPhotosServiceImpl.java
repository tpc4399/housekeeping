package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.SerialPhotos;
import com.housekeeping.admin.mapper.SerialPhotosMapper;
import com.housekeeping.admin.service.ISerialPhotosService;
import org.springframework.stereotype.Service;

/**
 * @Author su
 * @create 2021/5/26 11:53
 */
@Service("serialPhotosService")
public class SerialPhotosServiceImpl
        extends ServiceImpl<SerialPhotosMapper, SerialPhotos>
        implements ISerialPhotosService {
}
