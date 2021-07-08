package com.housekeeping.admin.vo;

import com.housekeeping.admin.entity.WorkDetails;
import com.housekeeping.admin.pojo.OrderDetailsPOJO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class WorkClockVO {

    private Integer id;                 //主鍵id
    private String  workProgress;       //工作进度
    private Integer workStatus;         //工作狀態 0未開始 1進行中 2已完成
    private Integer toWorkStatus;       //上班打卡狀態 0未打卡 1已打卡
    private LocalDateTime toWorkTime;   //打卡時間
    private Integer offWorkStatus;      //下班打卡狀態 0未打卡 1已打卡
    private LocalDateTime offWorkTime;  //下班打卡時間
    private String photo1;              //員工上傳圖片
    private String photo2;              //員工上傳圖片
    private String photo3;              //員工上傳圖片
    private String photo4;              //員工上傳圖片
    private String photo5;              //員工上傳圖片
    private String staffSummary;        //員工總結
    private Integer customerStarRating; //客戶打分
    private String customerPhoto;       //客戶圖片
    private String customerEvaluation;  //客戶評價

    private WorkDetails workDetails;

    private OrderDetailsPOJO orderDetails;

    private List<OrderPhotoVO> photos;

    public WorkClockVO(Integer id, String workProgress, Integer workStatus, Integer toWorkStatus, LocalDateTime toWorkTime, Integer offWorkStatus, LocalDateTime offWorkTime, String photo1, String photo2, String photo3, String photo4, String photo5, String staffSummary, Integer customerStarRating, String customerPhoto, String customerEvaluation, WorkDetails workDetails, OrderDetailsPOJO orderDetails, List<OrderPhotoVO> photos) {
        this.id = id;
        this.workProgress = workProgress;
        this.workStatus = workStatus;
        this.toWorkStatus = toWorkStatus;
        this.toWorkTime = toWorkTime;
        this.offWorkStatus = offWorkStatus;
        this.offWorkTime = offWorkTime;
        this.photo1 = photo1;
        this.photo2 = photo2;
        this.photo3 = photo3;
        this.photo4 = photo4;
        this.photo5 = photo5;
        this.staffSummary = staffSummary;
        this.customerStarRating = customerStarRating;
        this.customerPhoto = customerPhoto;
        this.customerEvaluation = customerEvaluation;
        this.workDetails = workDetails;
        this.orderDetails = orderDetails;
        this.photos = photos;
    }
}
