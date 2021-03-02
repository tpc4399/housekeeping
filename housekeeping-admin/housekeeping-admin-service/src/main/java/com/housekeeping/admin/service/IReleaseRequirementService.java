package com.housekeeping.admin.service;

import com.housekeeping.admin.dto.ReleaseRequirementBDTO;
import com.housekeeping.common.utils.R;

/**
 * @Author su
 * @Date 2021/1/12 9:05
 */
public interface IReleaseRequirementService {

    R releaseRequirements(ReleaseRequirementBDTO dto) throws InterruptedException;

}
