package com.housekeeping.admin.vo;

import com.housekeeping.admin.entity.EmployeesDetails;
import com.housekeeping.admin.pojo.WorkDetailsPOJO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class EmployeesDetailsDemandVo extends EmployeesDetails {

    private Integer demandEmployeesId;                      /* 报价单id */

    private Integer status;                                 /* 报价单状态 */

    private BigDecimal price;                               /* 报价状态 */

    private List<WorkDetailsPOJO> workDetailsPOJOList;      /* 服务时间 */
}
