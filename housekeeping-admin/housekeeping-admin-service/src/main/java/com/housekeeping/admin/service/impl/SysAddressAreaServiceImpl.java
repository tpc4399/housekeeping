package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.SysAddressArea;
import com.housekeeping.admin.entity.SysAddressCity;
import com.housekeeping.admin.mapper.SysAddressAreaMapper;
import com.housekeeping.admin.service.ISysAddressAreaService;
import com.housekeeping.admin.service.ISysAddressCityService;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author su
 * @Date 2021/3/8 16:40
 */
@Slf4j
@Service("sysAddressAreaService")
public class SysAddressAreaServiceImpl extends ServiceImpl<SysAddressAreaMapper, SysAddressArea> implements ISysAddressAreaService {

    @Resource
    private ISysAddressCityService sysAddressCityService;

    @Override
    public R getAllByCityId(Integer cityId) {
        QueryWrapper qw = new QueryWrapper();
        qw.eq("parent_id", cityId);
        List<SysAddressArea> res = this.list(qw);
        return R.ok(res, "查詢成功");
    }

    @Override
    public Boolean matchingArea(String address, String areaIds) {
        if (CommonUtils.isEmpty(areaIds)) return false;
        if (areaIds.length() == 0 || !areaIds.contains(" ")) return false;
        String[] r = areaIds.split(" ");
        for (int i = 0; i < r.length; i++) {
            SysAddressArea area = this.getById(Integer.valueOf(r[i]));          //區
            if (CommonUtils.isEmpty(area)){
                log.info("期望工作區域數據格式錯誤");
                return false;
            }
            SysAddressCity city = sysAddressCityService.getById(area.getParentId()); //市
            String cityArea = city.getName()+area.getName();
            if (address.contains(cityArea)){
                return true;
            }
        }
        return false;
    }
}
