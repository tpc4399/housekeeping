package com.housekeeping.admin.vo;

import com.housekeeping.admin.entity.SysJobNote;
import lombok.Data;

import java.util.List;

@Data
public class JobContendVO {

    private Integer id;         /* 主鍵id */
    private String contend;     /* 工作內容（标签） */
    private List<SysJobNote> notes;   /* 工作笔记 */

}
