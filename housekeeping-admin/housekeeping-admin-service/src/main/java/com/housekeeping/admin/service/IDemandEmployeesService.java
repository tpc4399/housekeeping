package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.entity.DemandEmployees;

import java.util.List;


public interface IDemandEmployeesService extends IService<DemandEmployees> {

    List<Integer> getAllUserId();
}
