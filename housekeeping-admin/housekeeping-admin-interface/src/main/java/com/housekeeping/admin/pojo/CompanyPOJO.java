package com.housekeeping.admin.pojo;

import lombok.Data;

/**
 * @Author su
 * @create 2021/5/9 12:05
 */
@Data
public class CompanyPOJO {

    private Float scope; //公司的分数
    private CompanyDetailsPOJO companyDetailsPOJO; //公司的信息

}
