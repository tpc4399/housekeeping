package com.housekeeping.admin.service.impl;



import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.CompanyDetails;
import com.housekeeping.admin.mapper.CompanyMapper;
import com.housekeeping.admin.service.CompanyService;
import org.springframework.stereotype.Service;


@Service("companyService")
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, CompanyDetails> implements CompanyService {




}
