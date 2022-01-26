package com.housekeeping.admin.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author su
 * @create 2021/5/28 16:25
 */
@Data
public class SetOrderWorkDetailsDTO {

    private String number;                        //订单编号
    private List<SetWorkDetailsDTO> workDetails;  //订单安排详情 (工作内容、时间安排)

}
