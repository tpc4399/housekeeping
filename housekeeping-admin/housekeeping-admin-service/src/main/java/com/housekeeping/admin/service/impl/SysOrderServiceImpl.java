package com.housekeeping.admin.service.impl;

import com.aliyun.oss.OSSClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.SysOrderDTO;
import com.housekeeping.admin.entity.SysOrder;
import com.housekeeping.admin.entity.SysOrderPlan;
import com.housekeeping.admin.mapper.SysOrderMapper;
import com.housekeeping.admin.service.ISysOrderPlanService;
import com.housekeeping.admin.service.ISysOrderService;
import com.housekeeping.common.utils.CommonConstants;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 *
 * 第一种写法（1）：
 *
 * 原符号       <        <=      >       >=       &        '        "
 * 替换符号    &lt;    &lt;=   &gt;    &gt;=   &amp;   &apos;  &quot;
 * 例如：sql如下：
 * create_date_time &gt;= #{startTime} and  create_date_time &lt;= #{endTime}
 *
 * 第二种写法（2）：
 * 大于等于
 * <![CDATA[ >= ]]>
 * 小于等于
 * <![CDATA[ <= ]]>
 * 例如：sql如下：
 * create_date_time <![CDATA[ >= ]]> #{startTime} and  create_date_time <![CDATA[ <= ]]> #{endTime}
 *
 * @Author su
 * @create 2020/11/16 14:47
 */
@Service("sysOrderService")
public class SysOrderServiceImpl extends ServiceImpl<SysOrderMapper, SysOrder> implements ISysOrderService {

    @Resource
    private ISysOrderPlanService sysOrderPlanService;

    @Resource
    private OSSClient ossClient;

    @Value("${oss.bucketName}")
    private String bucketName;

    @Value("${oss.urlPrefix}")
    private String urlPrefix;

    @Override
    public R releaseOrder(SysOrder sysOrder) {
        sysOrder.setCreateTime(LocalDateTime.now());
        sysOrder.setCustomerId(TokenUtils.getCurrentUserId());
        baseMapper.insert(sysOrder);
        return R.ok();
    }

    @Override
    public R page(IPage<SysOrder> page, SysOrderDTO sysOrderDTO) {
        QueryWrapper queryWrapper = new QueryWrapper();
        if (CommonUtils.isNotEmpty(sysOrderDTO.getNumber())){
            queryWrapper.like("number", sysOrderDTO.getNumber());
        }
        if (CommonUtils.isNotEmpty(sysOrderDTO.getCompanyId())){
            queryWrapper.eq("company_id", sysOrderDTO.getCompanyId());
        }
        if (CommonUtils.isNotEmpty(sysOrderDTO.getCustomerId())){
            queryWrapper.eq("customer_id", sysOrderDTO.getCustomerId());
        }
        if (CommonUtils.isNotEmpty(sysOrderDTO.getAddressId())){
            queryWrapper.eq("address_id", sysOrderDTO.getAddressId());
        }
        if (CommonUtils.isNotEmpty(sysOrderDTO.getType())){
            queryWrapper.eq("type", sysOrderDTO.getType());
        }
        if (CommonUtils.isNotEmpty(sysOrderDTO.getCreateTimeStart())){
            queryWrapper.ge("create_time", sysOrderDTO.getCreateTimeStart());
        }
        if (CommonUtils.isNotEmpty(sysOrderDTO.getCreateTimeEnd())){
            queryWrapper.le("create_time", sysOrderDTO.getCreateTimeEnd());
        }
        if (CommonUtils.isNotEmpty(sysOrderDTO.getTotalTimeMin())){
            queryWrapper.ge("total_time", sysOrderDTO.getTotalTimeMin());
        }
        if (CommonUtils.isNotEmpty(sysOrderDTO.getTotalTimeMax())){
            queryWrapper.le("total_time", sysOrderDTO.getTotalTimeMax());
        }
        return R.ok(baseMapper.selectPage(page, queryWrapper), "查詢成功");
    }

    @Override
    public LocalDateTime getEvaluationDeadTime(Integer orderId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("order_id", orderId);
        queryWrapper.orderByDesc("date");
        List<SysOrderPlan> list = sysOrderPlanService.list(queryWrapper);

        LocalDate end = list.get(0).getDate();
        LocalDate deadDate = end.plusDays(6);
        LocalTime deadTime = LocalTime.of(0, 0,0);
        LocalDateTime deadDateTime = LocalDateTime.of(deadDate, deadTime);
        return deadDateTime;
    }

    @Override
    public void setEvaluationDeadTime(LocalDateTime evaluationDeadTime, Integer orderId) {
        baseMapper.setEvaluationDeadTime(evaluationDeadTime, orderId);
    }

    @Override
    public R doEvaluation(MultipartFile[] file, Float evaluationStar, String evaluationContent, Integer orderId) {
        String evaluationImage = "";
        LocalDateTime now = LocalDateTime.now();
        String nowString = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String catalogue = CommonConstants.HK_ORDER_EVALUATION_IMAGE_ABSTRACT_PATH_PREFIX_PROV + orderId;
        //上传照片
        for (int i = 0; i < file.length; i++) {
            String type = file[i].getOriginalFilename().split("\\.")[1];
            String fileAbstractPath = catalogue + "/" + nowString+"["+i+"]."+ type;
            try {
                ossClient.putObject(bucketName, fileAbstractPath, new ByteArrayInputStream(file[i].getBytes()));
                String url = urlPrefix + fileAbstractPath + " ";
                evaluationImage += url;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //生成照片url
        evaluationImage = evaluationImage.trim();
        //保存照片等评论信息
        baseMapper.doEvaluation(evaluationImage, false, evaluationStar, evaluationContent, orderId, LocalDateTime.now());
        return R.ok("評價成功");
    }
}
