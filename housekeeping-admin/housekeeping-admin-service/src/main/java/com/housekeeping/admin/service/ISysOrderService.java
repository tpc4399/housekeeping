package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.SysOrderDTO;
import com.housekeeping.admin.entity.SysOrder;
import com.housekeeping.common.utils.R;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

/**
 * @Author su
 * @create 2020/11/16 14:46
 */
public interface ISysOrderService extends IService<SysOrder> {

    R releaseOrder(SysOrder sysOrder);

    R page(IPage<SysOrder> page, SysOrderDTO sysOrderDTO);

    LocalDateTime getEvaluationDeadTime(Integer orderId);

    void setEvaluationDeadTime(LocalDateTime evaluationDeadTime, Integer orderId);

    R doEvaluation(MultipartFile[] file, Float evaluationStar, String evaluationContent, Integer orderId);

}
