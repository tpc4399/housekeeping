package com.housekeeping.admin.dto;

import lombok.Data;

@Data
public class CustomerUpdateDTO {

    private Integer id; /* 主键id */
    private String name;
    private Boolean sex;
}
