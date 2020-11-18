package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.CompanyWorkListDTO;
import com.housekeeping.admin.entity.CompanyWorkList;
import com.housekeeping.common.utils.R;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author su
 * @create 2020/11/18 16:09
 */
public interface ICompanyWorkListService extends IService<CompanyWorkList> {

    R addToTheWorkList(CompanyWorkListDTO companyWorkListDTO);

}
