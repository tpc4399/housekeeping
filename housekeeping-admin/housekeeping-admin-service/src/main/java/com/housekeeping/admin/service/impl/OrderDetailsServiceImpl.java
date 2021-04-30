package com.housekeeping.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.aliyun.oss.OSSClient;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.PaymentCallbackDTO;
import com.housekeeping.admin.dto.RequestToChangeAddressDTO;
import com.housekeeping.admin.dto.SmilePayVerificationCodeDTO;
import com.housekeeping.admin.entity.*;
import com.housekeeping.admin.mapper.OrderDetailsMapper;
import com.housekeeping.admin.pojo.OrderDetailsPOJO;
import com.housekeeping.admin.pojo.OrderPhotoPOJO;
import com.housekeeping.admin.service.*;
import com.housekeeping.common.utils.CommonConstants;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.spring.web.json.Json;

import javax.annotation.Resource;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

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
    @Resource
    private IOrderDetailsService orderDetailsService;
    @Resource
    private IOrderPhotosService orderPhotosService;
    @Resource
    private IWorkDetailsService workDetailsService;

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

            redisTemplate.opsForHash().put(key, "name2", na.getName());
            redisTemplate.opsForHash().put(key, "phone2", na.getPhone());
            redisTemplate.opsForHash().put(key, "phPrefix2", na.getPhPrefix());
            redisTemplate.opsForHash().put(key, "address", na.getAddress());
            redisTemplate.opsForHash().put(key, "lat", na.getLat());
            redisTemplate.opsForHash().put(key, "lng", na.getLng());
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime payDeadLine = now.plusHours(odp.getH());
            redisTemplate.opsForHash().put(key, "updateDateTime", now);
            redisTemplate.opsForHash().put(key, "payDeadline", payDeadLine);

        }
        return R.ok(null, "操作成功");
    }

    @Override
    public R updateOrder(Integer number, Integer employeesId) {
        LocalDateTime now = LocalDateTime.now();
        String key = "OrderToBePaid:employeesId" + employeesId + ":" + number;
        OrderDetailsPOJO odp = null;
        Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
        try {
            odp = (OrderDetailsPOJO) CommonUtils.mapToObject(map, OrderDetailsPOJO.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LocalDateTime payDeadLine = now.plusHours(odp.getH());
        redisTemplate.opsForHash().put(key, "updateDateTime", now);
        redisTemplate.opsForHash().put(key, "payDeadline", payDeadLine);

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
        redisTemplate.opsForHash().put(key, "payType", payType);
        redisTemplate.opsForHash().put(key, "photos", pojoList);
        redisTemplate.opsForHash().put(key, "remarks", remarks);

        return R.ok(null, "修改成功");
    }

    @Async
    @Override
    public void paymentCallback(PaymentCallbackDTO dto){
        System.out.println("PaymentCallback:" + LocalDateTime.now() + "  " + dto.toString());

    }

    @Override
    public Boolean smilePayVerificationCode(SmilePayVerificationCodeDTO dto) {
        /**
         * A =	商家驗證參數 (共四碼,不足四碼前面補零)
         * 目前的商家驗證參數：1974
         * 例如商家驗證參數為1234 則 A = 1234
         *
         * B =	收款金額 (取八碼,不足八碼前面補零)
         * 例如金額為 532 元 則 B = 00000532
         *
         * C =	Smseid參數 (回送  Roturl 時 的 Smseid 參數的後四碼，如不為數字則以 9 替代 )
         * 例如Smseid為 12_24_123  ，後四碼 為 "_123"  則 C = 9123
         *
         * D =
         *
         * A & B & C
         * 以上列範例為例 ： D = 1234000005329123
         *
         * E =
         *
         * 取 D 的偶數位字數(由前面算起)相加後乘以 3
         * 以 D 為例 1234000005329123
         * 取 D 的偶數位字數(藍色字體)：( 2+4+0+0+5+2+1+3 ) X 3 = 51
         * F =
         *
         * 取 D 的奇數位字數(由前面算起)相加後乘以 9
         * 以 D 為例 1234000005329123
         * 取 D 的奇數位字數(紅色字體)：( 1+3+0+0+0+3+9+2 ) X 9 = 162
         *
         *
         * SmilePay驗證碼 ： E +  F = 51 + 162 = 213
         *
         *
         * 將資料回送 Roturl 時之 Mid_smilepay 即為 213
         * */

        String A = dto.getA();
        String B = dto.getB();
        Integer zeroLength = 8 - B.length();
        for (int i = 0; i < zeroLength; i++) {
            B = "0"+B;
        }
        String C = dto.getC();
        C = C.substring(C.length()-4, C.length());
        for (int i = 0; i < C.length(); i++) {
            char x = C.charAt(i);
            if (x < 48 || x > 57) C = C.replace(x+"", "9");
        }
        String D = A+B+C;


        int eSum = 0;
        for (int i = 1; i < D.length(); i+=2) {
            eSum += Integer.valueOf(D.substring(i, i+1));
        }
        Integer E = eSum * 3;

        int fSum = 0;
        for (int i = 0; i < D.length(); i+=2) {
            fSum += Integer.valueOf(D.substring(i, i+1));
        }
        Integer F = fSum * 9;
        Integer value = E + F;

        return value.toString().equals(dto.getCode());
    }

    @Override
    public Long toBePaid(Long number, Integer employeesId) {
        String key = "OrderToBePaid:employeesId" + employeesId + ":" + number;
        OrderDetailsPOJO odp = null;
        Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
        try {
            odp = (OrderDetailsPOJO) CommonUtils.mapToObject(map, OrderDetailsPOJO.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        OrderDetails od = new OrderDetails(odp);
        List<OrderPhotos> ops = orderPhotos(odp);
        List<WorkDetails> wds = workDetails(odp);
        orderDetailsService.save(od);
        if (CommonUtils.isNotEmpty(ops)) orderPhotosService.saveBatch(ops);
        if (CommonUtils.isNotEmpty(wds)) workDetailsService.saveBatch(wds);
        return number;
    }

    private List<OrderPhotos> orderPhotos(OrderDetailsPOJO pojo){
        if (CommonUtils.isEmpty(pojo.getPhotos())) return new ArrayList<>();
        List<OrderPhotos> ops = pojo.getPhotos().stream().map(x -> {
            return new OrderPhotos(null, Long.valueOf(pojo.getNumber()), x.getPhotoUrl(), x.getEvaluate());
        }).collect(Collectors.toList());
        return ops;
    }

    private List<WorkDetails> workDetails(OrderDetailsPOJO pojo){
        if (CommonUtils.isEmpty(pojo.getWorkDetails())) return new ArrayList<>();
        List<WorkDetails> wds = pojo.getWorkDetails().stream().map(x -> {
            if (x.getCanBeOnDuty()) return new WorkDetails(null, Long.valueOf(pojo.getNumber()), x.getDate(), x.getWeek(), null);
            return null;
        }).collect(Collectors.toList());
        return wds;
    }

}
