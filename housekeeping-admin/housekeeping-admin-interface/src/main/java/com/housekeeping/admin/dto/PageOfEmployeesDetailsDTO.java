package com.housekeeping.admin.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * @Author su
 * @Date 2020/12/4 17:47
 */
@Data
public class PageOfEmployeesDetailsDTO {

    private String number;    /* 員工編號 */
    private String name;    /* 員工姓名 */
    private Boolean sex;    /* 性別 */
    private LocalDate dateOfBirth;    /* 員工生日 */
    private String idCard;  /* 身份證號碼 */
    private String address1;    /* 省 */
    private String address2;    /* 市 */
    private String address3;    /* 區 */
    private String address4;    /* 詳細地址 */
    private String recordOfFormalSchooling; /* 学历，直接传本科 */
    private String phone;    /* 手機號 */
    private String accountLine;    /* line賬號 */
    private String describes;    /* 描述 */

}
