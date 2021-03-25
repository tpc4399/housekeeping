package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 需求单订单
 * @Author su
 * @Date 2021/3/2 16:08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("demand_order")
@AllArgsConstructor
@NoArgsConstructor
public class DemandOrder extends Model<DemandOrder> {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;                         /* 主键id */
    private Integer customerId;                 /* 需求单来源：客户_Id */
    private Boolean liveAtHome;                 /* 是否需要住宿 */
    private String jobIds;                      /* 被选中的工作内容标签 */
    private String housingArea;                 /* 房屋面积 */
    private BigDecimal estimatedSalary;         /* 预计薪资 */
    private String code;                        /* 薪资货币代码 */

    /* 定期服务时间安排见details */

}
