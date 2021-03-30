package com.housekeeping.admin.service;

import com.housekeeping.admin.dto.EmployeesInstanceDTO;

import java.util.List;

/**
 * @Author su
 * @Date 2021/3/30 18:45
 */
public interface IAsyncService {
    void setRedisDos(List<EmployeesInstanceDTO> dos);
}
