package com.housekeeping.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.housekeeping.admin.entity.EmployeesDetails;
import com.housekeeping.admin.vo.EmployeesDetailsVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EmployeesDetailsMapper extends BaseMapper<EmployeesDetails> {

    String getScaleById(Integer id);

    IPage<List<EmployeesDetailsVO>> cusPage(Page page, @Param("id") Integer id,@Param("companyId")Integer companyId);
}
