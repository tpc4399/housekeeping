package com.housekeeping.admin.vo;

import com.housekeeping.admin.entity.ManagerDetails;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ManagerDetailsVO extends ManagerDetails {

    private String lastReviserName;

    private String companyName;
}
