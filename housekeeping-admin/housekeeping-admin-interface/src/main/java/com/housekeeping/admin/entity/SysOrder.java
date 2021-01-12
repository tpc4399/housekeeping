package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 服务订单
 * @Author su
 * @create 2020/11/13 15:19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_order")
public class SysOrder extends Model<SysOrder> {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;                         /* 主键id */
    private String number;                      /* 订单编号 */
    private Integer customerId;                 /* 客户_id */
    private Integer addressId;                  /* 服务地址_id */
    private Boolean type;                       /* 订单类型：false = 需求单;true = 预约单 */
    private Integer employeesId;                /* 预约单:保洁员_id */
    private Integer serviceType;                /* 服务类型：1单次 2定期服务 3包月 */
    private LocalDateTime createTime;           /* 订单创建时间 */
    private String jobContend;                  /* 工作内容_ids */
    private String evaluationStar;              /* 评价星级 */
    private String evaluationContent;           /* 评价内容 */
    private String evaluationImage;             /* 评价多张图片 */
    private LocalDateTime evaluationTime;       /* 评价时间 */
    private Boolean evaluationType;             /* 是否到时自动评价 */
    private LocalDateTime evaluationDeadTime;   /* 评价截止时间,默认评价生效时间 */

}
