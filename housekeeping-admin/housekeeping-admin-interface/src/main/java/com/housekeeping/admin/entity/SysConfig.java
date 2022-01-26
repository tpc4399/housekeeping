package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @Author su
 * @Date 2021/2/23 10:40
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_config")
public class SysConfig extends Model<SysConfig> {

//    private Float matchmakingFee;                       /* 媒合費百分比 */
//    private Boolean matchmakingFeeSwitch;               /* 媒合費开关 */
//    private Float systemServiceFee;                     /* 系统服务费百分比 */
//    private Boolean systemServiceFeeSwitch;             /* 系统服务费开关 */
//    private Float servicesChargeForCreditCard;          /* 刷卡手续费百分比 */
//    private Boolean servicesChargeForCreditCardSwitch;  /* 刷卡手续费开关 */
//    private Boolean scaleFeeSwitch;                     /* 公司规模费开关 */

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;             /* 主键 */
    private String configKey;       /* 键 */
    private String configValue;     /* 值 */
    private String description;     /* 描述 */

}
