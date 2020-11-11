package com.housekeeping.admin.mapper;



import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.housekeeping.admin.entity.Group;


public interface GroupMapper extends BaseMapper<Group> {

    R cusRemove(Integer groupManagerId);
}
