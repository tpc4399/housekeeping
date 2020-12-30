package com.housekeeping.admin.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author su
 * @Date 2020/12/30 15:58
 */
@Data
public class RulesContractorVo {
    private Integer jobContendId;           /* 工作内容_id */
    private LocalDateTime startTime;        /* 预期开始时间 */
    private LocalDateTime endTime;          /* 期望完成时间 */
}
