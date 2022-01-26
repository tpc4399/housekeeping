package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.*;
import com.housekeeping.admin.entity.User;
import com.housekeeping.common.utils.R;

public interface IUserService extends IService<User> {

    User getUserByPhone(String phonePrefix,String phone,Integer deptId);
    R checkData(String data, String type,Integer deptId);
    R sendRegisterMSMessage(String phonePrefix,String phone,Integer deptId) throws Exception;
    R saveEmp(RegisterCompanyDTO dto);
    R saveCus(RegisterCustomerDTO dto);
    R saveAdmin(RegisterAdminDTO dto);
    R getAllUser(IPage<User> page, AdminPageDTO adminPageDTO,Integer deptId);
    User getUserByIdAndDept(Integer id, int i);
    R page(Page page, PageOfUserDTO dto);
    /* 添加管理员、公司、家庭账户 */
    R add1(AdminAdd1DTO dto);
    /* 添加经理、保洁员账户 */
    R add2(AdminAdd2DTO dto);
    /* 修改管理员、公司、家庭账户 */
    R update1(AdminUpdate1DTO dto);
    /* 修改经理、保洁员账户 */
    R update2(AdminUpdate2DTO dto);

    R removeAdmin(Integer userId);

    R removeCus(Integer userId);

    R removeComp(Integer userId);

    /* 判断手机号是否存在,true代表已存在，false代表不存在 */
    Boolean isExistPhone(String phonePrefix, String phone,Integer deptId);

    R removePersonal(Integer userId);

    R savePersonal(RegisterCompanyDTO dto);

    R savePersonalByAdmin(RegisterPersonalDTO dto);

    R getSms(Integer type);

    R sendForgetSms(String phonePrefix, String phone, Integer deptId) throws Exception;

    R updatePwdByPhone(ForgetDTO forgetDTO);

    R verfifyCode2(String phonePrefix, String phone, String code);

    R checkData2(String data, String type);

    R getAllCompany(Page page, AdminPageDTO adminPageDTO);

    R getAllStudio(Page page, AdminPageDTO adminPageDTO);

    R getInfo();
}
