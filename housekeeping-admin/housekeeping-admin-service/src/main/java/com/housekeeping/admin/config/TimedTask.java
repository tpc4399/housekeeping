package com.housekeeping.admin.config;

import com.housekeeping.admin.entity.CompanyDetails;
import com.housekeeping.admin.entity.EmployeesDetails;
import com.housekeeping.admin.service.EmployeesDetailsService;
import com.housekeeping.admin.service.ICompanyDetailsService;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.RedisUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author su
 * @create 2021/5/11 18:35
 */
@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
public class TimedTask {

    @Resource
    private EmployeesDetailsService employeesDetailsService;
    @Resource
    private ICompanyDetailsService companyDetailsService;
    @Resource
    private RedisUtils redisUtils;

    //3.添加定时任务
    @Scheduled(cron = "0 0 0,13,20,23 * * ?")
    //或直接指定时间间隔，例如：5秒
    //@Scheduled(fixedRate=5000)
    private void configureTasks() {
        /* 保洁员数据 */
        List<EmployeesDetails> employeesDetails = employeesDetailsService.list();
        fun(employeesDetails, "employees", "details");
        /* 公司数据 */
        List<CompanyDetails> companyDetails = companyDetailsService.list();
        fun(companyDetails, "company", "details");
    }


    private void fun(List list, String name1, String name2){
        list.forEach(x -> {
            Map<String, Object> map = new HashMap<>();
            try {
                map = CommonUtils.objectToMap(x);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            String key = name1+":"+map.get("id")+":"+name2;
            redisUtils.hmset(key, map);
        });
    }
}
