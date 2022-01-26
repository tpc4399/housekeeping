package com.housekeeping.admin.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author su
 * @create 2021/6/10 15:40
 */
@Data
public class SetOrderJobsDTO {

    private String number;                        //订单编号
    private List<Integer> jobIds;                 //工作内容

}
