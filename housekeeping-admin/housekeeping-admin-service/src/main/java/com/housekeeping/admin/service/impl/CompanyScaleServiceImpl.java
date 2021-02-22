package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.QueryIndexDTO;
import com.housekeeping.admin.dto.SetScaleDTO;
import com.housekeeping.admin.entity.CompanyScale;
import com.housekeeping.admin.mapper.CompanyScaleMapper;
import com.housekeeping.admin.service.ICompanyScaleService;
import com.housekeeping.common.utils.OptionalBean;
import com.housekeeping.common.utils.R;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author su
 * @Date 2021/2/22 16:27
 */
@Service("companyScaleService")
public class CompanyScaleServiceImpl
        extends ServiceImpl<CompanyScaleMapper, CompanyScale>
        implements ICompanyScaleService {
    @Override
    public R setScale(SetScaleDTO dto) {
        List<Integer> scale = OptionalBean.ofNullable(dto)
                .getBean(SetScaleDTO::getScale).get();
        List<BigDecimal> monthPrice = OptionalBean.ofNullable(dto)
                .getBean(SetScaleDTO::getMonthPrice).get();
        List<BigDecimal> yearPrice = OptionalBean.ofNullable(dto)
                .getBean(SetScaleDTO::getYearPrice).get();
        String code = OptionalBean.ofNullable(dto)
                .getBean(SetScaleDTO::getCode).get();
        //先不判空看看
        this.remove(new QueryWrapper<>());
        List<CompanyScale> companyScaleList = dto.toCompanyScaleList();
        this.saveBatch(companyScaleList);
        return R.ok("設置完成");
    }

    @Override
    public R listScale() {
        return R.ok(baseMapper.selectList(new QueryWrapper<>()), "查詢成功");
    }
}
