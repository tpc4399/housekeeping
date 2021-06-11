package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private Long number;      /* 订单编号 */
    private String photoUrl;  //照片url
    private String evaluate;  //照片评论
    private Boolean yes;  //是否已进行回传
    private String resultPhotoUrls; //工作重点回传照片
    private String result; //工作重点回传
    private LocalDateTime resultTime; //工作重点回传时间

    public OrderPhotos(Integer id, Long number, String photoUrl, String evaluate){
        this.id = id;
        this.number = number;
        this.photoUrl = photoUrl;
        this.evaluate = evaluate;
    }

}
