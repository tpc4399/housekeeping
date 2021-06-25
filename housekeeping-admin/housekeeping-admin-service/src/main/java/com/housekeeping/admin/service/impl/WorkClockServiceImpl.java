package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.CustomerEvaluationDTO;
import com.housekeeping.admin.dto.WorkClockDTO;
import com.housekeeping.admin.entity.EmployeesDetails;
import com.housekeeping.admin.entity.WorkClock;
import com.housekeeping.admin.mapper.WorkClockMapper;
import com.housekeeping.admin.service.EmployeesDetailsService;
import com.housekeeping.admin.service.WorkClockService;
import com.housekeeping.common.sms.SendMessage;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @Author su
 * @Date 2020/12/11 16:08
 */
@Service("workClockService")
public class WorkClockServiceImpl extends ServiceImpl<WorkClockMapper, WorkClock> implements WorkClockService {

    @Resource
    private EmployeesDetailsService employeesDetailsService;

    @Override
    public R workStart(Integer id,String phonePrefix,String phone) {

        WorkClock byId = this.getById(id);

        byId.setToWorkStatus(1);
        byId.setToWorkTime(LocalDateTime.now());
        byId.setWorkStatus(1);
        this.updateById(byId);

        QueryWrapper<EmployeesDetails> qw = new QueryWrapper<>();
        qw.eq("user_id", TokenUtils.getCurrentUserId());
        EmployeesDetails one = employeesDetailsService.getOne(qw);
        //发送短信
        String[] params = new String[]{one.getName()};
        SendMessage.sendWorkStartMessage(phonePrefix, phone, params);
        return R.ok("成功發送短信");
    }

    @Override
    public R workEnd(Integer id,String phonePrefix,String phone) {

        WorkClock byId = this.getById(id);
        if(byId.getOffWorkStatus().equals(0)){
            return R.failed("請先進行上班打卡！");
        }

        byId.setOffWorkStatus(1);
        byId.setOffWorkTime(LocalDateTime.now());
        byId.setWorkStatus(2);
        this.updateById(byId);

        QueryWrapper<EmployeesDetails> qw = new QueryWrapper<>();
        qw.eq("user_id",TokenUtils.getCurrentUserId());
        EmployeesDetails one = employeesDetailsService.getOne(qw);
        //发送短信
        String[] params = new String[]{one.getName()};
        SendMessage.sendWorkEndMessage(phonePrefix, phone, params);
        return R.ok("成功發送短信");
    }

    @Override
    public WorkClock getByWorkId(Integer id) {
        QueryWrapper<WorkClock> qw = new QueryWrapper<>();
        qw.eq("work_id",id);
        return this.getOne(qw);
    }

    @Override
    public R uploadPhotoAndSummary(WorkClockDTO workClockDTO) {
        WorkClock byId = this.getById(workClockDTO.getId());
        byId.setPhotos(workClockDTO.getPhotosUrl());
        byId.setStaffSummary(workClockDTO.getStaffSummary());
        this.updateById(byId);
        return R.ok("上傳成功");
    }

    @Override
    public R customerEvaluation(CustomerEvaluationDTO customerEvaluationDTO) {
        WorkClock byId = this.getById(customerEvaluationDTO.getId());
        byId.setCustomerStarRating(customerEvaluationDTO.getCustomerStarRating());
        byId.setCustomerPhoto(customerEvaluationDTO.getCustomerPhoto());
        byId.setCustomerEvaluation(customerEvaluationDTO.getCustomerEvaluation());
        this.updateById(byId);
        return R.ok("評價成功");
    }
}
