package com.housekeeping.admin.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author su
 * @create 2021/5/9 12:03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndexResultPOJO {

    private Boolean type;                   //true保洁员 false公司
    private EmployeesPOJO employees;        //保洁员信息
    private CompanyPOJO company;            //公司信息

    public IndexResultPOJO(EmployeesPOJO employees) {
        this.employees = employees;
        this.type = true;
    }

    public IndexResultPOJO(CompanyPOJO company) {
        this.company = company;
        this.type = false;
    }
}
