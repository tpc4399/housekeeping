package com.housekeeping.admin.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.UserWithdrawalDTO;
import com.housekeeping.admin.entity.User;
import com.housekeeping.admin.entity.UserWithdrawal;
import com.housekeeping.admin.mapper.UserWithdrawalMapper;
import com.housekeeping.admin.service.IUserService;
import com.housekeeping.admin.service.UserWithdrawalService;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;


@Service("userWithdrawalService")
public class UserWithdrawalServiceImpl extends ServiceImpl<UserWithdrawalMapper, UserWithdrawal> implements UserWithdrawalService {

    @Resource
    private IUserService userService;


    @Override
    public R addWithdrawal(UserWithdrawal userWithdrawal) {

        User byId = userService.getById(userWithdrawal.getUserId());

        //判断上次申请是否相差一个月
        QueryWrapper<UserWithdrawal> queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id",userWithdrawal.getUserId());
        queryWrapper.orderByDesc("create_time");
        List<UserWithdrawal> list = this.list(queryWrapper);
        if(CollectionUtils.isNotEmpty(list)){
            LocalDateTime createTime = list.get(list.size() - 1).getCreateTime();
            int year = createTime.getYear();
            int monthValue = createTime.getMonthValue();
            YearMonth yearMonth = YearMonth.of(year,monthValue);
            int compare = YearMonth.now().compareTo(yearMonth);
            if(compare<1){
                return R.failed("一個月内只允許申請一次");
            }
        }

        //判斷申請金額是否大於您所得佣金
        queryWrapper.in("status",0,2);
        BigDecimal sum = new BigDecimal(0);
        for (UserWithdrawal withdrawal : this.list()) {
            sum = withdrawal.getAmount().add(sum);
        }
        BigDecimal add = userWithdrawal.getAmount().add(sum);
        if(add.compareTo(byId.getBonus()) ==1){
            return R.failed("申請的金額已超過您所得佣金");
        }


        userWithdrawal.setCreateTime(LocalDateTime.now());
        this.save(userWithdrawal);
        return R.ok("申請成功");
    }

    @Override
    public R handleWithdrawal(Integer id, Integer status) {
        //更新提现申请状态
        UserWithdrawal withdrawal = this.getById(id);
        withdrawal.setStatus(status);
        this.updateById(withdrawal);

        //如果是修改已发放则修改用户佣金
        if(status.equals(3)) {
            User byId = userService.getById(withdrawal.getUserId());
            byId.setBonus(byId.getBonus().subtract(withdrawal.getAmount()));
            userService.updateById(byId);
        }
        return R.ok("处理成功");
    }

    @Override
    public R getAllWithdrawal(Integer id, Integer userId, String bank, String collectionAccount, Integer status) {
        QueryWrapper<UserWithdrawal> qw = new QueryWrapper<>();
        if(CommonUtils.isNotEmpty(id)){
            qw.eq("id",id);
        }
        if(CommonUtils.isNotEmpty(userId)){
            qw.eq("user_id",userId);
        }
        if(CommonUtils.isNotEmpty(bank)){
            qw.like("bank",id);
        }
        if(CommonUtils.isNotEmpty(collectionAccount)){
            qw.like("collection_account",id);
        }
        if(CommonUtils.isNotEmpty(status)){
            qw.eq("status",status);
        }
        List<UserWithdrawalDTO> collect = this.list(qw).stream().map(x -> {
            UserWithdrawalDTO userWithdrawalDTO = new UserWithdrawalDTO(x);
            User byId = userService.getById(x.getUserId());
            userWithdrawalDTO.setUser(byId);
            return userWithdrawalDTO;
        }).collect(Collectors.toList());
        return R.ok(collect);
    }
}
