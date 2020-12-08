package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.SysEnterpriseAuthenticationMessagePostDTO;
import com.housekeeping.admin.entity.SysEnterpriseAuthenticationMessage;
import com.housekeeping.common.utils.R;

/**
 * @Author su
 * @Date 2020/12/8 14:33
 */
public interface ISysEnterpriseAuthenticationMessageService extends IService<SysEnterpriseAuthenticationMessage> {
    R loadingTheDraft();
    R sendAuthMessage(SysEnterpriseAuthenticationMessagePostDTO authMessageDTO);
    R viewMine();
    R undo();
    R query(Page page);
    R doAudit(Integer id, Boolean isThrough);
}
