package com.housekeeping.admin.dto;

import lombok.Data;

/**
 * @Author su
 * @Date 2020/12/4 17:47
 */
@Data
public class PageOfManagerDTO {

    private Integer companyId;  /* 公司id */
    private Integer id; /* 經理id */
    private String name;    /* 經理姓名 */
    private String accountLine;    /* line賬號 */

}
