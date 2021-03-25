package com.housekeeping.admin.dto;

import com.housekeeping.admin.entity.SysIndex;
import com.housekeeping.admin.entity.SysJobContend;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author su
 * @Date 2021/3/25 10:03
 */
@Data
@NoArgsConstructor
public class IndexData {

    private Integer id;         /* 主鍵id */
    private String name;        /* 元素名字 */
    private String priceSlot;   /* 推荐价格区间 */
    private Integer orderValue;      /* 顺序 */
    private String selectedLogo;     /* 已选中的logo--base64 */
    private String uncheckedLogo;    /* 未选中的logo——base64 */
    private List<SysJobContend> sysJobContends; /* 工作内容 */

    public IndexData(SysIndex sysIndex){
        this.id = sysIndex.getId();
        this.name = sysIndex.getName();
        this.priceSlot = sysIndex.getPriceSlot();
        this.orderValue = sysIndex.getOrderValue();
        this.selectedLogo = sysIndex.getSelectedLogo();
        this.uncheckedLogo = sysIndex.getUncheckedLogo();
    }

}
