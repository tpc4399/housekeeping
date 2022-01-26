package com.housekeeping.admin.dto;

import com.housekeeping.admin.entity.SysJobNote;
import lombok.Data;

import java.util.List;

@Data
public class JobDTO {
    private Integer id;
    private String contend;
    private Boolean servicePlace;
    private Boolean area;
    private Boolean home;
    private Integer flat;
    private Integer hour;
    private Integer companyPrice;
    private Integer personalPrice;
    private Boolean status;
    private List<SysJobNote> noteIds;
}
