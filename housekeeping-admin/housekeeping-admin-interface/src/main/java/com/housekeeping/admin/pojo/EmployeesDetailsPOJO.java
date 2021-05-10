package com.housekeeping.admin.pojo;

import com.housekeeping.admin.dto.AddressDetailsDTO;
import com.housekeeping.admin.entity.SysJobContend;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 保洁员的相关信息
 * @Author su
 * @Date 2021/4/16 4:55
 */
@Data
public class EmployeesDetailsPOJO {

    private Integer employeesId;        /* 保洁员的id */
    private String name;                /* 保洁员姓名 */
    private LocalDate birthDate;        /* 出生年月日 */
    private String workYear;            /* 工作年限 */
    private Integer numberOfOrder;      /* 接单次数 */
    private String headerUrl;           /* 头像地址 */
    private Float starRating;           /* 评价星级 */
    private AddressDetailsDTO addressDTO; /* 地址 */
    private BigDecimal hourlyWage;      /* 时薪 */
    private String code;
    private String instances;            /* 距离 */
    private List<SysJobContend> skillTags;/* 技能标签 */

}
