package com.housekeeping.admin.dto;

import com.housekeeping.admin.vo.RulesWeekVo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 需求发布接口
 * @Author su
 * @Date 2021/1/5 17:40
 */
@Data
public class ReleaseRequirementBDTO {
    private Integer addressId;              /* 地址 */
    private String parentId;                /* 一级分类id */
    private List<Integer> jobs;             /* 被选中的工作内容标签 */
    private Integer serverPlaceType;        /* 服务场所类型  0住宿与交际 1洗浴与美容 2文化娱乐 3体育与游乐 4文化交流 5购物 6就诊与交通 7其它*/
    private String note;                    /* 备注 */
    private String housingArea;             /* 房屋面积 */
    private RulesWeekVo rulesWeekVo;        /* 服务时间安排 */
    private BigDecimal estimatedSalary;     /* 预计薪资 */
    private String code;                    /* 薪资货币代码 */
    private Boolean liveAtHome;             /* 能否居家 */
}
