package com.housekeeping.admin.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @Author su
 * @create 2020/11/24 17:33
 */
@Data
public class GroupDetailsUpdateDTO {

    private Integer id;  /* 主键id */
    private String groupName;   /* 組名 */

}
