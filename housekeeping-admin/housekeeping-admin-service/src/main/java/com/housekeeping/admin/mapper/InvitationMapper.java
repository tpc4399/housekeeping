package com.housekeeping.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.housekeeping.admin.entity.Invitation;
import org.apache.ibatis.annotations.Select;

import java.util.List;


public interface InvitationMapper extends BaseMapper<Invitation> {

    @Select("select distinct invitee from invitation")
    List<Integer> getAllInvitee();
}
