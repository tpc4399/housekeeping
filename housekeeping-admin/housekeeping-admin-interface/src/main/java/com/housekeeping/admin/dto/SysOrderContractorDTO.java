package com.housekeeping.admin.dto;


import io.swagger.annotations.ApiParam;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SysOrderContractorDTO {

    @ApiParam("false=需求单 true=预约单")
    private Boolean temp;                   /* false=需求单 true=预约单 */
    private Integer companyId;              /* 公司_id */
    private Integer addressId;              /* 服务地址_id */
    private String jobContendIds;              /* 工作内容_ids */
    private LocalDate startTime;
    private LocalDate forwardTime;
}
