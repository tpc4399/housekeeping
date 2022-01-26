package com.housekeeping.admin.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author su
 * @Date 2021/3/22 16:00
 */
@Data
public class AddJobContendDTO {
    private Integer id;             /* 工作内容_id */
    private String contend;         /* 工作內容（标签） */
    private Boolean servicePlace;
    private Boolean area;
    private Boolean home;
    private List<Integer> notes;                 /* 工作笔记_ids */
}
