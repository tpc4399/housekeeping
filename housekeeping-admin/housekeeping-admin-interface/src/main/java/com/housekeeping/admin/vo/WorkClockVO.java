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
    private Integer photo1Status;
    private String photo2;              //員工上傳圖片
    private Integer photo2Status;
    private String photo3;              //員工上傳圖片
    private Integer photo3Status;
    private String photo4;              //員工上傳圖片
    private Integer photo4Status;
    private String photo5;              //員工上傳圖片
    private Integer photo5Status;
    private String staffCheck;          //员工上班检查
    private Boolean customerConfirm;    //客户确认
    private String staffSummary;        //員工總結
    private String staffPhoto;          //员工描述图片
    private Integer customerStarRating; //客戶打分
    private String customerPhoto;       //客戶圖片
    private String customerEvaluation;  //客戶評價

    private WorkDetails workDetails;

    private OrderDetailsPOJO orderDetails;

    private List<OrderPhotoVO> photos;

    public WorkClockVO(Integer id, String workProgress, Integer workStatus, Integer toWorkStatus, LocalDateTime toWorkTime, Integer offWorkStatus, LocalDateTime offWorkTime, String photo1, Integer photo1Status, String photo2, Integer photo2Status, String photo3, Integer photo3Status, String photo4, Integer photo4Status, String photo5, Integer photo5Status, String staffCheck, Boolean customerConfirm, String staffSummary, String staffPhoto, Integer customerStarRating, String customerPhoto, String customerEvaluation, WorkDetails workDetails, OrderDetailsPOJO orderDetails, List<OrderPhotoVO> photos) {
        this.id = id;
        this.workProgress = workProgress;
        this.workStatus = workStatus;
        this.toWorkStatus = toWorkStatus;
        this.toWorkTime = toWorkTime;
        this.offWorkStatus = offWorkStatus;
        this.offWorkTime = offWorkTime;
        this.photo1 = photo1;
        this.photo1Status = photo1Status;
        this.photo2 = photo2;
        this.photo2Status = photo2Status;
        this.photo3 = photo3;
        this.photo3Status = photo3Status;
        this.photo4 = photo4;
        this.photo4Status = photo4Status;
        this.photo5 = photo5;
        this.photo5Status = photo5Status;
        this.staffCheck = staffCheck;
        this.customerConfirm = customerConfirm;
        this.staffSummary = staffSummary;
        this.staffPhoto = staffPhoto;
        this.customerStarRating = customerStarRating;
        this.customerPhoto = customerPhoto;
        this.customerEvaluation = customerEvaluation;
        this.workDetails = workDetails;
        this.orderDetails = orderDetails;
        this.photos = photos;
    }
}
