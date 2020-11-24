package com.housekeeping.admin.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.GroupDetailsDTO;
import com.housekeeping.admin.entity.GroupDetails;
import com.housekeeping.common.utils.R;
import org.springframework.web.bind.annotation.RequestBody;

public interface IGroupDetailsService extends IService<GroupDetails> {

    R saveGroup(GroupDetailsDTO groupDetailsDTO);

    R cusRemove(Integer id);

    IPage getGroup(Page page, Integer companyId, Integer id);

    R addMan(Integer groupId, Integer managerId);
}
