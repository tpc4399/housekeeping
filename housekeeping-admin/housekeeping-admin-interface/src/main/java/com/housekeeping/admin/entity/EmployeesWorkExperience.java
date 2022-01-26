package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author su
 * @Date 2020/12/2 9:34
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("employees_work_experience")
public class EmployeesWorkExperience extends Model<EmployeesWorkExperience> {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;         /* 主键 */
    private Integer employeesId;/* 员工_id */
    private String workYear;
    private String contends;    /* 工作内容 */

}
