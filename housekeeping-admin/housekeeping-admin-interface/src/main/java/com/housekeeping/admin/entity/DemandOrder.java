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
import java.time.LocalDate;

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
    private Integer serverPlaceType;            /* 服务场所类型  0住宿与交际 1洗浴与美容 2文化娱乐 3体育与游乐 4文化交流 5购物 6就诊与交通 7其它*/
    private String note;                        /* 备注 */
    private String jobIds;                      /* 被选中的工作内容标签 */
    private String housingArea;                 /* 房屋面积 */
    private BigDecimal estimatedSalary;         /* 预计薪资 */
    private String code;                        /* 薪资货币代码 */
    private LocalDate startDate;                /* 开始日期 */
    private LocalDate endDate;                  /* 结束日期 */
    private String week;                        /* 重复星期 */
    private Integer status;                     /* 状态 */
    /* 定期服务时间安排见details */

}
