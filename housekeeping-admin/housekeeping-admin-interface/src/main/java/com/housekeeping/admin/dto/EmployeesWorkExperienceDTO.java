package com.housekeeping.admin.dto;

import lombok.Data;

/**
 * @Author su
 * @Date 2020/12/2 10:01
 */
@Data
public class EmployeesWorkExperienceDTO {

    private Integer id;         /* 主键 */
    private String dateStart;   /* 开始时间年月 */
    private String dateEnd;     /* 结束时间年月 */
    private String jobs;        /* 岗位 */
    private String contends;    /* 工作内容 */

}
