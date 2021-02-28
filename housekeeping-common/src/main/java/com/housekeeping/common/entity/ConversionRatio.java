package com.housekeeping.common.entity;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 折算比例
 * @Author su
 * @Date 2021/2/28 13:00
 */
@Data
@Component
@RequestScope
public class ConversionRatio {

    private Map<String, BigDecimal> map = new ConcurrentHashMap<>();

    public synchronized Boolean containsByKey(String key){
        return map.containsKey(key);
    }

    public synchronized BigDecimal getValueByKey(String key){
        return map.get(key);
    }

    public synchronized void putKeyValue(String key, BigDecimal value){
        map.put(key, value);
    }

}
