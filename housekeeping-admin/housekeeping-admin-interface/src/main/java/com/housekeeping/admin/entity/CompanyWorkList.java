package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 公司-工作列表
 * @Author su
 * @create 2020/11/18 16:00
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("company_work_list")
public class CompanyWorkList extends Model<CompanyWorkList> {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;         /* 主键id */
    private Integer groupId;    /* 分组_id */
    private Integer orderId;    /* 订单_id */
    private LocalDateTime createTime;   /* 创建时间 */
    private Integer lastReviserId;      /* 最后修改人、创建人 */
}
