package com.housekeeping.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author su
 * @create 2021/6/11 17:31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeyWorkReturnDTO {

    private Integer orderPhotosId;//工作重点编号
    private String resultPhotoUrls; //工作重点回传照片
    private String result; //工作重点回传

}
