package com.housekeeping.admin.dto;

import com.housekeeping.admin.vo.RulesWeekVo;
import lombok.Data;

import java.util.List;

/**
 * 单次
 * @Author su
 * @Date 2021/1/5 17:40
 */
@Data
public class releaseRequirementBDTO {
    private Integer id;
    private Integer[] sonIds;
    private List<RulesWeekVo> rulesWeekVos; /* 钟点工：定期服务规则 */
}
