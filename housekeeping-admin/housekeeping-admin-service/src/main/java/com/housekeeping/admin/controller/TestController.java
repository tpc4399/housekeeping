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
import com.sun.org.apache.xpath.internal.operations.Bool;
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
import java.io.*;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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

    @PassToken
    @GetMapping("/noToken/test1")
    @ApiOperation("测试1")
    public R test1(){
        BigDecimal res = currencyService.exchangeRateToBigDecimal("TWD", "CNY", BigDecimal.valueOf(100));
        Map<String, Float> res1 =  currencyService.realTimeExchangeRate();
        return R.ok(res);
    }

    @GetMapping("/noToken/test2")
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

    @GetMapping("/noToken/test3")
    @ApiOperation("测试3")
    public R test3() {
        DateSlot dateSlot = new DateSlot();
        dateSlot.setStart(LocalDate.of(2021,3,1));
        dateSlot.setEnd(LocalDate.of(2021,3,30));
        Map<LocalDate, List<TimeSlot>> res =  employeesContractService.getCalendarByContractId(dateSlot, 1);
        return R.ok("解析成功");
    }

    @GetMapping("/noToken/test4")
    @ApiOperation("多线程测试")
    public R test4(){
        synchronized (this){
            int stock = Integer.valueOf((String) redisTemplate.opsForValue().get("stock"));
            if (stock > 0){
                int realStock = stock - 1;
                redisTemplate.opsForValue().set("stock", realStock+"");
                System.out.println("扣款成功，剩余库存："+realStock);
            }else {
                System.out.println("扣款失败，库存不足");
            }
        }
        return R.ok();
    }
    @GetMapping("/noToken/test5")
    @ApiOperation("多线程测试2")
    public R test5(){
        testService.threadMethod();
        return R.ok();
    }


    @RequestMapping(value = "/noToken/test6", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
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

    @GetMapping("/noToken/test7")
    @ApiOperation("测试7")
    public R test7(){
        return R.ok();
    }

    @ApiOperation("redis数据导入")
    @GetMapping("/noToken/redisInto")
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
        });
    }

    @ApiOperation("员工判断存在性检索")
    @GetMapping("/judgment")
    public R test9(Integer employeesId){
        return R.ok(employeesDetailsService.judgmentOfExistenceHaveJurisdictionOverManager(employeesId), "员工存在性检索");
    }

    @ApiOperation("数据导入")
    @GetMapping("/noToken/import")
    public R test10() throws IOException {
        List<String> list = new ArrayList<>();
        File file = new File("F:\\test.txt");
        InputStreamReader read = null;// 考虑到编码格式
        try {
            read = new InputStreamReader(new FileInputStream(file), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader bufferedReader = new BufferedReader(read);
        String lineTxt = null;
        while ((lineTxt = bufferedReader.readLine()) != null) {
            String[] strings = lineTxt.split("\\/");
            String add = URLDecoder.decode(strings[5], "UTF-8" );
            String s = add+"  "+strings[6];
            System.out.println(s);
            list.add(lineTxt);
        }
        read.close();
        return R.ok(null, "导入成功");
    }

}
