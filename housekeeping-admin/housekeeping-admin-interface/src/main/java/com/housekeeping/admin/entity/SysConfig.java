package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author su
 * @Date 2021/2/23 10:40
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_config")
public class SysConfig extends Model<SysConfig> {

//    private Float matchmakingFee;                       /* 媒合費百分比 */
//    private Boolean matchmakingFeeSwitch;               /* 媒合費开关 */
//    private Float systemServiceFee;                     /* 系统服务费百分比 */
//    private Boolean systemServiceFeeSwitch;             /* 系统服务费开关 */
//    private Float servicesChargeForCreditCard;          /* 刷卡手续费百分比 */
//    private Boolean servicesChargeForCreditCardSwitch;  /* 刷卡手续费开关 */
//    private Boolean scaleFeeSwitch;                     /* 公司规模费开关 */

}
