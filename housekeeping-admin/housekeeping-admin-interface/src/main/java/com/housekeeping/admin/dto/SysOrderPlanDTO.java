package com.housekeeping.admin.dto;

import com.housekeeping.admin.vo.RulesDateVo;
import com.housekeeping.admin.vo.RulesMonthlyVo;
import com.housekeeping.admin.vo.RulesWeekVo;
import io.swagger.annotations.ApiParam;
import lombok.Data;

import java.util.List;

/**
 * @Author su
 * @create 2020/11/16 14:21
 */
@Data
public class SysOrderPlanDTO {
    @ApiParam("false=需求单 true=预约单")
    private Boolean temp;                   /* false=需求单 true=预约单 */
    private Integer companyId;              /* 公司_id */
    private List<RulesDateVo> rulesDateVos; /* 单次服务规则 */
    private List<RulesWeekVo> rulesWeekVos; /* 定期服务规则 */
    private RulesMonthlyVo rulesMonthlyVo;  /* 包月规则 */
    private Integer addressId;              /* 服务地址_id */
    private String jobContendIds;              /* 工作内容_ids */
}
