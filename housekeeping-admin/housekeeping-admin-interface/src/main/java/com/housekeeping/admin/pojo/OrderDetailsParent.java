package com.housekeeping.admin.pojo;

import com.housekeeping.admin.entity.SysJobContend;
import lombok.Data;

import java.util.List;

/**
 * @Author su
 * @Date 2021/5/17 22:48
 */
@Data
public class OrderDetailsParent {
    private List<SysJobContend> jobs; //工作内容
    private String customerHeaderUrl; //客户头像
    private String employeesHeaderUrl; //保洁员头像
    private String addressEmployees;  //保洁员的地址
    private Float lngEmployees;  //经度
    private Float latEmployees;  //纬度
}
