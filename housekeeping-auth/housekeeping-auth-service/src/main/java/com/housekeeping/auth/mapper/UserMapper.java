package com.housekeeping.auth.mapper;

import com.housekeeping.admin.dto.UserDTO;
import org.apache.ibatis.annotations.Param;

/**
 * @Author su
 * @create 2020/10/27 2:21
 */
public interface UserMapper {

    void insertOne(@Param("userDTO") UserDTO userDTO,
                   @Param("number") String number,
                   @Param("lastReviserId") Integer lastReviserId);
    void changePasswordByPhone(@Param("phone") String phone,
                               @Param("newPassword") String newPassword);

}
