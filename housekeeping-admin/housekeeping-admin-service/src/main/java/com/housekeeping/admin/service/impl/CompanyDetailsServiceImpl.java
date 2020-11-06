package com.housekeeping.admin.service.impl;



import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.CompanyDetails;
import com.housekeeping.admin.mapper.CompanyDetailsMapper;
import com.housekeeping.admin.service.ICompanyDetailsService;
import org.springframework.stereotype.Service;


@Service("companyDetailsService")
public class CompanyDetailsServiceImpl extends ServiceImpl<CompanyDetailsMapper, CompanyDetails> implements ICompanyDetailsService {
}
