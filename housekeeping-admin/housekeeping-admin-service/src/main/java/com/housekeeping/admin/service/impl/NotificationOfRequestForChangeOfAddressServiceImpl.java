package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.NotificationOfRequestForChangeOfAddress;
import com.housekeeping.admin.mapper.NotificationOfRequestForChangeOfAddressMapper;
import com.housekeeping.admin.service.INotificationOfRequestForChangeOfAddressService;
import org.springframework.stereotype.Service;

/**
 * @Author su
 * @Date 2021/4/25 11:36
 */
@Service("notificationOfRequestForChangeOfAddressService")
public class NotificationOfRequestForChangeOfAddressServiceImpl 
        extends ServiceImpl<NotificationOfRequestForChangeOfAddressMapper, NotificationOfRequestForChangeOfAddress>
        implements INotificationOfRequestForChangeOfAddressService {
    @Override
    public void requestToChangeAddressHandle(Integer id, Boolean result) {
        baseMapper.requestToChangeAddressHandle(id, result); //
    }
}
