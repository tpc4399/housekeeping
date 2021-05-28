package com.housekeeping.admin.dto;

import com.housekeeping.admin.pojo.WorkDetailsPOJO;

import java.util.List;

/**
 * @Author su
 * @create 2021/5/28 16:25
 */
public class SetOrderWorkDetailsDTO {

    private String number;                      //订单编号
    private List<WorkDetailsPOJO> workDetails;  //订单安排详情 (工作内容、时间安排)


}
