package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.EmployeesDetailsDTO;
import com.housekeeping.admin.dto.EmployeesWorkExperienceDTO;
import com.housekeeping.admin.dto.PageOfEmployeesDTO;
import com.housekeeping.admin.dto.PageOfEmployeesDetailsDTO;
import com.housekeeping.admin.entity.EmployeesDetails;
import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.utils.R;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.UnknownHostException;
import java.time.LocalDate;
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

    /* 判斷員工存在性 */
    Boolean judgeEmployeesInCompany(Integer employeesId, Integer companyId);

    /* 设置预设工作内容 */
    void setPresetJobIds(String presetJobIds, Integer employeesId);

    R putWorkArea(List<Integer> areaIds);

    R getInfoById();

    R getAllEmpByCompanyId(Integer companyId);

    R getDetailById(Integer empId);

    R getAllEmployeesByAdmin(Page page, PageOfEmployeesDTO pageOfEmployeesDTO);

    @Access(RolesEnum.USER_EMPLOYEES)
    Integer getEmployeesIdByExistToken();

    /* 根据保洁员userId快速获取employeesId (经常用到) */
    Integer getEmployeesIdByUserId(Integer userId);

    R cusPage5(Page page, PageOfEmployeesDetailsDTO pageOfEmployeesDetailsDTO);

    R getGroupByEmpId(Integer employeesId);

    String setHeader(MultipartFile image);
    R addEmp(String name,
             Boolean sex,
             LocalDate dateOfBirth,
             String idCard,
             String address1,
             String address2,
             String address3,
             String address4,
             Float lng,
             Float lat,
             String educationBackground,
             String phonePrefix,
             String phone,
             String accountLine,
             String describes,
             String workYear,
             List<EmployeesWorkExperienceDTO> workExperiencesDTO,
             List<Integer> jobIds,
             MultipartFile image);

    R getEmployeesByIds(List<Integer> ids);
}
