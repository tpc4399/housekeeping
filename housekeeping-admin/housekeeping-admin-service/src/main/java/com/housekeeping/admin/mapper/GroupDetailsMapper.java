package com.housekeeping.admin.mapper;



import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.housekeeping.admin.entity.GroupDetails;
import com.housekeeping.admin.vo.GroupDetailsVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface GroupDetailsMapper extends BaseMapper<GroupDetails> {


    Boolean removeEmp(Integer id);

    Boolean removeMan(Integer id);

    IPage<List<GroupDetails>> getGroup(Page page,@Param("companyId") Integer companyId,@Param("id") Integer id);

    void addMan(@Param("groupId") Integer groupId,@Param("managerId") Integer managerId);

    void updateGroup(GroupDetails groupDetails);

    List<GroupDetailsVo> getAllGroups(String groupName);
}
