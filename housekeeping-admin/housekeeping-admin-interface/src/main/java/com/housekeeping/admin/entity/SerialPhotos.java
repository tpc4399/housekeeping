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
 * @create 2021/5/26 11:38
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("serial_photos")
@AllArgsConstructor
@NoArgsConstructor
public class SerialPhotos extends Model<SerialPhotos> {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String serialNumber;//流水号
    private String photoUrl;    //照片url
    private String evaluate;    //照片评论

}
