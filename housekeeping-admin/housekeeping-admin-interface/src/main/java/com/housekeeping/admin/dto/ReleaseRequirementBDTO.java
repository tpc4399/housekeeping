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
    private Boolean liveAtHome;             /* 能否居家 */
    private AddressDetailsDTO addressDTO;   /* 地址的dto */
    private List<Integer> jobs;             /* 被选中的工作内容标签 */
    private String housingArea;             /* 房屋面积 */
    private RulesWeekVo rulesWeekVo;        /* 服务时间安排 */
    private BigDecimal estimatedSalary;     /* 预计薪资 */
    private String code;                    /* 薪资货币代码 */
}
