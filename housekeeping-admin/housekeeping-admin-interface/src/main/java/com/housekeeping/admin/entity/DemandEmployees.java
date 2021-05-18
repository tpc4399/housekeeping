package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("demand_employees")
public class DemandEmployees extends Model<DemandEmployees> {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;                     /* 主键 */
    private Integer userId;                 /* 添加保洁员到需求单的用户id */
    private Integer employeesId;            /* 被添加到需求单的员工id */
    private Integer demandOrderId;          /* 需求单id */
    private Integer status;                 /* 客户确认状态（0未确认 1已确认） */
    private Integer readStatus;             /* 已读未读状态 （0未读 1已读）*/
    private Integer price;                  /* 价格 */
    private LocalDateTime createTime;       /* 创建时间 */
    private LocalDateTime updateTime;       /* 修改时间 */
    private String orderNumber;             /* 订单编号 */
}
