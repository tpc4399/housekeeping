package com.housekeeping.common.entity;

import com.housekeeping.admin.dto.ContractAndPriceDetails;
import com.housekeeping.admin.dto.JobAndPriceDetails;
import com.housekeeping.common.utils.ApplicationConfigConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Author su
 * @Date 2021/3/10 14:36
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeesScope {

    private Double scopeOfOrder; //保洁员设定的直线范围
    private Double instance;     //计算出的实际范围
    private Boolean areaIsOk;    //地区是否符合
    private Boolean extensionIsOk;                      /* 员工是否推广 */
    private List<JobAndPriceDetails> service1;          /* 匹配到的钟点工信息 */
    private List<ContractAndPriceDetails> service2;     /* 匹配到的包工信息 */
    private BigDecimal lowPrice;                        /* 最低价 */
    private BigDecimal highPrice;                       /* 最好价 */
    private Float score;                                /* 保洁员的评分 */
    private Map<String, String> weight;                 /* 匹配值得各项权重 */
    private Integer numberOfOrdersReceived;             /* 接单次数 */

    /**
     *  距离分数 scope1
     */
    public Double getScope1(){
        Double k = ((-1) * new Double(weight.get(ApplicationConfigConstants.distanceScoreDouble))) / this.scopeOfOrder;
        Double x = this.instance;
        Double b = new Double(weight.get(ApplicationConfigConstants.distanceScoreDouble));
        Double y = k*x + b;
        return y;
    }

    /**
     *  地区分数 scope2
     */
    public Double getScope2(){
        Double scope = this.areaIsOk ? new Double(weight.get(ApplicationConfigConstants.areaScopeDouble)) : 0.0;
        return scope;
    }

    /**
     *  价格分数 scope3
     */
    public Double getScope3(){
        Integer length = service1.size() + service2.size();
        AtomicReference<BigDecimal> total = new AtomicReference<>(new BigDecimal(0));
        service1.forEach(x -> {
            total.set(total.get().add(x.getTotalPrice()));
        });
        service2.forEach(x -> {
            total.set(total.get().add(x.getTotalPrice()));
        });
        BigDecimal price = total.get().divide(new BigDecimal(length));

        Double k1 = new Double(weight.get(ApplicationConfigConstants.priceScopeDouble)) / lowPrice.doubleValue();
        Double b2 = new Double(weight.get(ApplicationConfigConstants.priceScopeDouble));
        Double k3 = (-1) * new Double(weight.get(ApplicationConfigConstants.priceScopeDouble)) / lowPrice.doubleValue();
        Double b3 = k1 * lowPrice.add(highPrice).doubleValue();
        Double x = price.doubleValue();
        Double y = 0.0;

        if (x < lowPrice.doubleValue()){
            y = k1*x;
        }else if (x < highPrice.doubleValue()){
            y = b2;
        }else if (x < lowPrice.doubleValue() + highPrice.doubleValue()){
            y = k3*x + b3;
        }
        return y;
    }

    /**
     *  出勤率分数 scope4
     */
    public Double getScope4(){
        Integer length = service1.size() + service2.size();
        AtomicReference<BigDecimal> att = new AtomicReference<>(new BigDecimal(0));
        service1.forEach(x -> {
            att.set(att.get().add(new BigDecimal(x.getAttendance())));
        });
        service2.forEach(x -> {
            att.set(att.get().add(new BigDecimal(x.getAttendance())));
        });
        BigDecimal attendance = att.get().divide(new BigDecimal(length));
        BigDecimal totalScope = new BigDecimal(10.0 * length + new Double(weight.get(ApplicationConfigConstants.attendanceScopeDouble)));
        Double scope =  attendance.multiply(totalScope).doubleValue();
        return scope;
    }

    /**
     *  评价分数 scope5
     */
    public Double getScope5(){
        Double k = new Double(weight.get(ApplicationConfigConstants.evaluateScopeDouble)) / new Double(5);
        Double x = new Double(this.score);
        Double y = k*x;
        return y;
    }


    /**
     *  推广分数 scope6
     */
    public Double getScope6(){
        Double scope = this.extensionIsOk ? new Double(weight.get(ApplicationConfigConstants.extensionScopeDouble)) : 0.0;
        return scope;
    }

    /**
     *  接单次数分数 scope7
     */
    public Double getScope7(){
        Double scope = new Double(weight.get(ApplicationConfigConstants.numberOfOrdersReceivedScopeDouble));
        Double x = new Double(this.numberOfOrdersReceived);
        Double y = new Double(0.0);
        if (x>=0 && x<scope-1){
            y = x;
        }else if (x>=scope-1){
            y = Math.pow(x-scope+2, -1) + scope;
        }
        return y;
    }

    public Double getScopeTotal(){
        Double scope =
                this.getScope1()
                + this.getScope2()
                + this.getScope3()
                + this.getScope4()
                + this.getScope5()
                + this.getScope6()
                + this.getScope7();
        return scope;
    }

}
