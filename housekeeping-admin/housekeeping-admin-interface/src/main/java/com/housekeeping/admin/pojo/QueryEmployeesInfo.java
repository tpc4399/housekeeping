package com.housekeeping.admin.pojo;

import com.housekeeping.admin.entity.EmployeesDetails;
import com.housekeeping.admin.entity.SysJobContend;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author su
 * @create 2021/5/18 17:12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryEmployeesInfo {

    private EmployeesDetails ed;    //保洁员详细信息
    private List<SysJobContend> jobs; //工作内容
    private String instance;        //距离

}
