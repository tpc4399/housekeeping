package com.housekeeping.admin.dto;

import lombok.Data;

/**
 * @Author su
 * @create 2021/6/7 11:21
 */
@Data
public class WeightDTO {
    private String distanceScoreDouble; //距離權重
    private String areaScopeDouble;//地區權重
    private String priceScopeDouble;//價格權重
    private String attendanceScopeDouble;//出勤率权重
    private String evaluateScopeDouble;//評價權重
    private String extensionScopeDouble;//員工推廣權重
    private String extensionCompanyScopeDouble;//公司推廣權重
    private String numberOfOrdersReceivedScopeDouble;//接单次数权重
    private String timeScopeDouble;//时间匹配权重
    private String workScopeDouble;//工作内容匹配权重
}
