package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.AppointmentContractDTO;
import com.housekeeping.admin.entity.EmployeesContract;
import com.housekeeping.common.utils.R;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

/**
 * @Author su
 * @Date 2021/1/30 17:16
 */
public interface IEmployeesContractService extends IService<EmployeesContract> {

    R getByEmployeesId(Integer employeesId);
    R add2(Integer employeesId,
           String name,
           MultipartFile[] image,
           Integer dateLength,
           Float timeLength,
           BigDecimal totalPrice,
           Integer[] jobs,
           String description,
           Integer[] actives);

    R update(Integer id,
             String name,
             MultipartFile[] image,
             Integer dateLength,
             Float timeLength,
             BigDecimal totalPrice,
             Integer[] jobs,
             String description,
             Integer[] actives);

    R cusGetById(Integer id);

    /* 包工服务预约 */
    R appointmentContract(AppointmentContractDTO dto);

    R confirm(AppointmentContractDTO dto);
}
