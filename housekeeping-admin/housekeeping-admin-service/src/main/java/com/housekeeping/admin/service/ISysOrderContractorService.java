package com.housekeeping.admin.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.SysOrderContractorDTO;
import com.housekeeping.admin.entity.SysOrderContractor;
import com.housekeeping.common.utils.R;


public interface ISysOrderContractorService extends IService<SysOrderContractor> {


    R releaseOrderContractor(SysOrderContractorDTO sysOrderPlanDTO);
}
