package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Author su
 * @Date 2021/4/23 10:30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("notification_of_request_for_change_of_address")
public class NotificationOfRequestForChangeOfAddress
        extends Model<NotificationOfRequestForChangeOfAddress> {

    private Integer id;                  //主键
    private Long number;                 //订单编号
    private String name;
    private String phone;
    private String phPrefix;
    private String address;
    private Float lat;
    private Float lng;
    private LocalDateTime requestDateTime;  //请求修改的时间
    private Boolean result;                 //保洁员的处理结果，true同意 false拒绝

}
