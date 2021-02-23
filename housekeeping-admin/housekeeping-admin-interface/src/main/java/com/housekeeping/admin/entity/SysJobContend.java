package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.jws.WebParam;
import java.time.LocalDateTime;

/**
 * @Author su
 * @Date 2020/12/11 16:02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_job_contend")
public class SysJobContend extends Model<SysJobContend> {

    private Integer id;         /* 主鍵id */
    private String contend;     /* 工作內容（标签） */
    private Integer level;      /* 1 1级分类  */
    private Integer type;       /* 0: 單次鐘點    1： 定期服務    2: 包工服務 */
    private Integer parentId;   /* 父_id */

}
