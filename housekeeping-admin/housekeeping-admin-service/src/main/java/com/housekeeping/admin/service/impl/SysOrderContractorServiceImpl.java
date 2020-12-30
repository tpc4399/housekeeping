package com.housekeeping.admin.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.SysOrderContractor;
import com.housekeeping.admin.mapper.SysOrderContractorMapper;
import com.housekeeping.admin.service.ISysOrderContractorService;
import org.springframework.stereotype.Service;

@Service("sysOrderContractorService")
public class SysOrderContractorServiceImpl extends ServiceImpl<SysOrderContractorMapper, SysOrderContractor> implements ISysOrderContractorService {
}
