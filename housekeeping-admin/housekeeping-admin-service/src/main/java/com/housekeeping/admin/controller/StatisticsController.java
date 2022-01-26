package com.housekeeping.admin.controller;

import com.housekeeping.admin.service.IStatisticsService;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags= "生意统计")
@RestController
@AllArgsConstructor
@RequestMapping("/statistics")
public class StatisticsController {

    private final IStatisticsService statisticsService;

    /*订单*/
    @ApiOperation("(订单)员工订单统计（type 0年 1月 2周 3日）")
    @GetMapping("/empOrder")
    public R empOrder(Integer empId,Integer type,Integer year,Integer month,Integer day){
        return statisticsService.empOrder(empId, type, year, month, day);
    }

    @ApiOperation("(订单)员工、开始日期、结束日期获取订单统计")
    @GetMapping("/empOrderByDate")
    public R empOrderByDate(Integer empId, String startDate,String endDate){
        return statisticsService.empOrderByDate(empId, startDate, endDate);
    }

    @ApiOperation("(订单)公司订单统计（type 0年 1月 2周 3日）")
    @GetMapping("/comOrder")
    public R comOrder(Integer comId,Integer type,Integer year,Integer month,Integer day){
        return statisticsService.comOrder(comId, type, year, month, day);
    }

    @ApiOperation("(订单)公司、开始日期、结束日期获取订单统计")
    @GetMapping("/comOrderByDate")
    public R comOrderByDate(Integer comId, String startDate,String endDate){
        return statisticsService.comOrderByDate(comId, startDate, endDate);
    }



    /*收入*/
    @ApiOperation("(收入)员工收入统计（type 0年 1月 2周 3日）")
    @GetMapping("/empIncome")
    public R empIncome(Integer empId,Integer type,Integer year,Integer month,Integer day){
        return statisticsService.empIncome(empId, type, year, month, day);
    }

    @ApiOperation("(收入)员工、开始日期、结束日期获取收入统计")
    @GetMapping("/empIncomeByDate")
    public R empIncomeByDate(Integer empId, String startDate,String endDate){
        return statisticsService.empIncomeByDate(empId, startDate, endDate);
    }

    @ApiOperation("(收入)公司收入统计（type 0年 1月 2周 3日）")
    @GetMapping("/comIncome")
    public R comIncome(Integer comId,Integer type,Integer year,Integer month,Integer day){
        return statisticsService.comIncome(comId, type, year, month, day);
    }

    @ApiOperation("(收入)公司、开始日期、结束日期获取收入统计")
    @GetMapping("/comIncomeByDate")
    public R comIncomeByDate(Integer comId, String startDate,String endDate){
        return statisticsService.comIncomeByDate(comId, startDate, endDate);
    }



    /*工作*/
    @ApiOperation("(工作)员工工作统计（type 0年 1月 2周 3日）")
    @GetMapping("/empWork")
    public R empWork(Integer empId,Integer type,Integer year,Integer month,Integer day){
        return statisticsService.empWork(empId, type, year, month, day);
    }

    @ApiOperation("(工作)员工、开始日期、结束日期获取工作统计")
    @GetMapping("/empWorkByDate")
    public R empWorkByDate(Integer empId, String startDate,String endDate){
        return statisticsService.empWorkByDate(empId, startDate, endDate);
    }

    @ApiOperation("(工作)公司工作统计（type 0年 1月 2周 3日）")
    @GetMapping("/comWork")
    public R comWork(Integer comId,Integer type,Integer year,Integer month,Integer day){
        return statisticsService.comWork(comId, type, year, month, day);
    }

    @ApiOperation("(工作)公司、开始日期、结束日期获取工作统计")
    @GetMapping("/comWorkByDate")
    public R comWorkByDate(Integer comId, String startDate,String endDate){
        return statisticsService.comWorkByDate(comId, startDate, endDate);
    }


}
