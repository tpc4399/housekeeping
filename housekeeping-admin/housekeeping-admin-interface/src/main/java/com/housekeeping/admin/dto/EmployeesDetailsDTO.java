package com.housekeeping.admin.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * @Author su
 * @create 2020/11/22 22:20
 */
@Data
public class EmployeesDetailsDTO {

    private Integer id;    /* 主鍵id */
    private String name;    /* 員工姓名 */
    private LocalDate dateOfBirth;    /* 員工生日 */
    private String phone;    /* 手機號 */
    private String email;    /* 郵箱 */
    private String address;    /* 地區 */
    private String describes;    /* 描述 */
    private Boolean sex;    /* 性別 */

}
