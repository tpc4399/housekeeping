package com.housekeeping.admin.service;

import com.housekeeping.admin.vo.RulesDateVo;
import com.housekeeping.admin.vo.RulesMonthlyVo;
import com.housekeeping.admin.vo.RulesWeekVo;
import com.housekeeping.common.utils.R;

import java.util.List;

/**
 * @Author su
 * @Date 2021/1/12 9:05
 */
public interface IReleaseRequirementService {

    R jobContendRecheckingA(List<RulesDateVo> rulesDateVos);
    R jobContendRecheckingB(List<RulesWeekVo> rulesWeekVos);
    R jobContendRecheckingC(RulesMonthlyVo rulesMonthlyVo);
    R generateOrder(Integer[] sonIds, Integer serviceType);
    R putInStorageA(List<RulesDateVo> rulesDateVos);
    R putInStorageB(List<RulesWeekVo> rulesWeekVos);
    R putInStorageC(RulesMonthlyVo rulesMonthlyVo);

}
