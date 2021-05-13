package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.housekeeping.admin.dto.DemandDto;
import com.housekeeping.admin.dto.ReleaseRequirementBDTO;
import com.housekeeping.admin.dto.ReleaseRequirementUDTO;
import com.housekeeping.common.utils.R;

/**
 * @Author su
 * @Date 2021/1/12 9:05
 */
public interface IReleaseRequirementService {

    R releaseRequirements(ReleaseRequirementBDTO dto) throws InterruptedException;

    R page(IPage page);

    R getAllRequirement(Integer cusId, Page page);

    R getAllRequirementsByCompany(DemandDto demandDto,Page page);

    R removedCusId(Integer id);

    R updateCus(ReleaseRequirementUDTO dto) throws InterruptedException;

    R getCusById(Integer id);
}
