package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.entity.NotificationOfRequestForChangeOfAddress;
import com.housekeeping.common.utils.R;

/**
 * @Author su
 * @Date 2021/4/25 11:36
 */
public interface INotificationOfRequestForChangeOfAddressService extends IService<NotificationOfRequestForChangeOfAddress> {

    void requestToChangeAddressHandle(Integer id, Boolean result);

}
