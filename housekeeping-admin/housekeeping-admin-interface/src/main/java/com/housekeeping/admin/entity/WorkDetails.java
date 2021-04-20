package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author su
 * @Date 2021/4/20 15:40
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("work_details")
public class WorkDetails extends Model<WorkDetails> {

    private Long Number; //订单编号


}
