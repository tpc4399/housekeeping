package com.housekeeping.admin.pojo;

import com.housekeeping.admin.entity.SysJobContend;
import com.housekeeping.admin.entity.SysJobNote;
import lombok.Data;

import java.util.List;

/**
 * @Author su
 * @Date 2021/5/17 22:48
 */
@Data
public class OrderDetailsParent {
    private List<SysJobContend> jobs; //工作内容
    private List<SysJobNote> notes; //工作筆記
    private String customerHeadUrl; //客户头像
    private String employeesHeadUrl; //保洁员头像
    private String addressEmployees;  //保洁员的地址
    private Float lngEmployees;  //经度
    private Float latEmployees;  //纬度
    private WorkDetailsPOJO wdp;   //第一天的安排
    private Boolean yes1; // 客户是否已评价, 当订单处于待评价状态时，值有效
    private Boolean yes2; // 保洁员是否已评价, 当订单处于待评价状态时，值有效
}
