package com.housekeeping.admin.vo;

import com.housekeeping.admin.entity.CustomerDetails;
import com.housekeeping.admin.entity.DemandOrder;
import com.housekeeping.admin.entity.EmployeesDetails;
import com.housekeeping.admin.entity.Skill;
import lombok.Data;

import java.util.List;

@Data
public class DemandEmployeesVo {

    private DemandOrder demandOrder;                                    /* 需求单*/

    private CustomerDetails customerDetails;

    private List<Skill> workContent;

    private List<Skill> workType;

    private List<EmployeesDetailsDemandVo> employeesDetailsDemandVos;   /* 报价信息 */
}
