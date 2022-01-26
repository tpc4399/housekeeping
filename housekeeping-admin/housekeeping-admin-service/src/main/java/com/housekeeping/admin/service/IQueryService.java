package com.housekeeping.admin.service;

import com.housekeeping.admin.dto.QueryDTO;
import com.housekeeping.common.utils.R;

import java.math.BigDecimal;

/**
 * @Author su
 * @create 2021/5/9 11:37
 */
public interface IQueryService {

    R query(QueryDTO dto) throws InterruptedException;

    R query3(QueryDTO dto) throws InterruptedException;

    BigDecimal variablePrice(Integer employeesId,Integer jobId);

    R query4(QueryDTO dto) throws InterruptedException;
}
