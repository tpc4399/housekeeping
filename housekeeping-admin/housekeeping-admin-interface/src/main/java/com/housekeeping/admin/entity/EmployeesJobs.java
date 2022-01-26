package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.housekeeping.admin.dto.JobsDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author su
 * @Date 2021/1/11 10:20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("employees_jobs")
public class EmployeesJobs extends Model<EmployeesJobs> {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    private Integer employeesId;/* 保洁员_id */
    private Integer jobId;      /* 工作內容_id */


}
