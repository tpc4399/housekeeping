package com.housekeeping.admin.vo;

import com.housekeeping.admin.entity.ManagerDetails;
import lombok.Data;

@Data
public class ManagerVo extends ManagerDetails {

    private String noCertifiedCompany;
}
