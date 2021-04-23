package com.housekeeping.admin.service;

import com.housekeeping.admin.dto.RequestToChangeAddressDTO;
import com.housekeeping.common.utils.R;

/**
 * @Author su
 * @Date 2021/4/23 10:34
 */
public interface INotificationService {

    R requestToChangeAddress(RequestToChangeAddressDTO dto);

}
