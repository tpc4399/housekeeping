package com.housekeeping.admin.mapper;



import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.housekeeping.admin.entity.Group;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface GroupMapper extends BaseMapper<Group> {


    Boolean removeEmp(Integer id);

    Boolean removeMan(Integer id);

    IPage<List<Group>> getGroup(Page page,@Param("companyId") Integer companyId,@Param("id") Integer id);

    R addMan(@Param("groupId") Integer groupId,@Param("managerId") Integer managerId);
}
