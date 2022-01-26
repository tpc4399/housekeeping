package com.housekeeping.admin.service;

import com.housekeeping.common.utils.R;

public interface IStatisticsService {

    R empOrder(Integer empId, Integer type, Integer year, Integer month, Integer day);

    R empOrderByDate(Integer empId, String startDate, String endDate);

    R comOrder(Integer comId, Integer type, Integer year, Integer month, Integer day);

    R comOrderByDate(Integer comId, String startDate, String endDate);

    R empIncome(Integer empId, Integer type, Integer year, Integer month, Integer day);

    R empIncomeByDate(Integer empId, String startDate, String endDate);

    R comIncome(Integer comId, Integer type, Integer year, Integer month, Integer day);

    R comIncomeByDate(Integer comId, String startDate, String endDate);

    R empWork(Integer empId, Integer type, Integer year, Integer month, Integer day);

    R empWorkByDate(Integer empId, String startDate, String endDate);

    R comWork(Integer comId, Integer type, Integer year, Integer month, Integer day);

    R comWorkByDate(Integer comId, String startDate, String endDate);
}
