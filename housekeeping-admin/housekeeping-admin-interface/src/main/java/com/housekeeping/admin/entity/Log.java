package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_log")
public class Log extends Model<Log> implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;    /* 主键 */
    private String title;    /* 标题 */
    private Integer lastReviserId;    /* 最后修改人 */
    private String remoteAddr;    /* 请求地址 */
    private String userAgent;    /* 用户代理 */
    private String requestUri;    /* 请求URI */
    private String method;    /* 请求方法 */
    private String params;    /* 请求参数 */
    private Long time;    /* 响应用时 */
    private LocalDateTime createTime;    /* 创建时间 */

}
