package com.housekeeping.admin.service;

import com.housekeeping.common.utils.R;

/**
 * @Author su
 * @Date 2021/1/12 16:31
 */
public interface IAddressCodingService {

    R addressCoding(String address);
    Integer getInstanceByPointByWalking(String latitude1,
                                  String longitude1,
                                  String latitude2,
                                  String longitude2);

}
