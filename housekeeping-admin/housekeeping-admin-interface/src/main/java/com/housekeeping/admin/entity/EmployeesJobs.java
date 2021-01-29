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
    private Integer employeesId;
    private Integer jobId;
    private String timeSlot;
    private String priceSlot;

    public EmployeesJobs(){}

    public EmployeesJobs(Integer employeesId, JobsDTO jobsDTO){
        this.employeesId = employeesId;
        this.jobId = jobsDTO.getJobId();
        StringBuilder timeSlot = new StringBuilder();
        StringBuilder priceSlot = new StringBuilder();
        jobsDTO.getPriceSlot().forEach(x->{
            timeSlot.append(x.getLowH() + " ");
            priceSlot.append(x.getPrice() + " ");
        });
        timeSlot.append(jobsDTO.getPriceSlot().get(jobsDTO.getPriceSlot().size()-1).getHighH());
        this.timeSlot = timeSlot.toString().replace(" ", ",");
        this.priceSlot = priceSlot.toString().replace(" ", ",");
    }

    public EmployeesJobs(Integer employeesId, Integer jobId) {
        this.employeesId = employeesId;
        this.jobId = jobId;
    }
}
