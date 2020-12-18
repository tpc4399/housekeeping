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
@TableName("group_details")
public class GroupDetails extends Model<GroupDetails> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;/* 主键id */
    private Integer companyId; /* 所屬公司id */
    private String headUrl; /* 组图像 */
    private String groupName;/* 組名 */
    private LocalDateTime createTime;/* 創建時間 */
    private LocalDateTime updateTime;/* 修改時間 */
    private Integer lastReviserId;/* 最後修改人id */
}
