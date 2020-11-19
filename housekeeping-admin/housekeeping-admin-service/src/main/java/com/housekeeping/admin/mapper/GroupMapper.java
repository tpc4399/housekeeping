package com.housekeeping.admin.mapper;



import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.housekeeping.admin.entity.GroupDetails;


public interface GroupMapper extends BaseMapper<GroupDetails> {

    R cusRemove(Integer groupManagerId);
}
