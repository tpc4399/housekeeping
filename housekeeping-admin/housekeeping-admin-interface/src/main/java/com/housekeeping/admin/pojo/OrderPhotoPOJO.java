package com.housekeeping.admin.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author su
 * @Date 2021/4/22 17:25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderPhotoPOJO {

    private Integer orderPhotoId; //工作重点的id
    private String photoUrl;  //照片url
    private String evaluate;  //照片评论

}
