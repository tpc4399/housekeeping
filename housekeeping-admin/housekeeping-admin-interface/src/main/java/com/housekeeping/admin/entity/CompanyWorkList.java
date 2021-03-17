package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @Author su
 * @create 2020/11/18 16:00
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("company_work_list")
public class CompanyWorkList extends Model<CompanyWorkList> {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;                 /* 主键id */
    private Integer companyId;          /* 公司_id */
    private Integer demandOrderId;      /* 需求订单_id */
    private LocalDateTime createTime;   /* 创建时间 */
    private Boolean temporaryOrderRequest; /* 客户是否发送临时订单请求 */
    private LocalDateTime requestTime;   /* 临时订单请求时间 */

}
