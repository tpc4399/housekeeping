package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.EmployeesDetailsDTO;
import com.housekeeping.admin.dto.PageOfEmployeesDTO;
import com.housekeeping.admin.dto.PageOfEmployeesDetailsDTO;
import com.housekeeping.admin.entity.EmployeesDetails;
import com.housekeeping.admin.vo.EmployeesHandleVo;
import com.housekeeping.common.utils.R;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

public interface EmployeesDetailsService extends IService<EmployeesDetails> {
    R saveEmp(EmployeesDetailsDTO employeesDetailsDTO,String type);
    R updateEmp(EmployeesDetailsDTO employeesDetailsDTO);
    R cusPage(Page page, PageOfEmployeesDetailsDTO pageOfEmployeesDetailsDTO, String type);
    R getLinkToLogin(Integer id, Long h) throws UnknownHostException;
    R cusPage1(Page page, PageOfEmployeesDTO pageOfEmployeesDTO, String type);
    String uploadHead(MultipartFile file, Integer id) throws IOException;
    R updateHeadUrlByUserId(String headUrl, Integer id);
    R canSheMakeAnWork(Integer employeesId);
    R blacklist(Integer employeesId, Boolean action);
    Boolean isMe(Integer employeesId);

    R cusRemove(Integer employeesId);

    public List<Integer> getAllIdsByCompanyId(Integer companyId);

    /* 根据employeesId判断员工是否存在于系统,存在true，不存在false    (全数据检索)*/
    Boolean judgmentOfExistence(Integer employeesId);

    /* 根据employeesId判断员工是否存在于公司,存在true，不存在false     (本公司检索)*/
    Boolean judgmentOfExistenceFromCompany(Integer employeesId);

    /* 根据employeesId判断员工是否存在于经理所在公司,存在true，不存在false   (本公司检索) */
    Boolean judgmentOfExistenceFromManager(Integer employeesId);

    /* 根据employeesId判断员工是否管辖于经理,存在true，不存在false   (本组检索) */
    Boolean judgmentOfExistenceHaveJurisdictionOverManager(Integer employeesId);

    /* 设置预设工作内容 */
    void setPresetJobIds(String presetJobIds, Integer employeesId);

    R putWorkArea(List<Integer> areaIds);

    R getInfoById();

    R getAllEmpByCompanyId(Integer companyId);

    R getDetailById(Integer empId);

    IPage getAllEmployeesByAdmin(Page page, PageOfEmployeesDTO pageOfEmployeesDTO);
}
