package com.housekeeping.auth.mapper;

import com.housekeeping.admin.dto.UserDTO;
import com.housekeeping.admin.entity.User;
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

    void bindingEmailByUserId(@Param("userId") Integer userId,
                              @Param("email") String email);

    Integer getDeptIdByUserId(Integer userId);

    User getOne(@Param("deptId") Integer deptId,
                @Param("email") String email);

}
