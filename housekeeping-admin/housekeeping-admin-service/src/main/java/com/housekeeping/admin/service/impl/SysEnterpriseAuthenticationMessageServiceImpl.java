package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.SysEnterpriseAuthenticationMessagePostDTO;
import com.housekeeping.admin.entity.CompanyDetails;
import com.housekeeping.admin.entity.SysEnterpriseAuthenticationMessage;
import com.housekeeping.admin.mapper.SysEnterpriseAuthenticationMessageMapper;
import com.housekeeping.admin.service.ICompanyDetailsService;
import com.housekeeping.admin.service.ISysEnterpriseAuthenticationMessageService;
import com.housekeeping.common.sms.SendMessage;
import com.housekeeping.common.utils.*;
import org.apache.ibatis.ognl.OgnlRuntime;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private RedisUtils redisUtils;

    @Override
    public R isValidate() {
        Integer userId = TokenUtils.getCurrentUserId();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        CompanyDetails companyDetails = companyDetailsService.getOne(queryWrapper);
        return R.ok(companyDetails.getIsValidate(), "獲取成功");
    }

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
        if ((Boolean) this.isValidate().getData()){
            return R.failed("貴公司已經認證");
        }
        if(CommonUtils.isNotEmpty(authMessageDTO.getCode())){
            if (!authMessageDTO.getCode().equals(redisUtils.get(CommonConstants.CHECK_KEY_BY_PHONE + "_" + authMessageDTO.getPhonePrefix() + "_" +  authMessageDTO.getPhone()))) {
                return R.failed("驗證碼錯誤");
            }
        }

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
        authMessage.setEnclosure(authMessage.getEnclosure());
        authMessage.setCreateTime(LocalDateTime.now());
        authMessage.setUpdateTime(LocalDateTime.now());
        authMessage.setLastReviserId(userId);
        //先删除再加
        QueryWrapper qwDelete= new QueryWrapper();
        qwDelete.eq("company_id", companyId);
        baseMapper.delete(qwDelete);
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
                baseMapper.undo(sysEnterpriseAuthenticationMessage.getCompanyId());
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
            companyDetailsService.authSuccess(authMessage.getCompanyId(), authMessage.getCompanyName());
            return R.ok("審核成功，已提交");
        }else {
            return R.failed(authMessage.getAuditStatus() + "認證信息狀態異常");
        }
    }

    @Override
    public R sendSms(String prefixPhone, String phone) {
        //生成随机验证码
        String code = CommonUtils.getRandomSixCode();
        String key = CommonConstants.CHECK_KEY_BY_PHONE + "_" + prefixPhone + "_" +  phone;
        //存入redis
        redisUtils.set(key, code);
        redisUtils.expire(key, CommonConstants.VALID_TIME_MINUTES * 60);//三分鐘
        //发送短信
        String[] params = new String[]{code, CommonConstants.VALID_TIME_MINUTES.toString()};
        SendMessage.sendMessage(prefixPhone, phone, params);
        return R.ok("成功發送短信");
    }


}
