package com.housekeeping.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.housekeeping.admin.entity.SysEnterpriseAuthenticationMessage;
import org.apache.ibatis.annotations.Param;

/**
 * @Author su
 * @Date 2020/12/8 14:32
 */
public interface SysEnterpriseAuthenticationMessageMapper extends BaseMapper<SysEnterpriseAuthenticationMessage> {
    void undo(Integer companyId);

    void updateCompany(@Param("id") Integer id,@Param("number") String number);
}
