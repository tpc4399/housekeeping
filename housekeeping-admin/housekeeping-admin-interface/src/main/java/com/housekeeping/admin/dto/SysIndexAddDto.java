package com.housekeeping.admin.dto;

import com.housekeeping.admin.vo.PriceSlotVo;
import lombok.Data;

import java.util.List;

/**
 * @Author su
 * @Date 2021/1/26 15:14
 */
@Data
public class SysIndexAddDto {
    private String name;                        /* 元素名字 */
    private List<Integer> jobParentIds;         /* 工作内容父_ids */
    private List<PriceSlotVo> priceSlotList;    /* 推荐价格区间 */
    private Integer orderValue;                      /* 顺序 */
}
