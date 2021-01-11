package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
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

    private Integer id;
    private Integer employeesId;
    private Integer jobId;

}
