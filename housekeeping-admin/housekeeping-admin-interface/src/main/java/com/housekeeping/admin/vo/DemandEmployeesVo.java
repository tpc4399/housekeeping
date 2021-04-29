package com.housekeeping.admin.vo;

import com.housekeeping.admin.entity.DemandOrder;
import com.housekeeping.admin.entity.EmployeesDetails;
import lombok.Data;

import java.util.List;

@Data
public class DemandEmployeesVo {

    private DemandOrder demandOrder;                                    /* 报价单*/

    private List<EmployeesDetailsDemandVo> employeesDetailsDemandVos;   /* 报价信息 */
}
