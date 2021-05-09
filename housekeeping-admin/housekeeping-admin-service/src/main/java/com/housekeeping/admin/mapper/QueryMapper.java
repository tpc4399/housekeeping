package com.housekeeping.admin.mapper;

import java.util.List;

/**
 * @Author su
 * @create 2021/5/9 13:17
 */
public interface QueryMapper {

    /* 獲取可以做鐘點工的職員id 設置了時間表，設置了工作內容 */
    List<Integer> allCalendar();

    /* 獲取可以做包工的職員id，有發補過包工信息 */
    List<Integer> allContract();

    /* 獲取能幹包工，或者能幹鐘點的職員id */
    List<Integer> pool();

}
