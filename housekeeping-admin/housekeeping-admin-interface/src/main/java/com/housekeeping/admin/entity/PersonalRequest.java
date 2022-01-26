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
@TableName("personal_request")
public class PersonalRequest extends Model<PersonalRequest> {

    @TableId(type= IdType.AUTO)
    private Integer id;

    private Integer personalId;

    private Integer type;  //1工作室  2公司

    private String personalReason;

    private String companyName;

    private String companyNumber;

    private String legalName;

    private String phonePrefix;

    private String phone;

    private String registerAddress;

    private String enclosure;

    private Integer status;     //0申請中  1已拒絕  2已同意

    private String adminReason;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
