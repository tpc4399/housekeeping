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
    private Integer type;           /* 0: 單次鐘點    1： 定期服務    2: 包工服務 */
    private List<SysJobContendSonVo> contends;  /* 工作内容 */
}
