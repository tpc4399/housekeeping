package com.housekeeping.auth.mapper;

import com.housekeeping.common.entity.HkUser;
import org.apache.ibatis.annotations.Param;

/**
 * @Author su
 * @create 2020/10/27 2:21
 */
public interface HkUserMapper {
    HkUser byEmail(@Param("email") String email, @Param("deptId")  Integer deptId);
    HkUser byPhone(@Param("phonePrefix") String phonePrefix,
                   @Param("phone") String phone,
                   @Param("deptId")  Integer deptId);
    HkUser byPhoneLogin(@Param("phone") String phone,
                        @Param("deptId")  Integer deptId);
}
