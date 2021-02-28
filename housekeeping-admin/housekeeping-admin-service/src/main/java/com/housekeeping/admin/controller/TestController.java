package com.housekeeping.admin.controller;

import com.housekeeping.admin.dto.DateSlot;
import com.housekeeping.admin.service.*;
import com.housekeeping.admin.vo.TimeSlot;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import net.sf.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
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
public class TestController {

    private final ICurrencyService currencyService;
    private final IAddressCodingService addressCodingService;
    private final IEmployeesCalendarService employeesCalendarService;
    private final IEmployeesContractService employeesContractService;
    private final ITestService testService;

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
}
