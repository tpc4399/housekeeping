package com.housekeeping.admin.dto;

import lombok.Data;

import java.util.List;

@Data
public class JobDTO {
    private Integer id;
    private List<Integer> noteIds;
}
