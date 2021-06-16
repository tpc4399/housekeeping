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
import java.util.*;

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

    @Scheduled(cron = "* 0/1 * * * ? ") //每1分钟执行一次
    private void configureTasks() {
        /* 保洁员数据 */
        List<EmployeesDetails> employeesDetails = employeesDetailsService.list();
        fun(employeesDetails, "employees", "details");
        /* 公司数据 */
        List<CompanyDetails> companyDetails = companyDetailsService.list();
        fun(companyDetails, "company", "details");
    }

    private void fun(List list, String name1, String name2){
        List<Integer> mysqlIds = new ArrayList<>(); //当前数据库的ids
        /* 增，改 */
        list.forEach(x -> {
            Map<String, Object> map = new HashMap<>();
            try {
                map = CommonUtils.objectToMap(x);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            String key = name1+":"+map.get("id")+":"+name2;
            redisUtils.hmset(key, map);
            mysqlIds.add((Integer) map.get("id"));
        });

        /* redis数据准备 */
        String pattern = name1+":*:"+name2;
        Set<String> keys = redisUtils.keys(pattern);
        Object[] keysArr = keys.toArray();//当前redis的key

        /* 该删的删 */
        for (int i = 0; i < keysArr.length; i++) {
            String key = keysArr[i].toString();
            String[] section = key.split(":");
            Integer redisKeyId = Integer.valueOf(section[1]);
            if (!mysqlIds.contains(redisKeyId)){
                //既然mysql中没有，redis里面有，那就要执行删除程序了
                redisUtils.del(key);
            }
        }

    }

    /**
     * 延时队列 之定时获取队列中即将过期的消息
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
                        String orderNumber = msg.getBody();
                        Boolean status1 = orderEvaluationService.getEvaluationStatusOfCustomer(orderNumber);
                        Boolean status2 = orderEvaluationService.getEvaluationStatusOfEmployees(orderNumber);
                        if (msg.getChannel().equals(CommonConstants.MESSAGE_CHANNEL_CUSTOMER) && !status1)
                            //客户自动好评的任务需要执行
                            orderEvaluationService.customerEvaluation(new OrderEvaluationDTO(orderNumber, new Integer(5), "該客戶默認好評",""));
                        if (msg.getChannel().equals(CommonConstants.MESSAGE_CHANNEL_EMPLOYEES) && !status2)
                            //保洁员自动好评的任务需要执行
                            orderEvaluationService.employeesEvaluation(new OrderEvaluationDTO(orderNumber, new Integer(5), "該保潔員默認好評",""));
                        orderEvaluationService.evaluationStatusHandle(orderNumber);
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
