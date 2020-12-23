package com.housekeeping.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.housekeeping.admin.entity.CustomerDetails;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author su
 * @create 2020/11/23 10:54
 */
public interface CustomerDetailsMapper extends BaseMapper<CustomerDetails> {

    void updateHeadUrlById(@Param("headUrl") String headUrl,
                           @Param("userId") Integer userId);

    List<Integer> getIdbyByCompanyId(Integer companyId);

    List<Integer> getUserIdByGId(Integer gid);
}
