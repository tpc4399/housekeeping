package com.housekeeping.admin.vo;

import lombok.Data;

@Data
public class OrderPhotoVO {

    private Integer orderPhotoId; //工作重点的id
    private String photoUrl;  //照片url
    private String evaluate;  //照片评论
    private String empPhoto;   //员工上传图片
}
