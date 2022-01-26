package com.housekeeping.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.housekeeping.admin.dto.PersonalRequestDTO;
import com.housekeeping.admin.entity.NotificationOfRequestForChangeOfAddress;
import com.housekeeping.admin.entity.PersonalRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author su
 * @Date 2021/4/25 11:40
 */
public interface PersonalRequestMapper
        extends BaseMapper<PersonalRequest> {

    List<PersonalRequestDTO> getAll(@Param("id") Integer id,@Param("status") Integer status,@Param("type")Integer type);

    void updateCompany(String companyId);

    void updateCompany2(String companyId);
}
