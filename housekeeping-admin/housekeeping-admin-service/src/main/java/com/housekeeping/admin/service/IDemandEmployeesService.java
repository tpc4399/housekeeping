package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.entity.CompanyPromotion;
import com.housekeeping.admin.entity.DemandEmployees;
import org.omg.CORBA.portable.Delegate;

import java.util.List;


public interface IDemandEmployeesService extends IService<DemandEmployees> {

    List<Integer> getAllUserId();
}
