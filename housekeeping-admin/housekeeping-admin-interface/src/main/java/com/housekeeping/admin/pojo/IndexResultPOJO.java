package com.housekeeping.admin.pojo;

import lombok.Data;

/**
 * @Author su
 * @create 2021/5/9 12:03
 */
@Data
public class IndexResultPOJO {

    private Boolean type;                   //true保洁员 false公司
    private EmployeesPOJO employees;        //保洁员信息
    private CompanyPOJO company;            //公司信息
}
