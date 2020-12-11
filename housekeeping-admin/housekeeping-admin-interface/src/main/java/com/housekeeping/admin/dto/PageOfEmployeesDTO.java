package com.housekeeping.admin.dto;

import lombok.Data;

/**
 * @Author su
 * @Date 2020/12/4 17:47
 */
@Data
public class PageOfEmployeesDTO {

    private Integer id; /* 員工id */
    private Integer companyId;  /* 公司id */
    private String name;    /* 員工姓名 */
    private String accountLine;    /* line賬號 */

}
