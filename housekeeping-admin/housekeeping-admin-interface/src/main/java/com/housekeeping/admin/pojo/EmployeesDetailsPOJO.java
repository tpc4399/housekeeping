package com.housekeeping.admin.pojo;

import com.housekeeping.admin.dto.AddressDetailsDTO;
import com.housekeeping.admin.entity.EmployeesDetails;
import com.housekeeping.admin.entity.SysJobContend;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 保洁员的相关信息
 * @Author su
 * @Date 2021/4/16 4:55
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeesDetailsPOJO {

    private Integer employeesId;        /* 保洁员的id */
    private String name;                /* 保洁员姓名 */
    private LocalDate dateOfBirth;        /* 出生年月日 */
    private String workYear;            /* 工作年限 */
    private Integer numberOfOrder;      /* 接单次数 */
    private String headUrl;           /* 头像地址 */
    private Float starRating;           /* 评价星级 */
    private AddressDetailsDTO addressDTO; /* 地址 */
    private String workingArea;   /* 期望工作区域 */
    private BigDecimal hourlyWage;      /* 时薪 */
    private String code;
    private String instances;           /* 距离 */
    private List<SysJobContend> skillTags;/* 技能标签 */
    private Integer certified;          /* 1个体户 2工作室 3公司 */

    public EmployeesDetailsPOJO(EmployeesDetails ed){
        workingArea = ed.getWorkingArea();
        employeesId = ed.getId();
        name = ed.getName();
        dateOfBirth = ed.getDateOfBirth();
        workYear = ed.getWorkYear();
        numberOfOrder = ed.getNumberOfOrders();
        headUrl = ed.getHeadUrl();
        starRating = ed.getStarRating();
        addressDTO = new AddressDetailsDTO(ed.getAddress2()+ed.getAddress3()+ed.getAddress4(), new Float(ed.getLng()), new Float(ed.getLat()));
    }
}
