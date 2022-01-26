package com.housekeeping.admin.vo;

import com.housekeeping.admin.entity.SysJobNote;
import lombok.Data;

import java.util.List;

@Data
public class ContentVO {

    private String name;
    private List<SysJobNote> notes;
}
