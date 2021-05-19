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
    //优先类型
    private Integer priorityType;

    //时薪分
    private Float getScope1(){
        Float maxScope = new Float(weight.get(ApplicationConfigConstants.priceScopeDouble));
        Float x = this.variable5.floatValue();
        Float y = new Float(0);
        Float low = lowPrice.floatValue();
        Float high = highPrice.floatValue();
        Float mid = (low+high)/2;
        if (x>=0 && x<=mid) y = maxScope*x / mid;
        if (x>mid) y = (-1)*maxScope*x/mid + 2*maxScope;
        return y;
    }

    //距离分  y = max*x / 10000 + max
    private Float getScope2(){
        Float maxScope = new Float(weight.get(ApplicationConfigConstants.distanceScoreDouble));
        Float x = new Float(this.variable6);
        Float y = (-1) * maxScope * x/new Float(50) + maxScope;
        return y;
    }

    //评价星级分 y = maxScope*(x/new Float(5));
    private Float getScope3(){
        Float maxScope = new Float(weight.get(ApplicationConfigConstants.evaluateScopeDouble));
        Float x = this.variable7;
        Float y = maxScope*(x/new Float(5));
        return y;
    }

    //工作内容匹配分
    private Float getScope4(){
        Float maxScope = new Float(weight.get(ApplicationConfigConstants.workScopeDouble));
        Float x = variable2;
        Float y = maxScope*x;
        return y;
    }

    //时间匹配分
    private Float getScope5(){
        Float maxScope = new Float(weight.get(ApplicationConfigConstants.timeScopeDouble));
        Float x = variable1;
        Float y = maxScope*x;
        return y;
    }

    //推广分
    private Float getScope6(){
        Float maxScope = new Float(weight.get(ApplicationConfigConstants.extensionScopeDouble));
        return variable8?maxScope:new Float(0);
    }

    public Float scope(){
        Float scope1 = new Float(0); //时薪分
        Float scope2 = new Float(0); //距离分
        Float scope3 = new Float(0); //评价分
        Float scope4 = new Float(0); //钟点工工作内容
        Float scope5 = new Float(0); //时间匹配分
        Float scope6 = new Float(0); //推广分
        if (priorityType == 0){
            scope1 = getScope1();
            scope2 = getScope2();
            scope3 = getScope3();
            scope4 = getScope4();
            scope5 = getScope5();
        }
        if (priorityType == 1 || priorityType == 2 || priorityType == 3 || priorityType == 4) {
            scope1 = getScope1();
            scope2 = getScope2();
            scope3 = getScope3();
            scope4 = getScope4();
        }

        return scope1+scope2+scope3+scope4+scope5+scope6;
    }

}
