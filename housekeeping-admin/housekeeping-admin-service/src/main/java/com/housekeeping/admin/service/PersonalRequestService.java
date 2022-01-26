package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.entity.NotificationOfRequestForChangeOfAddress;
import com.housekeeping.admin.entity.PersonalRequest;
import com.housekeeping.common.utils.R;


public interface PersonalRequestService extends IService<PersonalRequest> {

    R getAll(Page page,Integer id,Integer status,String name,Integer type);

    void updateCompany(String companyId);

    void updateCompany2(String companyId);
}
