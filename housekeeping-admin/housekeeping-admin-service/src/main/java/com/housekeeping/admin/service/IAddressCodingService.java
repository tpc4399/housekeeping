package com.housekeeping.admin.service;

import com.google.maps.errors.ApiException;
import com.housekeeping.admin.dto.AddressDetailsDTO;
import com.housekeeping.common.utils.R;

import java.io.IOException;

/**
 * @Author su
 * @Date 2021/1/12 16:31
 */
public interface IAddressCodingService {

    R addressCoding(String address);
    Double getInstanceByPointByWalking(String latitude1,
                                  String longitude1,
                                  String latitude2,
                                  String longitude2);
    AddressDetailsDTO addressCodingGoogleMap(String address) throws InterruptedException, ApiException, IOException;
}
