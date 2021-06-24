package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.EmployeesDetails;
import com.housekeeping.admin.entity.SysJobNote;
import com.housekeeping.admin.entity.WorkClock;
import com.housekeeping.admin.mapper.SysJobNoteMapper;
import com.housekeeping.admin.mapper.WorkClockMapper;
import com.housekeeping.admin.service.EmployeesDetailsService;
import com.housekeeping.admin.service.ISysJobNoteService;
import com.housekeeping.admin.service.WorkClockService;
import com.housekeeping.common.sms.SendMessage;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author su
 * @Date 2020/12/11 16:08
 */
@Service("workClockService")
public class WorkClockServiceImpl extends ServiceImpl<WorkClockMapper, WorkClock> implements WorkClockService {

    @Resource
    private EmployeesDetailsService employeesDetailsService;

    @Override
    public R workStart(String phonePrefix,String phone) {
        QueryWrapper<EmployeesDetails> qw = new QueryWrapper<>();
        qw.eq("user_id", TokenUtils.getCurrentUserId());
        EmployeesDetails one = employeesDetailsService.getOne(qw);
        //发送短信
        String[] params = new String[]{one.getName()};
        SendMessage.sendWorkStartMessage(phonePrefix, phone, params);
        return R.ok("成功發送短信");
    }

    @Override
    public R workEnd(String phonePrefix,String phone) {
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
}
