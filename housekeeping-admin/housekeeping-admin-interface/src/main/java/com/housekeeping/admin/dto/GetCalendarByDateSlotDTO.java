package com.housekeeping.admin.dto;

import lombok.Data;

/**
 * @Author su
 * @Date 2021/2/3 17:23
 */
@Data
public class GetCalendarByDateSlotDTO {

    private DateSlot dateSlot;
    private Integer contractId;

}
