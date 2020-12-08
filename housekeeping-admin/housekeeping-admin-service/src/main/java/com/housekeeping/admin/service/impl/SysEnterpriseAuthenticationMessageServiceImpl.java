package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.SysEnterpriseAuthenticationMessagePostDTO;
import com.housekeeping.admin.entity.SysEnterpriseAuthenticationMessage;
import com.housekeeping.admin.mapper.SysEnterpriseAuthenticationMessageMapper;
import com.housekeeping.admin.service.ICompanyDetailsService;
import com.housekeeping.admin.service.ISysEnterpriseAuthenticationMessageService;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @Author su
 * @Date 2020/12/8 14:33
 */
@Service("sysEnterpriseAuthenticationMessageService")
public class SysEnterpriseAuthenticationMessageServiceImpl
        extends ServiceImpl<SysEnterpriseAuthenticationMessageMapper, SysEnterpriseAuthenticationMessage>
        implements ISysEnterpriseAuthenticationMessageService {

    @Resource
    private ICompanyDetailsService companyDetailsService;

    @Override
    public R sendAuthMessage(SysEnterpriseAuthenticationMessagePostDTO authMessageDTO) {
        SysEnterpriseAuthenticationMessage authMessage = new SysEnterpriseAuthenticationMessage();
        Integer userId = TokenUtils.getCurrentUserId();
        Integer companyId = companyDetailsService.getCompanyIdByUserId(userId);
        authMessage.setCompanyId(companyId);
        authMessage.setCompanyName(authMessageDTO.getCompanyName());
        authMessage.setCompanyNumber(authMessageDTO.getCompanyNumber());
        authMessage.setLegalName(authMessageDTO.getLegalName());
        authMessage.setPhonePrefix(authMessageDTO.getPhonePrefix());
        authMessage.setPhone(authMessageDTO.getPhone());
        authMessage.setRegisterAddress(authMessageDTO.getRegisterAddress());
        authMessage.setCreateTime(LocalDateTime.now());
        authMessage.setUpdateTime(LocalDateTime.now());
        authMessage.setLastReviserId(userId);
        if (authMessageDTO.getIsSend()){
            authMessage.setAuditStatus(1);
            baseMapper.insert(authMessage);
            return R.ok("已交付人工審核，請耐心等待");
        }else {
            authMessage.setAuditStatus(0);
            baseMapper.insert(authMessage);
            return R.ok("已保存至草稿箱");
        }
    }

}
