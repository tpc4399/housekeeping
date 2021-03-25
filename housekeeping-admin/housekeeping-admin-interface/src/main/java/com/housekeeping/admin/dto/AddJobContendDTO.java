package com.housekeeping.admin.dto;

import lombok.Data;

/**
 * @Author su
 * @Date 2021/3/22 16:00
 */
@Data
public class AddJobContendDTO {
    private Integer id;             /* 工作内容_id */
    private String contend;         /* 工作內容（标签） */
}
