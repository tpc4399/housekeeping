package com.housekeeping.admin.vo;

import com.housekeeping.admin.entity.WorkDetails;
import com.housekeeping.admin.pojo.OrderDetailsPOJO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WorkClockVO {

    private Integer id;                 //主鍵id
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

    private WorkDetails workDetails;

    private OrderDetailsPOJO orderDetails;

    public WorkClockVO(Integer id, Integer workStatus, Integer toWorkStatus, LocalDateTime toWorkTime, Integer offWorkStatus, LocalDateTime offWorkTime, String photos, String staffSummary, Integer customerStarRating, String customerPhoto, String customerEvaluation, WorkDetails workDetails, OrderDetailsPOJO orderDetails) {
        this.id = id;
        this.workStatus = workStatus;
        this.toWorkStatus = toWorkStatus;
        this.toWorkTime = toWorkTime;
        this.offWorkStatus = offWorkStatus;
        this.offWorkTime = offWorkTime;
        this.photos = photos;
        this.staffSummary = staffSummary;
        this.customerStarRating = customerStarRating;
        this.customerPhoto = customerPhoto;
        this.customerEvaluation = customerEvaluation;
        this.workDetails = workDetails;
        this.orderDetails = orderDetails;
    }
}
