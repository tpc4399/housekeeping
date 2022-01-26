package com.housekeeping.admin.dto;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

/**
 * @Author su
 * @create 2021/5/7 16:45
 */
public class EmployeesDetailsAddDTO {

    private String name;    /* 員工姓名 */
    private Boolean sex;    /* 性別 */
    private LocalDate dateOfBirth;    /* 員工生日 */
    private String idCard;  /* 身份證號碼 */
    private String address1;    /* 省 */
    private String address2;    /* 市 */
    private String address3;    /* 區 */
    private String address4;    /* 詳細地址 */
    private Float lng;          /* 经度 */
    private Float lat;          /* 纬度 */
    private String educationBackground; /* 学历，直接传本科 */
    private String phonePrefix; /* 手机号前缀 */
    private String phone;    /* 手機號 */
    private String accountLine;    /* line賬號 */
    private String describes;    /* 描述 */
    private String workYear;     /* 工作年限 */
    private List<EmployeesWorkExperienceDTO> workExperiencesDTO; /* 工作经验 */
    private List<Integer> jobIds;   /* 工作内容 */
    private MultipartFile image;    /* 头像 */
}
