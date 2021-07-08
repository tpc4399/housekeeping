package com.housekeeping.admin.dto;

import com.housekeeping.admin.entity.SysJobNote;
import lombok.Data;

import java.util.List;

@Data
public class JobDTO {
    private Integer id;
    private String content;
    private List<SysJobNote> noteIds;
}
