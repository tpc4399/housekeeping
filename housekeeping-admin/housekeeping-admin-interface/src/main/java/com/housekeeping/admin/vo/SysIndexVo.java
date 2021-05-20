package com.housekeeping.admin.vo;

import com.housekeeping.admin.entity.SysJobContend;
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
    private Integer orderValue;                 /* 顺序 */
    private String selectedLogo;                /* 已选中的logo--base64 */
    private String uncheckedLogo;               /* 未选中的logo——base64 */
    private String newSelectedLogo;        /* 新已选中的logo--base64 */
    private String newUncheckedLogo;    /* 新未选中的logo */
    private List<SysJobContend> sysJobContends; /* 工作内容 */
}
