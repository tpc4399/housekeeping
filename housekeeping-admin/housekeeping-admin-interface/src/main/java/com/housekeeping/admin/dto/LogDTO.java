package com.housekeeping.admin.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author su
 * @create 2020/10/26 21:25
 */
@Data
public class LogDTO {

    /* 标题 */
    private String title;

    /* 最后修改人 */
    private Integer lastReviserId;

    /* 请求地址 */
    private String remoteAddr;

    /* 用户代理 */
    private String userAgent;

    /* 请求URI */
    private String requestUri;

    /* 请求方法 */
    private String method;

    /* 请求参数 */
    private String params;

    /* 响应用时 */
    private String time;

    /* 创建时间_1 */
    private LocalDateTime createTime1;

    /* 创建时间_2 */
    private LocalDateTime createTime2;

}
