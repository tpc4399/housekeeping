package com.housekeeping.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.housekeeping.admin.dto.PageOfEmployeesDTO;
import com.housekeeping.admin.entity.EmployeesDetails;
import com.housekeeping.admin.vo.EmployeesDetailsVO;
import com.housekeeping.admin.vo.EmployeesVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EmployeesDetailsMapper extends BaseMapper<EmployeesDetails> {

    String getScaleById(Integer id);

    void updateHeadUrlById(@Param("headUrl") String headUrl,
                           @Param("userId") Integer userId);

    List<Integer> getAllIdsByCompanyId(Integer companyId);

    void blacklist(@Param("employeesId") Integer employeesId,
                   @Param("action") Boolean action);

    void setWorkingArea(@Param("employeesId") Integer employeesId,
                        @Param("areaIds") String areaIds);

    IPage<List<EmployeesVo>> getAllEmployeesByAdmin(Page page,
                                                    @Param("query") PageOfEmployeesDTO pageOfEmployeesDTO);

    void setPresetJobIds(@Param("presetJobIds") String presetJobIds,
                         @Param("employeesId") Integer employeesId);
}
