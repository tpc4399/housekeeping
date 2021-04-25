package com.housekeeping.admin.service.impl;

import com.aliyun.oss.OSSClient;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.RequestToChangeAddressDTO;
import com.housekeeping.admin.entity.CompanyDetails;
import com.housekeeping.admin.entity.CustomerAddress;
import com.housekeeping.admin.entity.NotificationOfRequestForChangeOfAddress;
import com.housekeeping.admin.entity.OrderDetails;
import com.housekeeping.admin.mapper.OrderDetailsMapper;
import com.housekeeping.admin.pojo.OrderDetailsPOJO;
import com.housekeeping.admin.pojo.OrderPhotoPOJO;
import com.housekeeping.admin.service.ICustomerAddressService;
import com.housekeeping.admin.service.INotificationOfRequestForChangeOfAddressService;
import com.housekeeping.admin.service.IOrderDetailsService;
import com.housekeeping.common.utils.CommonConstants;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Author su
 * @Date 2021/4/19 9:46
 */
@Service("orderDetailsService")
public class OrderDetailsServiceImpl extends ServiceImpl<OrderDetailsMapper, OrderDetails> implements IOrderDetailsService {

    @Resource
    private OSSClient ossClient;
    @Value("${oss.bucketName}")
    private String bucketName;
    @Value("${oss.urlPrefix}")
    private String urlPrefix;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private ICustomerAddressService customerAddressService;
    @Resource
    private INotificationOfRequestForChangeOfAddressService notificationOfRequestForChangeOfAddressService;

    @Override
    public Integer orderRetentionTime(Integer employeesId) {
        return baseMapper.orderRetentionTime(employeesId);
    }

    @Override
    public R requestToChangeAddress(RequestToChangeAddressDTO dto) {
        CustomerAddress ca = customerAddressService.getById(dto.getAddressId());
        NotificationOfRequestForChangeOfAddress na = new NotificationOfRequestForChangeOfAddress(
                null,
                dto.getNumber(),
                ca.getName(),
                ca.getPhone(),
                ca.getPhonePrefix(),
                ca.getAddress(),
                new Float(ca.getLat()),
                new Float(ca.getLng()),
                LocalDateTime.now(),
                null);
        synchronized (this){
            notificationOfRequestForChangeOfAddressService.save(na);
            na = (NotificationOfRequestForChangeOfAddress) CommonUtils
                    .getMaxId("notification_of_request_for_change_of_address", notificationOfRequestForChangeOfAddressService);
        }

        //TODO 将内容发送到聊天框

        //TODO 将内容更新到保洁员的通知列表


        return R.ok(null, "申請成功");
    }

    @Override
    public R requestToChangeAddressHandle(Integer id, Boolean result) {
        notificationOfRequestForChangeOfAddressService.requestToChangeAddressHandle(id, result);
        if (result){
            NotificationOfRequestForChangeOfAddress na = notificationOfRequestForChangeOfAddressService.getById(id);
            Set<String> keys = redisTemplate.keys("OrderToBePaid:employeesId*:" + na.getNumber());
            String[] keysArr = (String[]) keys.toArray();
            String key = keysArr[0];
            Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
            OrderDetailsPOJO odp = null;
            try {
                odp = (OrderDetailsPOJO) CommonUtils.mapToObject(map, OrderDetailsPOJO.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            odp.setName2(na.getName());
            odp.setPhone2(na.getPhone());
            odp.setPhPrefix2(na.getPhPrefix());
            odp.setAddress(na.getAddress());
            odp.setLat(na.getLat());
            odp.setLng(na.getLng());
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime payDeadLine = now.plusHours(odp.getH());
            odp.setUpdateDateTime(now);
            odp.setPayDeadline(payDeadLine);
            Map<String, Object> map2 = new HashMap<>();
            try {
                map2 = CommonUtils.objectToMap(odp);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            redisTemplate.opsForHash().putAll(key, map2);
        }
        return R.ok(null, "操作成功");
    }

    @Override
    public R updateOrder(Integer number, Integer employeesId) {
        LocalDateTime now = LocalDateTime.now();
        String key = "OrderToBePaid:employeesId" + employeesId + ":" + number;
        Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
        OrderDetailsPOJO odp = null;
        try {
            odp = (OrderDetailsPOJO) CommonUtils.mapToObject(map, OrderDetailsPOJO.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LocalDateTime payDeadLine = now.plusHours(odp.getH());
        odp.setUpdateDateTime(now);
        odp.setPayDeadline(payDeadLine);
        Map<String, Object> map2 = new HashMap<>();
        try {
            map2 = CommonUtils.objectToMap(odp);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        redisTemplate.opsForHash().putAll(key, map2);
        return R.ok(null, "更新成功");
    }

    @Override
    public R pay(Long number, Integer employeesId, MultipartFile[] photos, String[] evaluates, String payType, String remarks) {
        /* List<OrderPhotoPOJO> pojoList准备 */
        List<OrderPhotoPOJO> pojoList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        String nowString = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String catalogue = CommonConstants.HK_ORDER_PHOTOS_ABSTRACT_PATH_PREFIX_PROV + number;
        File mkdir = new File(catalogue);
        if (!mkdir.exists()){
            mkdir.mkdirs();
        }
        AtomicReference<Integer> count = new AtomicReference<>(0);
        Arrays.stream(photos).forEach(file -> {
            String fileType = file.getOriginalFilename().split("\\.")[1];
            String fileName = nowString + "[" + count.toString() + "]."+ fileType;
            String fileAbstractPath = catalogue + "/" + fileName;
            try {
                ossClient.putObject(bucketName, fileAbstractPath, new ByteArrayInputStream(file.getBytes()));
                OrderPhotoPOJO pojo = new OrderPhotoPOJO(urlPrefix + fileAbstractPath, evaluates[count.get()]);
                pojoList.add(pojo);
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                count.getAndSet(count.get() + 1);
            }
        });

        String key = "OrderToBePaid:employeesId" + employeesId + ":" + number;
        Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
        OrderDetailsPOJO odp = null;
        try {
            odp = (OrderDetailsPOJO) CommonUtils.mapToObject(map, OrderDetailsPOJO.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        odp.setPhotos(pojoList);
        odp.setPayType(payType);
        odp.setRemarks(remarks);
        Map<String, Object> map2 = new HashMap<>();
        try {
            map2 = CommonUtils.objectToMap(odp);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        redisTemplate.opsForHash().putAll(key, map2);

        return R.ok(null, "修改成功");
    }
}
