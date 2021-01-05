package com.housekeeping.admin.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author su
 * @Date 2021/1/5 11:38
 */
@Data
public class SysJobContendVo {
    private Integer id;             /* 工作内容_id */
    private String contend;         /* 工作內容（标签） */
    private List<SysJobContendSonVo> contends;  /* 工作内容 */
}
