package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.SysEnterpriseAuthenticationMessagePostDTO;
import com.housekeeping.admin.entity.SysEnterpriseAuthenticationMessage;
import com.housekeeping.admin.mapper.SysEnterpriseAuthenticationMessageMapper;
import com.housekeeping.admin.service.ICompanyDetailsService;
import com.housekeeping.admin.service.ISysEnterpriseAuthenticationMessageService;
import com.housekeeping.common.utils.CommonUtils;
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

    /***
     * 加載我的草稿
     * @return
     */
    @Override
    public R loadingTheDraft() {
        Integer userId = TokenUtils.getCurrentUserId();
        Integer companyId = companyDetailsService.getCompanyIdByUserId(userId);
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("company_id", companyId);
        queryWrapper.eq("audit_status", 0);
        SysEnterpriseAuthenticationMessage sysEnterpriseAuthenticationMessage = baseMapper.selectOne(queryWrapper);
        if (CommonUtils.isNotEmpty(sysEnterpriseAuthenticationMessage)){
            return R.ok(sysEnterpriseAuthenticationMessage, "查詢成功");
        }else {
            return R.failed("草稿為空");
        }
    }

    /***
     * 发布申请和材料
     * @param authMessageDTO
     * @return
     */
    @Override
    public R sendAuthMessage(SysEnterpriseAuthenticationMessagePostDTO authMessageDTO) {
        SysEnterpriseAuthenticationMessage authMessage = new SysEnterpriseAuthenticationMessage();
        Integer userId = TokenUtils.getCurrentUserId();
        Integer companyId = companyDetailsService.getCompanyIdByUserId(userId);
        /**
         * 發佈之前先查查是否已經發佈過了
         */
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("company_id", companyId);
        queryWrapper.eq("audit_status", 1);
        SysEnterpriseAuthenticationMessage sysEnterpriseAuthenticationMessage = baseMapper.selectOne(queryWrapper);
        if (CommonUtils.isNotEmpty(sysEnterpriseAuthenticationMessage)){
            return R.failed("已經申請過了，是否撤銷申請並重新發佈？");
        }
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

    /**
     * 查看我的申请和材料
     * @return
     */
    @Override
    public R viewMine() {
        Integer userId = TokenUtils.getCurrentUserId();
        Integer companyId = companyDetailsService.getCompanyIdByUserId(userId);
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("company_id", companyId);
        queryWrapper.eq("audit_status", 1);
        SysEnterpriseAuthenticationMessage sysEnterpriseAuthenticationMessage = baseMapper.selectOne(queryWrapper);
        if (CommonUtils.isNotEmpty(sysEnterpriseAuthenticationMessage)){
            return R.ok(sysEnterpriseAuthenticationMessage, "查詢成功");
        }else {
            return R.failed("您還未申請認證");
        }
    }

    /***
     * 撤销我的申请和材料
     * @return
     */
    @Override
    public R undo() {
        Integer userId = TokenUtils.getCurrentUserId();
        Integer companyId = companyDetailsService.getCompanyIdByUserId(userId);
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("company_id", companyId);
        queryWrapper.eq("audit_status", 1);
        SysEnterpriseAuthenticationMessage sysEnterpriseAuthenticationMessage = baseMapper.selectOne(queryWrapper);
        if (CommonUtils.isNotEmpty(sysEnterpriseAuthenticationMessage)){
            if (sysEnterpriseAuthenticationMessage.getAuditStatus() == 1){
                sysEnterpriseAuthenticationMessage.setAuditStatus(0);
                baseMapper.updateById(sysEnterpriseAuthenticationMessage);
                return R.ok("撤銷成功");
            }else {
                return R.failed(sysEnterpriseAuthenticationMessage.getAuditStatus()+":您的申請單不是已發佈狀態");
            }
        }else {
            return R.failed("未發現認證申請單");
        }
    }

    /***
     * 管理员按更新时间查询所有
     * @param page
     * @return
     */
    @Override
    public R query(Page page) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("audit_status", 1);
        queryWrapper.orderByDesc("create_time");
        return R.ok(baseMapper.selectPage(page, queryWrapper));
    }

    @Override
    public R doAudit(Integer id, Boolean isThrough) {
        SysEnterpriseAuthenticationMessage authMessage = baseMapper.selectById(id);
        if (authMessage.getAuditStatus() == 1){
            authMessage.setAuditStatus(isThrough?3:4);
            baseMapper.updateById(authMessage);
            //更新公司信息
            companyDetailsService.authSuccess(authMessage.getCompanyId());
            return R.ok("審核成功，已提交");
        }else {
            return R.failed(authMessage.getAuditStatus() + "認證信息狀態異常");
        }
    }

}
