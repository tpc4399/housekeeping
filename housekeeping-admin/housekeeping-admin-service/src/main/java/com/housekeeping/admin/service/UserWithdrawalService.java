package com.housekeeping.admin.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.entity.UserWithdrawal;
import com.housekeeping.common.utils.R;


public interface UserWithdrawalService extends IService<UserWithdrawal> {
    R addWithdrawal(UserWithdrawal userWithdrawal);

    R handleWithdrawal(Integer id, Integer status);

    R getAllWithdrawal(Integer id, Integer userId, String bank, String collectionAccount, Integer status);
}
