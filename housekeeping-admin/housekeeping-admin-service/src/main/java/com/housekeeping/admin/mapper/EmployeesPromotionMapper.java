package com.housekeeping.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.housekeeping.admin.dto.CompanyPromotionDTO;
import com.housekeeping.admin.dto.EmployeesPromotionDTO;
import com.housekeeping.admin.entity.CompanyPromotion;
import com.housekeeping.admin.entity.EmployeesPromotion;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface EmployeesPromotionMapper extends BaseMapper<EmployeesPromotion> {

    List<EmployeesPromotionDTO> getEmpInfoByCompanyId(@Param("companyId") Integer companyId,@Param("empId") Integer empId,@Param("empName") String empName);

    List<Integer> getEmpByRan(Integer random);
}
