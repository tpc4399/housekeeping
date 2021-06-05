package com.housekeeping.common.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.ArrayList;
import java.util.List;

public  class PageUtils {

    public static Page getPages(Integer currentPage, Integer pageSize, List list) {
        Page page = new Page();
        int size = list.size();

        if(size==0){
            page.setCurrent(currentPage).setSize(pageSize).setTotal(list.size()).setRecords(new ArrayList());
        }

        else {
            if(pageSize > size) {
                pageSize = size;
            }

            // 求出最大页数，防止currentPage越界
            int maxPage = size % pageSize == 0 ? size / pageSize : size / pageSize + 1;

            if(currentPage > maxPage) {
                currentPage = maxPage;
            }

            // 当前页第一条数据的下标
            int curIdx = currentPage > 1 ? (currentPage - 1) * pageSize : 0;

            List pageList = new ArrayList();

            // 将当前页的数据放进pageList
            for(int i = 0; i < pageSize && curIdx + i < size; i++) {
                pageList.add(list.get(curIdx + i));
            }

            page.setCurrent(currentPage).setSize(pageSize).setTotal(list.size()).setRecords(pageList);
        }
        return page;
    }
}
