package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.entity.WorkDetails;

/**
 * @Author su
 * @Date 2021/4/28 16:10
 */
public interface IWorkDetailsService extends IService<WorkDetails> {

    void add(WorkDetails wd);

}
