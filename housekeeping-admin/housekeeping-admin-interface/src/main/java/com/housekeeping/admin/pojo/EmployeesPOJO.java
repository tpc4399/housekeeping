package com.housekeeping.admin.pojo;

import lombok.Data;

/**
 * @Author su
 * @create 2021/5/9 12:05
 */
@Data
public class EmployeesPOJO {

    private Float scope; //保洁员的分数
    private EmployeesDetailsPOJO employeesDetailsPOJO; //保洁员的信息

}
