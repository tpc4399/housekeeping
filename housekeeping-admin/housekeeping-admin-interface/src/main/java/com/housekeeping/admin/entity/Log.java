package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("log")
public class Log extends Model<Log> {

    /* 主键 */
    private String id;

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

    /* 创建时间 */
    private LocalDateTime createTime;

    /* 删除标记 */
    private Integer delFlag;
}
