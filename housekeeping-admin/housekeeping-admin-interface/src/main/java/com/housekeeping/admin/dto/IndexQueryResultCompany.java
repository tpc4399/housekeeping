package com.housekeeping.admin.dto;

import com.housekeeping.admin.entity.CompanyDetails;
import lombok.Data;

import java.util.List;

/**
 * @Author su
 * @Date 2021/2/17 20:34
 */
@Data
public class IndexQueryResultCompany {

    private CompanyDetails companyDetail;                               //公司详情
    private List<IndexQueryResultEmployees> matchingEmployeesDetails;   //公司下面匹配的员工详情
    private Integer recommendedValue;                                   //推荐值

}
