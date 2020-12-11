package com.housekeeping.admin.dto;

import lombok.Data;

/**
 * @Author su
 * @Date 2020/12/4 17:47
 */
@Data
public class PageOfManagerDetailsDTO {

    private Integer id; /* 經理id */
    private String name;    /* 經理姓名 */
    private String accountLine;    /* line賬號 */

}
