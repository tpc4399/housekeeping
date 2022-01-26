package com.housekeeping.admin.dto;

import com.housekeeping.admin.entity.EmployeesContract;
import com.housekeeping.admin.entity.EmployeesDetails;
import com.housekeeping.admin.entity.Skill;
import com.housekeeping.admin.entity.SysJobContend;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class EmployeesContractDTO {

    private Integer id;             /* 主键 */
    private Integer employeesId;    /* 保洁员_id */
    private String jobs;            /* 包工的可选工作内容 */
    private String name;            /* 名称 */
    private String description;     /* 包工描述 */
    private String photoUrls;       /* 包工照片urls */
    private Float dayWage;          /* 天价格 */
    private String code;            /* 天价格货币编码 */
    private String activityIds;     /* 参与活动_ids */
    private Integer dateLength;     /* 日期长度：天数 */
    private Float timeLength;       /* 时间长度：每日时长 */
    private BigDecimal totalPrice;  /* 总价格 */
    private List<Skill> jobContends;
    private EmployeesDetails employeesDetails;

    public EmployeesContractDTO(EmployeesContract employeesContract) {
        this.id = employeesContract.getId();
        this.employeesId = employeesContract.getEmployeesId();
        this.jobs = employeesContract.getJobs();
        this.name = employeesContract.getName();
        this.description = employeesContract.getDescription();
        this.photoUrls = employeesContract.getPhotoUrls();
        this.dayWage = employeesContract.getDayWage();
        this.code = employeesContract.getCode();
        this.activityIds = employeesContract.getActivityIds();
        this.dateLength = employeesContract.getDateLength();
        this.timeLength = employeesContract.getTimeLength();
        this.totalPrice = employeesContract.getTotalPrice();
    }
}
