package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.CustomerUpdateDTO;
import com.housekeeping.admin.entity.CustomerDetails;
import com.housekeeping.common.utils.R;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @Author su
 * @create 2020/11/23 10:55
 */
public interface ICustomerDetailsService extends IService<CustomerDetails> {

    R toDefault(Integer id);

    String uploadHead(MultipartFile file, Integer id) throws IOException;

    R updateHeadUrlByUserId(String headUrl, Integer id);

    R getCustomerList(Integer cid, String name);

    R updateCus(CustomerUpdateDTO customerUpdateDTO);

    R blacklist(Integer customerId, Boolean action);
}
