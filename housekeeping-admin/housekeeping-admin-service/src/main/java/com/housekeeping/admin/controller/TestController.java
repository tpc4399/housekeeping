package com.housekeeping.admin.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.housekeeping.admin.dto.DateSlot;
import com.housekeeping.admin.entity.CompanyDetails;
import com.housekeeping.admin.entity.EmployeesDetails;
import com.housekeeping.admin.service.*;
import com.housekeeping.admin.vo.TimeSlot;
import com.housekeeping.auth.annotation.PassToken;
import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import net.sf.json.JSONObject;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @Author su
 * @Date 2021/1/18 16:53
 */
@Api(tags={"【ZZ】单元测试接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/test")
public class TestController {

    private final ICurrencyService currencyService;
    private final IAddressCodingService addressCodingService;
    private final IEmployeesCalendarService employeesCalendarService;
    private final IEmployeesContractService employeesContractService;
    private final ITestService testService;
    private final RedisUtils redisUtils;
    private final EmployeesDetailsService employeesDetailsService;
    private final ICompanyDetailsService companyDetailsService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Resource
    private MongoUtils mongoUtils;

    @GetMapping("/test1")
    @ApiOperation("测试1")
    public R test1(){
        currencyService.exchangeRateToBigDecimal("TWD", "CNY", BigDecimal.valueOf(100));
        return R.ok();
    }

    @GetMapping("/test2")
    @ApiOperation("测试2")
    public R test2(String address){
        //把地址存為經緯度 湖北省武汉市洪山区茉莉公馆 高雄市苓雅区四维三路2号 高雄市议会台北联络处 湖北省武汉市洪山区绿地国际理想城
        JSONObject jsonObject = (JSONObject) addressCodingService.addressCoding("address").getData();
        try {
            JSONObject result = (JSONObject) jsonObject.get("result");
            JSONObject location = (JSONObject) result.get("location");
            Double lng = (Double) location.get("lng");
            Double lat = (Double) location.get("lat");
        }catch (RuntimeException e){
            return R.failed("地址解析失敗");
        }
        return R.ok(jsonObject,"解析成功");
    }

    @GetMapping("/test3")
    @ApiOperation("测试3")
    public R test3() {
        DateSlot dateSlot = new DateSlot();
        dateSlot.setStart(LocalDate.of(2021,3,1));
        dateSlot.setEnd(LocalDate.of(2021,3,30));
        Map<LocalDate, List<TimeSlot>> res =  employeesContractService.getCalendarByContractId(dateSlot, 1);
        return R.ok("解析成功");
    }

    @GetMapping("/test4")
    @ApiOperation("多线程测试")
    public R test4(){
        for (int i = 0; i < 20; i++) {
            testService.syncMethod(i);
        }
        return R.ok();
    }
    @GetMapping("/test5")
    @ApiOperation("多线程测试2")
    public R test5(){
        testService.threadMethod();
        return R.ok();
    }


    @RequestMapping(value = "/test6", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ApiOperation("长链接测试")
    public String test6() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String re = "目前C股票价格为："+ new Random().nextInt(1000)+100;
        System.out.println("fawfgajkhugwak");
        return re;
    }

    @GetMapping("/test7")
    @ApiOperation("测试7")
    public R test7(){
        mongoUtils.createCollection("testddf");
        return R.ok();
    }

    @ApiOperation("redis数据导入")
    @GetMapping("/redisInto")
    public R test8(){
        /* 保洁员数据 */
        List<EmployeesDetails> employeesDetails = employeesDetailsService.list();
        fun(employeesDetails, "employees", "details");
        /* 公司数据 */
        List<CompanyDetails> companyDetails = companyDetailsService.list();
        fun(companyDetails, "company", "details");

        return R.ok(null, "存入成功");
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
//            try {
////                redisTemplate.opsForHash().put(key, "sss", CommonConstants.JacksonMapper.writeValueAsString(LocalDateTime.of(2020,1,8,20,30,00,00)));
//                redisTemplate.opsForHash().put(key, "sss", CommonConstants.JacksonMapper.writeValueAsString(x));
//
//            } catch (JsonProcessingException e) {
//                e.printStackTrace();
//            }
        });
    }
}
