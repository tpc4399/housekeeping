package com.housekeeping.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.housekeeping.admin.entity.User;
import com.housekeeping.common.entity.HkUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {
    User getUserByIdAndDept(@Param("id") Integer id,@Param("i") int i);

    List<Integer> getAllEmps(Integer id);

    List<Integer> getAllMans(Integer id);

    HkUser byPhoneLogin(@Param("phone") String phone,
                        @Param("deptId")  Integer deptId);
}
