package com.housekeeping.admin.service.impl;

import com.housekeeping.admin.dto.RequestToChangeAddressDTO;
import com.housekeeping.admin.service.ICustomerAddressService;
import com.housekeeping.admin.service.INotificationService;
import com.housekeeping.common.utils.R;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author su
 * @Date 2021/4/23 10:35
 */
@Service("notificationService")
public class NotificationServiceImpl implements INotificationService {

    @Resource
    private ICustomerAddressService customerAddressService;

    @Override
    public R requestToChangeAddress(RequestToChangeAddressDTO dto) {
        return null;
    }
}
