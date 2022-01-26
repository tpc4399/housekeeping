package com.housekeeping.admin.vo;

import lombok.Data;

import java.util.List;

@Data
public class WorkCheckVO {

    private Integer id;
    private List<String> staffCheck;
}
