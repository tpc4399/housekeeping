package com.housekeeping.admin.vo;

import lombok.Data;

@Data
public class SearchEmployees {
    private String number;    /* 員工编号 */
    private String name;    /* 員工姓名 */
    private String phone;    /* 手機號 */
    private String email;    /* 郵箱 */
    private String presetJobIds;/* 预设工作内容 */
    private Integer companyId;  /* 所屬公司id */
    private String address2;/* 市 */
    private String address3;/* 區 */
    private String educationBackground;    /* 學歷 */
    private String accountLine;    /* line賬號 */
    private Boolean sex;    /* 性別 */
}
