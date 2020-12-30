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
    private Integer id;         /* 主鍵id */
    private String contend;     /* 工作內容（标签） */
    private Integer type;       /* 0 钟点服务  1 包工服务天 2 包工服务次 */
    private Integer companyId;  /* 所属公司_id */
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer lastReviserId;

}
