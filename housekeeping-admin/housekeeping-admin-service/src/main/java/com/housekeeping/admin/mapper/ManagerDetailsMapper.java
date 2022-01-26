package com.housekeeping.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.housekeeping.admin.dto.PageOfManagerDTO;
import com.housekeeping.admin.entity.EmployeesDetails;
import com.housekeeping.admin.entity.ManagerDetails;
import com.housekeeping.admin.vo.EmployeesDetailsVO;
import com.housekeeping.admin.vo.ManagerVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ManagerDetailsMapper extends BaseMapper<ManagerDetails> {

    String getScaleById(Integer id);

    IPage<List<EmployeesDetailsVO>> cusPage(Page page, @Param("id") Integer id,@Param("companyId")Integer companyId);

    void updateHeadUrlById(@Param("headUrl") String headUrl,
                           @Param("userId") Integer userId);

    List<Integer> getManIdsByCompId(Integer id);

    List<ManagerVo> getAllManagerByAdmin(@Param("query")PageOfManagerDTO pageOfEmployeesDTO);

    List<Integer> getAllUserIdByCompanyId(Integer companyId);
}
