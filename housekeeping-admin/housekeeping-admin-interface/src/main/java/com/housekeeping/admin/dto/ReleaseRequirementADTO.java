package com.housekeeping.admin.dto;

import com.housekeeping.admin.vo.RulesDateVo;
import lombok.Data;

import java.util.List;

/**
 * 单次
 * @Author su
 * @Date 2021/1/5 17:40
 */
@Data
public class ReleaseRequirementADTO {
    private Integer id;
    private Integer[] sonIds;
    private List<RulesDateVo> rulesDateVos; /* 钟点工：单次服务规则 */
}
