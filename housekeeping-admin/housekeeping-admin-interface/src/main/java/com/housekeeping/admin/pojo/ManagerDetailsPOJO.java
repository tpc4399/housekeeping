package com.housekeeping.admin.pojo;

import com.housekeeping.admin.entity.ManagerDetails;
import lombok.Data;

import java.util.List;

/**
 * @Author su
 * @create 2021/5/27 14:51
 */
@Data
public class ManagerDetailsPOJO {
    private ManagerDetails md;
    private List<Integer> roles;
}
