package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.AdvertisingRenewalAdminVo;
import com.housekeeping.admin.dto.CompanyAdvertisingAdminVo;
import com.housekeeping.admin.entity.CompanyAdvertising;
import com.housekeeping.admin.vo.AdvertisingRenewalVo;
import com.housekeeping.admin.vo.AdvertisingVo;
import com.housekeeping.admin.vo.CompanyAdvertisingVo;
import com.housekeeping.common.utils.R;
import org.springframework.web.multipart.MultipartFile;

public interface ICompanyAdvertisingService extends IService<CompanyAdvertising> {


    R add(CompanyAdvertisingVo companyAdvertising);

    R cusUpdate(AdvertisingVo companyAdvertising);

    R renewal(AdvertisingRenewalVo companyAdvertising);

    R getByCompanyId(Integer companyId, Integer id, String name);

    R getByRan(Integer ran);

    R uploadPhoto(MultipartFile file);

    R getByUserId(Integer userId, Integer id, String name);

    R addByAdmin(CompanyAdvertisingAdminVo companyAdvertising);

    R renewalByAdmin(AdvertisingRenewalAdminVo companyAdvertising);

    R getByAdmin(Integer id, String name, Boolean status);
}
