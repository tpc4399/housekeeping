package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.CompanyWorkListDTO;
import com.housekeeping.admin.dto.CompanyWorkListQueryDTO;
import com.housekeeping.admin.entity.CompanyWorkList;
import com.housekeeping.admin.entity.SysOrder;
import com.housekeeping.common.utils.R;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author su
 * @create 2020/11/18 16:09
 */
public interface ICompanyWorkListService extends IService<CompanyWorkList> {

    R addToTheWorkList(CompanyWorkListDTO companyWorkListDTO);
    R page(IPage<CompanyWorkList> page, CompanyWorkListQueryDTO companyWorkListQueryDTO);
    R matchTheOrder(Integer orderId);
    R dispatchOrder(Integer orderId);

}
