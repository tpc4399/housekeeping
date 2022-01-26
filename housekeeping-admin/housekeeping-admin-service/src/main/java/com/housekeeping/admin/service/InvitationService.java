package com.housekeeping.admin.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.entity.Invitation;
import com.housekeeping.common.utils.R;

public interface InvitationService extends IService<Invitation> {

    R getAllInvitees(Integer userId);

    R getOrders(Integer userId);

    R getAllUser(Page page);
}
