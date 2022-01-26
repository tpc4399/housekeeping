package com.housekeeping.admin.dto;

import lombok.Data;

/**
 * @Author su
 * @Date 2020/12/2 10:01
 */
@Data
public class EmployeesWorkExperienceDTO {

    private Integer id;         /* 主键 */
    private String workYear;    /* 工作经验 */
    private String contends;    /* 工作内容 */

}
