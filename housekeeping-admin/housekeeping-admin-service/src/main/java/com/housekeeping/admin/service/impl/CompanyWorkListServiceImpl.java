package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.CompanyWorkListDTO;
import com.housekeeping.admin.dto.CompanyWorkListQueryDTO;
import com.housekeeping.admin.entity.CompanyWorkList;
import com.housekeeping.admin.entity.SysOrder;
import com.housekeeping.admin.mapper.CompanyWorkListMapper;
import com.housekeeping.admin.service.ICompanyWorkListService;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @Author su
 * @create 2020/11/18 16:10
 */
@Service("companyWorkListService")
public class CompanyWorkListServiceImpl extends ServiceImpl<CompanyWorkListMapper, CompanyWorkList> implements ICompanyWorkListService {
    @Override
    public R addToTheWorkList(CompanyWorkListDTO companyWorkListDTO) {
        if (CommonUtils.isNotEmpty(companyWorkListDTO.getGroupId())
                && CommonUtils.isNotEmpty(companyWorkListDTO.getOrderId())){
            CompanyWorkList companyWorkList = new CompanyWorkList();
            companyWorkList.setGroupId(companyWorkListDTO.getGroupId());
            companyWorkList.setOrderId(companyWorkList.getOrderId());
            companyWorkList.setCreateTime(LocalDateTime.now());
            companyWorkList.setLastReviserId(TokenUtils.getCurrentUserId());
            return R.ok(baseMapper.insert(companyWorkList), "添加成功");
        }else {
            return R.failed("傳參錯誤,不能為空");
        }
    }

    @Override
    public R page(IPage<CompanyWorkList> page, CompanyWorkListQueryDTO companyWorkListQueryDTO) {
        QueryWrapper queryWrapper = new QueryWrapper();
        if (CommonUtils.isNotEmpty(companyWorkListQueryDTO.getGroupId())){
            queryWrapper.eq("group_id", companyWorkListQueryDTO.getGroupId());
        }
        if (CommonUtils.isNotEmpty(companyWorkListQueryDTO.getOrderId())){
            queryWrapper.eq("order_id", companyWorkListQueryDTO.getOrderId());
        }
        if (CommonUtils.isNotEmpty(companyWorkListQueryDTO.getCreateTimeStart())){
            queryWrapper.ge("create_time", companyWorkListQueryDTO.getCreateTimeStart());
        }
        if (CommonUtils.isNotEmpty(companyWorkListQueryDTO.getCreateTimeEnd())){
            queryWrapper.le("create_time", companyWorkListQueryDTO.getCreateTimeEnd());
        }
        if (CommonUtils.isNotEmpty(companyWorkListQueryDTO.getLastReviserId())){
            queryWrapper.eq("last_reviser_id", companyWorkListQueryDTO.getLastReviserId());
        }
        return R.ok(baseMapper.selectPage(page, queryWrapper));
    }
}
