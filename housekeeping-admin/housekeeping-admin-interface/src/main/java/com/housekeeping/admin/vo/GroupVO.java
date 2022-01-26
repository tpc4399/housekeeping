package com.housekeeping.admin.vo;

import lombok.Data;

@Data
public class GroupVO {

    private Integer groupId;
    private String groupName; /* 组名 */
    private String headUrl; /* 图像 */
    private String responsible; /* 负责人 */
    private Integer empNum; /* 员工人数 */
    private Integer manNum; /* 经理人数 */
}
