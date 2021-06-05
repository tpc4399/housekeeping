package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("company_advertising")
public class CompanyAdvertising extends Model<CompanyAdvertising> {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;             /* 主鍵id */
    private Integer companyId;      /* 公司id */
    private Integer typeId;         /* 广告类型 */
    private String title;           /* 標題 */
    private String link;            /* 鏈接 */
    private String content;         /* 内容 */
    private String photo;           /* 圖片 */
    private Boolean promotion;      /* 推廣 0未推廣 1已推廣 */
    private LocalDateTime endTime;  /* 推廣結束日期 */

}
