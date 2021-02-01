package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.housekeeping.admin.dto.AddEmployeesContractDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author su
 * @Date 2021/1/30 16:20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("employees_contract")
public class EmployeesContract extends Model<EmployeesContract> {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;             /* 主键 */
    private Integer employeesId;    /* 保洁员_id */
    private Integer type;           /* 包工类型 */
    private String name;            /* 名称 */
    private String description;     /* 包工描述 */
    private String photoUrls;       /* 包工照片urls */
    private Float weekWage;         /* 周价格 */
    private String code;            /* 周价格货币编码 */
    private String activityIds;     /* 参与活动_ids */

    public EmployeesContract() {
    }

    public EmployeesContract(AddEmployeesContractDTO dto) {
        this.employeesId = dto.getEmployeesId();
        this.type = dto.getType();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.weekWage = dto.getWeekWage();
        this.code = dto.getCode();
    }
}
