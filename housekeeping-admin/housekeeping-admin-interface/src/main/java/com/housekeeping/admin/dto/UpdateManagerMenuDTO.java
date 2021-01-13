package com.housekeeping.admin.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author su
 * @Date 2021/1/13 17:51
 */
@Data
public class UpdateManagerMenuDTO {
    private Integer managerId;
    private List<Integer> menuIds;
}
