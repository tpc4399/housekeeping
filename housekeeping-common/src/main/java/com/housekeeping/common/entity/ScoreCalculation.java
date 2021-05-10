package com.housekeeping.common.entity;

import com.housekeeping.common.utils.ApplicationConfigConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @Author su
 * @create 2021/5/9 15:57
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoreCalculation {

    //钟点工时间匹配率
    private Float variable1;
    //钟点工工作內容匹配率   如果不能匹配，那就是0
    private Float variable2;
    //包工工作内容匹配率   如果不能匹配，那就是0
    private Float variable4;
    //最低時薪
    private BigDecimal variable5;
    //距離
    private String variable6;
    //評價星級
    private Float variable7;
    //推广
    private Boolean variable8;
    //分数权重
    private Map<String, String> weight;

    private BigDecimal lowPrice;

    private BigDecimal highPrice;


    //时薪分  y=(-maxScope)/(x-low-1)   y=maxScope   y=(maxScope)/(x-high+1)
            //y = max/low         y=maxScope      y=(maxScope)/(x-high+1)
    private Float getScope1(){
        Float maxScope = new Float(weight.get(ApplicationConfigConstants.priceScopeDouble));
        Float x = this.variable5.floatValue();
        Float y = new Float(0);
        Float low = lowPrice.floatValue();
        Float high = highPrice.floatValue();
        if (x>=0 && x<=low) y = maxScope*x / low;
        if (x>low && x<high) y = maxScope;
        if (x>high && x<2*high-low)  y = (maxScope*x)/(high-low) + maxScope;
        if (x>2*high-low) y = new Float(0);
        return y;
    }

    //距离分  y = max*x / 10000 + max
    private Float getScope2(){
        Float maxScope = new Float(weight.get(ApplicationConfigConstants.distanceScoreDouble));
        Float x = new Float(this.variable6);
        Float y = (-1) * maxScope * x/new Float(50000) + maxScope;
        return y;
    }

    //评价星级分 y = maxScope*(x/new Float(5));
    private Float getScope3(){
        Float maxScope = new Float(weight.get(ApplicationConfigConstants.evaluateScopeDouble));
        Float x = this.variable7;
        Float y = maxScope*(x/new Float(5));
        return y;
    }

    //工作分
    private Float getScope4(){
        Float maxScope = new Float(weight.get(ApplicationConfigConstants.attendanceScopeDouble));
        Float x = variable1+variable2+variable4;
        Float y = maxScope*x;
        return y;
    }

    private Float getScope5(){
        Float maxScope = new Float(weight.get(ApplicationConfigConstants.extensionScopeDouble));
        return variable8?maxScope:new Float(0);
    }

    public Float scope(){
        Float scope1 = getScope1();
        Float scope2 = getScope2();
        Float scope3 = getScope3();
        Float scope4 = getScope4();
        Float scope5 = getScope5();
        return scope1+scope2+scope3+scope4+scope5;
    }

}
