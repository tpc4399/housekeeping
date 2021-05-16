package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.entity.CompanyWorkList;
import com.housekeeping.admin.pojo.WorkDetailsPOJO;
import com.housekeeping.common.utils.R;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author su
 * @create 2020/11/18 16:09
 */
public interface ICompanyWorkListService extends IService<CompanyWorkList> {
    /* 添加需求订单到感兴趣列表 *//*
    R beInterested(Integer demandOrderId);*/

    /* 生成每个员工的临时订单，返回筛选后的员工ids */
    R suitableEmployees(Integer userId,Integer typeId,Integer  demandOrderId);
    /* 存储该保洁员的的临时订单 */
    R selectSuitableEmployees(Integer employeesId, Integer demandOrderId);

    R getAllInterestedEmployees(Integer demandOrderId);

    R getInterestedByManager();

    R getInterestedByCompany();

    List<WorkDetailsPOJO> getServiceTimeByEmployees(Integer demandOrderId, Integer employeesId);

    BigDecimal getPrice(List<WorkDetailsPOJO> workDetails,Integer demandOrderId, Integer employeesId);


    R confirmDemand(Integer quotationId);

    R changePrice(String quotationId, Integer price);

    R cusRemove(Integer id);

    R cusGetById(Integer quotationId);
}
