package com.housekeeping.auth.mapper;

import com.housekeeping.admin.dto.UserDTO;
import com.housekeeping.admin.entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @Author su
 * @create 2020/10/27 2:21
 */
@Repository
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

    String getPassword(Integer currentUserId);

    Boolean updatePassword(@Param("currentUserId") Integer currentUserId,@Param("encryptString") String encryptString);

    String getPhone(Integer currentUserId);

    String getPre(Integer currentUserId);

    void changePhone(@Param("phone") String phone,@Param("phonePrefix") String phonePrefix,@Param("currentUserId") Integer currentUserId);

    void changeEmpPhone(@Param("phone")String phone,@Param("phonePrefix") String phonePrefix,@Param("currentUserId") Integer currentUserId);
}
