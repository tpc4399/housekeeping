package com.housekeeping.admin.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author su
 * @Date 2021/1/26 15:14
 */
@Data
public class SysIndexVo {
    private Integer id;                         /* 主鍵id */
    private String name;                        /* 元素名字 */
    private List<PriceSlotVo> priceSlotList;    /* 推荐价格区间 */
    private Integer orderValue;                      /* 顺序 */
}
