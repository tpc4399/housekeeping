package com.housekeeping.admin.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.housekeeping.admin.dto.OrderEvaluationDTO;
import com.housekeeping.admin.entity.CompanyDetails;
import com.housekeeping.admin.entity.EmployeesDetails;
import com.housekeeping.admin.service.EmployeesDetailsService;
import com.housekeeping.admin.service.ICompanyDetailsService;
import com.housekeeping.admin.service.IOrderEvaluationService;
import com.housekeeping.common.entity.Message;
import com.housekeeping.common.utils.*;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
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
@Slf4j
@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
public class TimedTask {

    @Resource
    private EmployeesDetailsService employeesDetailsService;
    @Resource
    private ICompanyDetailsService companyDetailsService;
    @Resource
    private RedisUtils redisUtils;
    private static ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().build();
    @Resource
    private DelayingQueueService delayingQueueService;
    @Resource
    private IOrderEvaluationService orderEvaluationService;

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


    /**
     * 定时对自动好评队列进行消费
     * zset会对score进行排序 让最早消费的数据位于最前
     * 拿最前的数据跟当前时间比较 时间到了则消费
     */
    @Scheduled(cron = "*/1 * * * * *")
    public void consumer() throws JsonProcessingException {
        List<Message> msgList = delayingQueueService.pull();
        if (null != msgList) {
            long current = System.currentTimeMillis();
            msgList.stream().forEach(msg -> {
                // 已超时的消息拿出来消费
                if (current >= msg.getDelayTime()) {
                    try {
                        log.info("消费消息：{}:消息创建时间：{},消费时间：{}", mapper.writeValueAsString(msg), msg.getCreateTime(), LocalDateTime.now());
                        //插入好评
                        if (msg.getChannel().equals(CommonConstants.MESSAGE_CHANNEL_CUSTOMER)){
                            //客户自动好评的任务需要执行
                            String orderNumber = msg.getBody();
                            Boolean status = orderEvaluationService.getEvaluationStatusOfCustomer(orderNumber);
                            if (!status) orderEvaluationService.customerEvaluation(new OrderEvaluationDTO(orderNumber, new Integer(5), "該客戶默認好評",""));
                        }
                        if (msg.getChannel().equals(CommonConstants.MESSAGE_CHANNEL_EMPLOYEES)){
                            //保洁员自动好评的任务需要执行
                            String orderNumber = msg.getBody();
                            System.out.println(orderNumber);
                            Boolean status = orderEvaluationService.getEvaluationStatusOfEmployees(orderNumber);
                            System.out.println(status);
                            if (!status) orderEvaluationService.employeesEvaluation(new OrderEvaluationDTO(orderNumber, new Integer(5), "該保潔員默認好評",""));
                        }
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    //移除消息
                    delayingQueueService.remove(msg);
                }
            });
        }
    }
}
