package com.housekeeping.im.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.housekeeping.im.entity.ImChatGroup;
import com.housekeeping.im.entity.ImUser;
import com.housekeeping.im.entity.ImUserInfo;
import org.omg.CORBA.INTERNAL;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Qualifier("imUserMapper")
public interface ImUserMapper extends BaseMapper<ImUser> {


    /**
     * 根据用户id 获取群组
     * @param userId id
     * @return List<ImGroup>
     */
    List<ImChatGroup> getUserGroups(String userId);


    /**
     * 获取群组的用户
     * @param chatId 群组id
     * @return 用户List
     */
    List<ImUserInfo> getChatUserList(String chatId);

    List<Integer> getGroupsById(Integer id);

    List<Integer> getMansByGroupId(Integer gid);

    Integer getCompanyId(int id);

    Integer getUserId(Integer id);

    Integer getEmpId(int parseInt);

    Integer getUserIdByCom(Integer companyId);

    String getCompanyLogo(Integer userId);

    String employeesHeadUrl(Integer userId);

    String managerHeadUrl(Integer userId);

    String customerHeadUrl(Integer userId);

    Integer getCusIdByDemand(String demandId);

    Integer getUSerIdByEmpId(String empId);
}
