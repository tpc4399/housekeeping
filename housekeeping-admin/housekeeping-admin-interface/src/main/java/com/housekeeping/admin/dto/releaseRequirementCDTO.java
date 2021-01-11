package com.housekeeping.admin.dto;

import com.housekeeping.admin.vo.RulesMonthlyVo;
import lombok.Data;

/**
 * 单次
 * @Author su
 * @Date 2021/1/5 17:40
 */
@Data
public class releaseRequirementCDTO {
    private Integer id;
    private Integer[] sonIds;
    private RulesMonthlyVo rulesMonthlyVo;  /* 钟点工：包月规则 */
}
