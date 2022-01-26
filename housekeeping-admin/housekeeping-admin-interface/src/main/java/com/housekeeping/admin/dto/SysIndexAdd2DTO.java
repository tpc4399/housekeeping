package com.housekeeping.admin.dto;

import com.housekeeping.admin.vo.PriceSlotVo;
import lombok.Data;

import java.util.List;

@Data
public class SysIndexAdd2DTO {

    private String name;                        /* 元素名字 */
    private List<JobDTO> jobs;                 /* 工作内容_ids */
    private List<PriceSlotVo> priceSlotList;    /* 推荐价格区间 */
    private Integer orderValue;                 /* 顺序 */
    private String selectedLogo;                /* 已选中的logo--base64 */
    private String uncheckedLogo;               /* 未选中的logo——base64 */
    private String newSelectedLogo;        /* 新已选中的logo--base64 */
    private String newUncheckedLogo;    /* 新未选中的logo */
}
