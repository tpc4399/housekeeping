package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.jws.WebParam;
import java.time.LocalDateTime;

/**
 * @Author su
 * @Date 2020/12/11 16:02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_job_contend")
public class SysJobContend extends Model<SysJobContend> {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;    /* 主鍵id */
    private String contend;/* 內容 */
    private Integer type;/* 0 钟点服务  1 包工服务 */
    private Integer unit;/* 0 天  1 次 */
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer lastReviserId;

}
