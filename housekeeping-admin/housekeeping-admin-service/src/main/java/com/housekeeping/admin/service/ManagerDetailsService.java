package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.ManagerDetailsDTO;
import com.housekeeping.admin.dto.PageOfManagerDTO;
import com.housekeeping.admin.dto.PageOfManagerDetailsDTO;
import com.housekeeping.admin.entity.ManagerDetails;
import com.housekeeping.common.utils.R;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

public interface ManagerDetailsService extends IService<ManagerDetails> {
    R saveEmp(ManagerDetailsDTO managerDetailsDTO);
    R updateEmp(ManagerDetailsDTO managerDetailsDTO);
    R getLinkToLogin(Integer id, Long h) throws UnknownHostException;
    Integer getCompanyIdByManagerId(Integer managerId);
    R cusPage1(Page page, PageOfManagerDTO pageOfEmployeesDTO, String type);
    R cusPage(Page page, PageOfManagerDetailsDTO pageOfEmployeesDetailsDTO, String type);
    String uploadHead(MultipartFile file, Integer id) throws IOException;
    R updateHeadUrlByUserId(String headUrl, Integer id);
    List<Integer> getManIdsByCompId(Integer id);
    /* 判断该保洁员受不受到我管辖 */
    Boolean thereIsACleaner(Integer employeesId);

    R cusRemove(Integer managerId);
    R getAllByCompanyUserId(Integer companyUserId);

    R getInfoById();

    R getAllManagerByAdmin(Page page, PageOfManagerDTO pageOfEmployeesDTO);

    List<Integer> getAllUserIdByCompanyId(Integer companyId);

    Boolean judgeManagerInCompany(Integer managerId, Integer companyId);
}
