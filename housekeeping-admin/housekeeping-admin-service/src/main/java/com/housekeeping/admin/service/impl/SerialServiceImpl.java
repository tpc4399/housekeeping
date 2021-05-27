package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.entity.Serial;
import com.housekeeping.admin.entity.SerialPhotos;
import com.housekeeping.admin.entity.SerialWorks;
import com.housekeeping.admin.mapper.SerialMapper;
import com.housekeeping.admin.pojo.OrderDetailsPOJO;
import com.housekeeping.admin.service.ISerialPhotosService;
import com.housekeeping.admin.service.ISerialService;
import com.housekeeping.admin.service.ISerialWorksService;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author su
 * @create 2021/5/26 9:44
 */
@Service("serialService")
public class SerialServiceImpl
        extends ServiceImpl<SerialMapper, Serial>
        implements ISerialService {

    @Resource
    private ISerialWorksService serialWorksService;
    @Resource
    private ISerialPhotosService serialPhotosService;

    @Override
    public R generatePipeline(OrderDetailsPOJO od) {
        Serial s = new Serial(od);
        List<SerialPhotos> serialPhotos = serialPhotos(od);
        List<SerialWorks> serialWorks = serialWorks(od);
        this.save(s);
        serialWorksService.saveBatch(serialWorks);
        serialPhotosService.saveBatch(serialPhotos);
        return R.ok(null, "成功保存流水");
    }

    @Override
    public R pageOfSerial(Page page) {
        return R.ok(this.page(page), "分頁查詢成功");
    }

    @Override
    public R serialPhotos(String serialNumber) {
        QueryWrapper qw = new QueryWrapper();
        qw.eq("serial_number", serialNumber);
        List<SerialPhotos> serialPhotosList = serialPhotosService.list(qw);
        return R.ok(serialPhotosList, "获取照片成功");
    }

    @Override
    public R serialWorks(String serialNumber) {
        QueryWrapper qw = new QueryWrapper();
        qw.eq("serial_number", serialNumber);
        List<SerialWorks> serialWorksList = serialWorksService.list(qw);
        return R.ok(serialWorksList, "获取工作成功");
    }

    private List<SerialPhotos> serialPhotos(OrderDetailsPOJO od){
        if (CommonUtils.isEmpty(od.getPhotos())) return new ArrayList<>();
        List<SerialPhotos> serialPhotos = od.getPhotos().stream().map(photo -> {
            SerialPhotos sp = new SerialPhotos(null, od.getSerialNumber(), photo.getPhotoUrl(), photo.getEvaluate());
            return sp;
        }).collect(Collectors.toList());
        return serialPhotos;
    }

    private List<SerialWorks> serialWorks(OrderDetailsPOJO od){
        if (CommonUtils.isEmpty(od.getWorkDetails())) return new ArrayList<>();
        od.getWorkDetails().stream().map(wd -> {
            StringBuilder sb = new StringBuilder();
            wd.getTimeSlots().forEach(timeSlot -> {
                String s = timeSlot.getTimeSlotStart()+"+"+timeSlot.getTimeSlotLength().toString()+" ";
                sb.append(s);
            });
            SerialWorks sw = new SerialWorks(null, od.getSerialNumber(), wd.getDate(), wd.getWeek(), sb.toString(), wd.getCanBeOnDuty(), wd.getTodayPrice());
            return sw;
        }).collect(Collectors.toList());
        return new ArrayList<>();
    }

}
