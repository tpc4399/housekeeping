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
@TableName("company_promotion")
public class CompanyPromotion extends Model<CompanyPromotion> {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;             /* 主键id */
    private Integer companyId;      /* 公司id */
    private Integer promotion;      /* 是否推广 默认0未推广 1已推广 */
    private LocalDateTime startTime;/* 开始时间 */
    private String days;            /* 天数 */
    private LocalDateTime endTime;  /* 结束时间 */
    private Integer count;          /* 推广次数 */
    private Integer lastReviserId;  /* 最后修改人id */
}
