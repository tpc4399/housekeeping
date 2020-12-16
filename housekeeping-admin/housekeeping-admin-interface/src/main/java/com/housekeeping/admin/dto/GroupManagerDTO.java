package com.housekeeping.admin.dto;



import lombok.Data;

import java.util.List;

/**
 * @Author su
 * @create 2020/11/25 14:23
 */
@Data

public class GroupManagerDTO {

    private Integer groupId;        /* 组_id */
    private List<Integer> managerId;    /* 经理_id */

}
