package com.housekeeping.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.housekeeping.admin.dto.PageOfEmployeesDTO;
import com.housekeeping.admin.entity.EmployeesDetails;
import com.housekeeping.admin.vo.EmployeesDetailsSkillVo;
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

    List<EmployeesVo> getAllEmployeesByAdmin(@Param("query") PageOfEmployeesDTO pageOfEmployeesDTO);

    void setPresetJobIds(@Param("presetJobIds") String presetJobIds,
                         @Param("employeesId") Integer employeesId);

    EmployeesDetailsSkillVo getCusById(Integer empId);

    List<Integer> getAllChatGroup(Integer userId);

    void updateChatName(@Param("chatId") Integer chatId,@Param("name") String name);

    void updateChatPhoto(@Param("chatId")Integer chatId,@Param("headUrl") String headUrl);

    void setPresetJobPrice(@Param("price")String price,@Param("empId") Integer empId);

    List<EmployeesDetails> getPersonal(@Param("query")PageOfEmployeesDTO pageOfEmployeesDTO);

    Integer getCollectionVolume(Integer empId);
}
