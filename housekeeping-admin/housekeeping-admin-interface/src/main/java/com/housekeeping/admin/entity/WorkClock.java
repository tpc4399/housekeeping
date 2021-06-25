package com.housekeeping.admin.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("work_clock")
public class WorkClock extends Model<WorkClock> {

    @TableId(type = IdType.AUTO)
    private Integer id;                 //主鍵id
    private Integer workId;             //工作id
    private Integer workStatus;         //工作狀態 0未開始 1進行中 2已完成
    private Integer toWorkStatus;       //上班打卡狀態 0未打卡 1已打卡
    private LocalDateTime toWorkTime;   //打卡時間
    private Integer offWorkStatus;      //下班打卡狀態 0未打卡 1已打卡
    private LocalDateTime offWorkTime;  //下班打卡時間
    private String photos;              //員工上傳圖片
    private String staffSummary;        //員工總結
    private Integer customerStarRating; //客戶打分
    private String customerPhoto;       //客戶圖片
    private String customerEvaluation;  //客戶評價
}
