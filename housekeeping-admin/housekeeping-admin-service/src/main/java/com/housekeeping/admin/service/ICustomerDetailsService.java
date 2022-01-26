package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.CompanyDetailsPageDTO;
import com.housekeeping.admin.dto.CustomerUpdateDTO;
import com.housekeeping.admin.dto.PageOfEmployeesDTO;
import com.housekeeping.admin.entity.CustomerDetails;
import com.housekeeping.admin.vo.CancelCollectionVO;
import com.housekeeping.common.utils.R;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @Author su
 * @create 2020/11/23 10:55
 */
public interface ICustomerDetailsService extends IService<CustomerDetails> {

    R toDefault(Integer id);

    String uploadHead(MultipartFile file, Integer id) throws IOException;

    R updateHeadUrlByUserId(String headUrl, Integer id);

    R getCustomerList(Page page,Integer cid, String name);

    R updateCus(CustomerUpdateDTO customerUpdateDTO);

    R blacklist(Integer customerId, Boolean action);

    /* 根据userId获取CustomerDetails */
    CustomerDetails getByUserId(Integer userId);

    R collection(Integer empId);

    R getCollectionList(PageOfEmployeesDTO pageOfEmployeesDTO);

    R cancelCollection(String ids);

    R checkCollection(Integer empId);

    R collectionCompany(Integer companyId);

    R getCollectionCompanyList(CompanyDetailsPageDTO companyDetailsPageDTO);

    R cancelCollectionCompany(String ids);

    R checkCollectionCompany(Integer companyId);
}
