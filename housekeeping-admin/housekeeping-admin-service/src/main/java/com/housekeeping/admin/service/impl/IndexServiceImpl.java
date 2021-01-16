package com.housekeeping.admin.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.IndexQueryDTO;
import com.housekeeping.admin.entity.Index;
import com.housekeeping.admin.entity.SysJobContend;
import com.housekeeping.admin.mapper.IndexMapper;
import com.housekeeping.admin.service.IEmployeesCalendarService;
import com.housekeeping.admin.service.IEmployeesJobsService;
import com.housekeeping.admin.service.IIndexService;
import com.housekeeping.admin.service.ISysJobContendService;
import com.housekeeping.common.utils.R;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service("indexService")
public class IndexServiceImpl extends ServiceImpl<IndexMapper, Index> implements IIndexService {

    @Resource
    private ISysJobContendService jobContendService;
    @Resource
    private IEmployeesCalendarService employeesCalendarService;
    @Resource
    private IEmployeesJobsService employeesJobsService;

    @Override
    public R getCusById(Integer id) {
        List<Integer> ids = baseMapper.getContentIds(id);
        ArrayList<SysJobContend> sysJobContends = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            SysJobContend byId = jobContendService.getById(ids.get(i));
            sysJobContends.add(byId);
        }
        return R.ok(sysJobContends);
    }

    @Override
    public R query(IndexQueryDTO indexQueryDTO) {
        /** 建立搜索池：只有符合条件的保洁员才会放进池子（设定了工作内容，设定了工作时间表） */
        List<Integer> searchPool = new ArrayList<>();
        QueryWrapper qw = new QueryWrapper();
        qw.select("employees_id").groupBy("employees_id");
        List<Integer> employeeIdsFromCalendar = employeesCalendarService.listObjs(qw);
        List<Integer> employeeIdsFromJob = employeesJobsService.listObjs(qw);
        searchPool = getIntersection(employeeIdsFromCalendar, employeeIdsFromJob);


        return null;
    }

    public List<Integer> getIntersection(List<Integer> a, List<Integer> b){
        a.retainAll(b);
        return a;
    }
}
