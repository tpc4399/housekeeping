package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @Author su
 * @Date 2021/4/28 16:04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("order_photos")
@AllArgsConstructor
@NoArgsConstructor
public class OrderPhotos extends Model<OrderPhotos> {

    private Integer id;
    private Long number;      /* 订单编号 */
    private String photoUrl;  //照片url
    private String evaluate;  //照片评论

}
