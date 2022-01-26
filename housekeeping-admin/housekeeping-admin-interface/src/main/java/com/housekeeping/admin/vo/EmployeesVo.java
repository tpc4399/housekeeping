package com.housekeeping.admin.vo;

import com.housekeeping.admin.entity.EmployeesDetails;
import lombok.Data;

@Data
public class EmployeesVo extends EmployeesDetails {

    private String noCertifiedCompany;

    private  Integer collectionVolume;

}
