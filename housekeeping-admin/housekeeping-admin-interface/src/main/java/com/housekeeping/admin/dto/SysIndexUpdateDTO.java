package com.housekeeping.admin.dto;

import com.housekeeping.admin.vo.PriceSlotVo;
import lombok.Data;

import java.util.List;

/**
 * @Author su
 * @Date 2021/3/3 17:01
 */
@Data
public class SysIndexUpdateDTO {
    private Integer id;
    private String name;                        /* 元素名字 */
    private List<Integer> jobs;                 /* 工作内容_ids */
    private List<PriceSlotVo> priceSlotList;    /* 推荐价格区间 */
    private Integer orderValue;                 /* 顺序 */
    private String selectedLogo;                /* 已选中的logo--base64 */
    private String uncheckedLogo;               /* 未选中的logo——base64 */
}
