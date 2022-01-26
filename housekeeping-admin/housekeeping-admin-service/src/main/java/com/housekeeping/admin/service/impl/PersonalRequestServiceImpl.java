package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.PersonalRequestDTO;
import com.housekeeping.admin.entity.EmployeesDetails;
import com.housekeeping.admin.entity.NotificationOfRequestForChangeOfAddress;
import com.housekeeping.admin.entity.PersonalRequest;
import com.housekeeping.admin.mapper.NotificationOfRequestForChangeOfAddressMapper;
import com.housekeeping.admin.mapper.PersonalRequestMapper;
import com.housekeeping.admin.service.EmployeesDetailsService;
import com.housekeeping.admin.service.INotificationOfRequestForChangeOfAddressService;
import com.housekeeping.admin.service.PersonalRequestService;
import com.housekeeping.common.utils.PageUtils;
import com.housekeeping.common.utils.R;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;


@Service("personalRequestService")
public class PersonalRequestServiceImpl
        extends ServiceImpl<PersonalRequestMapper, PersonalRequest>
        implements PersonalRequestService {

    @Resource
    private EmployeesDetailsService employeesDetailsService;

    @Override
    public R getAll(Page page,Integer id,Integer status,String name,Integer type) {

        List<PersonalRequestDTO> collect = null;

        List<PersonalRequestDTO> personalRequestDTOS = baseMapper.getAll(id,status,type);
        personalRequestDTOS.forEach(x ->{
            EmployeesDetails byId = employeesDetailsService.getById(x.getPersonalId());
            x.setEmployeesDetails(byId);
        });
        if(StringUtils.isNotBlank(name)){
            collect = personalRequestDTOS.stream().filter(x -> {
                String name1 = x.getEmployeesDetails().getName();
                if (name1.contains(name)) {
                    return true;
                } else {
                    return false;
                }
            }).collect(Collectors.toList());
        }else {
            collect = personalRequestDTOS;
        }
        Page pages = PageUtils.getPages((int) page.getCurrent(), (int) page.getSize(), collect);
        return R.ok(pages);
    }

    @Override
    public void updateCompany(String companyId) {
        baseMapper.updateCompany(companyId);
    }

    @Override
    public void updateCompany2(String companyId) {
        baseMapper.updateCompany2(companyId);
    }
}
