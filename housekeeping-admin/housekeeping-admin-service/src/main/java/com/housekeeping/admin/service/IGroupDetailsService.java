package com.housekeeping.admin.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.GroupDetailsDTO;
import com.housekeeping.admin.dto.GroupDetailsUpdateDTO;
import com.housekeeping.admin.entity.GroupDetails;
import com.housekeeping.admin.vo.GroupVO;
import com.housekeeping.common.utils.R;
import io.swagger.models.auth.In;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;

public interface IGroupDetailsService extends IService<GroupDetails> {

    R saveGroup(GroupDetailsDTO groupDetailsDTO);

    R updateGroup(GroupDetailsUpdateDTO groupDetailsUpdateDTO);

    R cusRemove(Integer id);

    IPage getGroup(Page page, Integer companyId, Integer id);

    String uploadLogo(MultipartFile file, Integer groupId) throws IOException;

    R updateLogUrlByGroupId(String fileName, Integer groupId);

    R getGroupData(Page page,Integer companyId, Integer id,String groupName);

    R saveGroupByAdmin(Integer companyId, String groupName);

    R updateGroupByAdmin(Integer id, String groupName, Integer companyId);

    R getAllGroups(Page page, String groupName);

    R addGroup2(String headPortrait,
                String name,
                Integer[] managerIds,
                Integer[] employeesIds);

    /* 組名字在該公司的存在性，true表示已存在 */
    Boolean judgeGroupNameInCompany(String name, Integer companyId);

    /* 組logo oss存儲 */
    String logoSave(MultipartFile logo);

    R updateGroup2(Integer groupId, String headPortrait, String name, Integer[] managerIds, Integer[] employeesIds);
}
