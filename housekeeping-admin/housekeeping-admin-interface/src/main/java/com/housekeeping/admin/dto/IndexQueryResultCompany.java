package com.housekeeping.admin.dto;

import com.housekeeping.admin.entity.CompanyDetails;
import lombok.Data;

import java.util.List;

/**
 * 被匹配的公司
 * @Author su
 * @Date 2021/2/17 20:34
 */
@Data
public class IndexQueryResultCompany {

    private CompanyDetails companyDetail;                               //公司详情
    private List<IndexQueryResultEmployees> matchingEmployeesDetails;   //公司下面匹配的员工详情
    private Integer recommendedValue;                                   //推荐值 棄用
    private Double recommendedScope;                                    //推荐分数
    private Boolean certified;                                          /* 公司是否已認證 */

}
