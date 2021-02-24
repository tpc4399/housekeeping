package com.housekeeping.admin.dto;


import com.housekeeping.admin.vo.CompanyAdvertisingVo;
import lombok.Data;

@Data
public class CompanyAdvertisingAdminVo {

    private Integer companyId;
    private String title;           /* 標題 */
    private String link;            /* 鏈接 */
    private String content;         /* 内容 */
    private String photo;           /* 圖片 */
    private Integer day;            /* 選擇推廣天數 */
}
