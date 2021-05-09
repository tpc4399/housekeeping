package com.housekeeping.admin.service;

import com.housekeeping.admin.dto.QueryDTO;
import com.housekeeping.admin.dto.QueryIndexDTO;
import com.housekeeping.common.utils.R;

/**
 * @Author su
 * @create 2021/5/9 11:37
 */
public interface IQueryService {

    R query(QueryDTO dto) throws InterruptedException;

}
