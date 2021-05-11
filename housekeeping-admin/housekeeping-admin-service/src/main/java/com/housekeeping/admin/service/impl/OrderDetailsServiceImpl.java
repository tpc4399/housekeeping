package com.housekeeping.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.aliyun.oss.OSSClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.PaymentCallbackDTO;
import com.housekeeping.admin.dto.RequestToChangeAddressDTO;
import com.housekeeping.admin.dto.SmilePayVerificationCodeDTO;
import com.housekeeping.admin.entity.*;
import com.housekeeping.admin.mapper.OrderDetailsMapper;
import com.housekeeping.admin.pojo.OrderDetailsPOJO;
import com.housekeeping.admin.pojo.OrderPhotoPOJO;
import com.housekeeping.admin.pojo.WorkDetailsPOJO;
import com.housekeeping.admin.service.*;
import com.housekeeping.admin.vo.TimeSlot;
import com.housekeeping.common.utils.CommonConstants;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
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
import java.time.LocalTime;
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
    @Resource
    private EmployeesDetailsService employeesDetailsService;
    @Resource
    private ICustomerDetailsService customerDetailsService;

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
    public R inputSql(String number, Boolean status) {
        /* odp 获取订单信息 */
        Set<String> keys = redisTemplate.keys("OrderToBePaid:employeesId*:" + number);
        Object[] keysArr = (Object[]) keys.toArray();
        String key = keysArr[0].toString();
        OrderDetailsPOJO odp = null;
        Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
        try {
            odp = (OrderDetailsPOJO) CommonUtils.mapToObject(map, OrderDetailsPOJO.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /* 验证是否是自己能操作的订单，保洁员和客户只能作废属于自己的订单 */
        if (!this.orderVerification(odp)) return R.failed(null, "訂單不存在（這可能不是你的訂單）");

        OrderDetails od = new OrderDetails(odp);
        if (status) od.setOrderState(CommonConstants.ORDER_STATE_TO_BE_SERVED);//订单已支付，待服务
        else od.setOrderState(CommonConstants.ORDER_STATE_VOID);//取消订单

        List<OrderPhotos> ops = orderPhotos((List<OrderPhotoPOJO>) map.get("photos"), number);
        List<WorkDetails> wds = workDetails((List<WorkDetailsPOJO>) map.get("workDetails"), number);
        orderDetailsService.save(od);
        if (CommonUtils.isNotEmpty(ops)) orderPhotosService.saveBatch(ops);
        if (CommonUtils.isNotEmpty(wds)) workDetailsService.saveBatch(wds);
        return R.ok(number, "操作成功");
    }

    @Override
    public R queryByCus(Integer type) {
        /* type = 0全部 1待付款 2待服务 3进行中 4待评价 5已完成 */
        CustomerDetails cd = customerDetailsService.getByUserId(TokenUtils.getCurrentUserId());
        Integer customerId = cd.getId();
        Map<String, List> resMap = new HashMap<>();
        if (type.equals(0)){
            resMap.put("1", this.order1ByCustomer(customerId));
            resMap.put("2", this.order2ByCustomer(customerId));
            resMap.put("3", this.order3ByCustomer(customerId));
            resMap.put("4", this.order4ByCustomer(customerId));
            resMap.put("5", this.order5ByCustomer(customerId));
        }else if (type.equals(1)){
            resMap.put("1", this.order1ByCustomer(customerId));
        }else if (type.equals(2)){
            resMap.put("2", this.order2ByCustomer(customerId));
        }else if (type.equals(3)){
            resMap.put("3", this.order3ByCustomer(customerId));
        }else if (type.equals(4)){
            resMap.put("4", this.order4ByCustomer(customerId));
        }else if (type.equals(5)){
            resMap.put("5", this.order5ByCustomer(customerId));
        }
        return R.ok(resMap, "获取成功");
    }

    /**
     * 0 --> 订单作废中     订单不进行保留 Order void
     * 2 --> 未付款        待付款状态  To be paid
     * 3 --> 付款处理中     已付款但是还没收到付款的 Payment processing
     * 4 --> 已付款        待服务      To be served
     * 5 --> 已付款        进行状态    have in hand
     * 8 --> 已做完工作     待确认状态  To be confirmed
     * 15 -->             待评价状态  To be evaluated
     * 20 --> 已评价       已完成状态  Completed
     */
    @Override
    public R queryByEmp(Integer type) {
        /* type = 0全部 1待付款 2待服务 3进行中 4待评价 5已完成 */
        Integer employeesId = employeesDetailsService.getEmployeesIdByUserId(TokenUtils.getCurrentUserId());
        Map<String, List> resMap = new HashMap<>();
        if (type.equals(0)){
            resMap.put("1", this.order1ByEmployees(employeesId));
            resMap.put("2", this.order2ByEmployees(employeesId));
            resMap.put("3", this.order3ByEmployees(employeesId));
            resMap.put("4", this.order4ByEmployees(employeesId));
            resMap.put("5", this.order5ByEmployees(employeesId));
        }else if (type.equals(1)){
            resMap.put("1", this.order1ByEmployees(employeesId));
        }else if (type.equals(2)){
            resMap.put("2", this.order2ByEmployees(employeesId));
        }else if (type.equals(3)){
            resMap.put("3", this.order3ByEmployees(employeesId));
        }else if (type.equals(4)){
            resMap.put("4", this.order4ByEmployees(employeesId));
        }else if (type.equals(5)){
            resMap.put("5", this.order5ByEmployees(employeesId));
        }
        return R.ok(resMap, "获取成功");
    }

    @Override
    public List<OrderDetailsPOJO> order1ByEmployees(Integer employeesId) {
        List<OrderDetailsPOJO> pojoList = new ArrayList<>();
        /* odp 获取订单信息 */
        Set<String> keys = redisTemplate.keys("OrderToBePaid:employeesId"+employeesId+":*");
        Object[] keysArr = keys.toArray();
        for (int i = 0; i < keysArr.length; i++) {
            String key = keysArr[i].toString();
            OrderDetailsPOJO odp = null;
            Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
            try {
                odp = (OrderDetailsPOJO) CommonUtils.mapToObject(map, OrderDetailsPOJO.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            List<OrderPhotoPOJO> orderPhotosPOJOs = (List<OrderPhotoPOJO>) map.get("photos");
            List<WorkDetailsPOJO> workDetailsPOJOs = (List<WorkDetailsPOJO>) map.get("workDetails");
            odp.setPhotos(orderPhotosPOJOs);
            odp.setWorkDetails(workDetailsPOJOs);
            pojoList.add(odp);
        }
        //TODO 排序

        return pojoList;
    }

    @Override
    public List<OrderDetailsPOJO> order2ByEmployees(Integer employeesId) {
        List<OrderDetailsPOJO> pojoList = new ArrayList<>();
        QueryWrapper qw = new QueryWrapper();
        qw.eq("employees_id", employeesId);
        qw.eq("order_status", CommonConstants.ORDER_STATE_TO_BE_SERVED);
        List<OrderDetails> ods = orderDetailsService.list(qw);
        ods.stream().map(od -> {
            QueryWrapper qw2 = new QueryWrapper();
            qw2.eq("number", od.getNumber());
            List<WorkDetails> wds = workDetailsService.list(qw2);
            List<OrderPhotos> ops = orderDetailsService.list(qw2);
            OrderDetailsPOJO odp = this.odp(od, wds, ops);
            return odp;
        }).collect(Collectors.toList());
        return pojoList;
    }

    @Override
    public List<OrderDetailsPOJO> order3ByEmployees(Integer employeesId) {
        List<OrderDetailsPOJO> pojoList = new ArrayList<>();
        QueryWrapper qw = new QueryWrapper();
        qw.eq("employees_id", employeesId);
        qw.eq("order_status", CommonConstants.ORDER_STATE_HAVE_IN_HAND);
        List<OrderDetails> ods = orderDetailsService.list(qw);
        ods.stream().map(od -> {
            QueryWrapper qw2 = new QueryWrapper();
            qw2.eq("number", od.getNumber());
            List<WorkDetails> wds = workDetailsService.list(qw2);
            List<OrderPhotos> ops = orderDetailsService.list(qw2);
            OrderDetailsPOJO odp = this.odp(od, wds, ops);
            return odp;
        }).collect(Collectors.toList());
        return pojoList;
    }

    @Override
    public List<OrderDetailsPOJO> order4ByEmployees(Integer employeesId) {
        List<OrderDetailsPOJO> pojoList = new ArrayList<>();
        QueryWrapper qw = new QueryWrapper();
        qw.eq("employees_id", employeesId);
        qw.eq("order_status", CommonConstants.ORDER_STATE_TO_BE_EVALUATED);
        List<OrderDetails> ods = orderDetailsService.list(qw);
        ods.stream().map(od -> {
            QueryWrapper qw2 = new QueryWrapper();
            qw2.eq("number", od.getNumber());
            List<WorkDetails> wds = workDetailsService.list(qw2);
            List<OrderPhotos> ops = orderDetailsService.list(qw2);
            OrderDetailsPOJO odp = this.odp(od, wds, ops);
            return odp;
        }).collect(Collectors.toList());
        return pojoList;
    }

    @Override
    public List<OrderDetailsPOJO> order5ByEmployees(Integer employeesId) {
        List<OrderDetailsPOJO> pojoList = new ArrayList<>();
        QueryWrapper qw = new QueryWrapper();
        qw.eq("employees_id", employeesId);
        qw.eq("order_status", CommonConstants.ORDER_STATE_COMPLETED);
        List<OrderDetails> ods = orderDetailsService.list(qw);
        ods.stream().map(od -> {
            QueryWrapper qw2 = new QueryWrapper();
            qw2.eq("number", od.getNumber());
            List<WorkDetails> wds = workDetailsService.list(qw2);
            List<OrderPhotos> ops = orderDetailsService.list(qw2);
            OrderDetailsPOJO odp = this.odp(od, wds, ops);
            return odp;
        }).collect(Collectors.toList());
        return pojoList;
    }

    @Override
    public List<OrderDetailsPOJO> order1ByCustomer(Integer customerId) {
        List<OrderDetailsPOJO> pojoList = new ArrayList<>();
        /* odp 获取订单信息 */
        Set<String> keys = redisTemplate.keys("OrderToBePaid:employeesId*");
        Object[] keysArr = keys.toArray();
        for (int i = 0; i < keysArr.length; i++) {
            String key = keysArr[i].toString();
            OrderDetailsPOJO odp = null;
            Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
            try {
                odp = (OrderDetailsPOJO) CommonUtils.mapToObject(map, OrderDetailsPOJO.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (customerId.equals(odp.getCustomerId())) {
                List<OrderPhotoPOJO> orderPhotosPOJOs = (List<OrderPhotoPOJO>) map.get("photos");
                List<WorkDetailsPOJO> workDetailsPOJOs = (List<WorkDetailsPOJO>) map.get("workDetails");
                odp.setPhotos(orderPhotosPOJOs);
                odp.setWorkDetails(workDetailsPOJOs);
                pojoList.add(odp);//属于客户的，才能被返回
            }
        }
        //TODO 排序

        return pojoList;
    }

    @Override
    public List<OrderDetailsPOJO> order2ByCustomer(Integer customerId) {
        List<OrderDetailsPOJO> pojoList = new ArrayList<>();
        QueryWrapper qw = new QueryWrapper();
        qw.eq("customer_id", customerId);
        qw.eq("order_status", CommonConstants.ORDER_STATE_TO_BE_SERVED);
        List<OrderDetails> ods = orderDetailsService.list(qw);
        ods.stream().map(od -> {
            QueryWrapper qw2 = new QueryWrapper();
            qw2.eq("number", od.getNumber());
            List<WorkDetails> wds = workDetailsService.list(qw2);
            List<OrderPhotos> ops = orderDetailsService.list(qw2);
            OrderDetailsPOJO odp = this.odp(od, wds, ops);
            return odp;
        }).collect(Collectors.toList());
        return pojoList;
    }

    @Override
    public List<OrderDetailsPOJO> order3ByCustomer(Integer customerId) {
        List<OrderDetailsPOJO> pojoList = new ArrayList<>();
        QueryWrapper qw = new QueryWrapper();
        qw.eq("customer_id", customerId);
        qw.eq("order_status", CommonConstants.ORDER_STATE_HAVE_IN_HAND);
        List<OrderDetails> ods = orderDetailsService.list(qw);
        ods.stream().map(od -> {
            QueryWrapper qw2 = new QueryWrapper();
            qw2.eq("number", od.getNumber());
            List<WorkDetails> wds = workDetailsService.list(qw2);
            List<OrderPhotos> ops = orderDetailsService.list(qw2);
            OrderDetailsPOJO odp = this.odp(od, wds, ops);
            return odp;
        }).collect(Collectors.toList());
        return pojoList;
    }

    @Override
    public List<OrderDetailsPOJO> order4ByCustomer(Integer customerId) {
        List<OrderDetailsPOJO> pojoList = new ArrayList<>();
        QueryWrapper qw = new QueryWrapper();
        qw.eq("customer_id", customerId);
        qw.eq("order_status", CommonConstants.ORDER_STATE_TO_BE_EVALUATED);
        List<OrderDetails> ods = orderDetailsService.list(qw);
        ods.stream().map(od -> {
            QueryWrapper qw2 = new QueryWrapper();
            qw2.eq("number", od.getNumber());
            List<WorkDetails> wds = workDetailsService.list(qw2);
            List<OrderPhotos> ops = orderDetailsService.list(qw2);
            OrderDetailsPOJO odp = this.odp(od, wds, ops);
            return odp;
        }).collect(Collectors.toList());
        return pojoList;
    }

    @Override
    public List<OrderDetailsPOJO> order5ByCustomer(Integer customerId) {
        List<OrderDetailsPOJO> pojoList = new ArrayList<>();
        QueryWrapper qw = new QueryWrapper();
        qw.eq("customer_id", customerId);
        qw.eq("order_status", CommonConstants.ORDER_STATE_COMPLETED);
        List<OrderDetails> ods = orderDetailsService.list(qw);
        ods.stream().map(od -> {
            QueryWrapper qw2 = new QueryWrapper();
            qw2.eq("number", od.getNumber());
            List<WorkDetails> wds = workDetailsService.list(qw2);
            List<OrderPhotos> ops = orderDetailsService.list(qw2);
            OrderDetailsPOJO odp = this.odp(od, wds, ops);
            return odp;
        }).collect(Collectors.toList());
        return pojoList;
    }

    @Override
    public R payment1() {
        return null;
    }

    @Override
    public R payment2(String number) {
        /* odp 获取订单信息 */
        Set<String> keys = redisTemplate.keys("OrderToBePaid:employeesId*:" + number);
        String[] keysArr = (String[]) keys.toArray();
        String key = keysArr[0];
        OrderDetailsPOJO odp = null;
        Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
        try {
            odp = (OrderDetailsPOJO) CommonUtils.mapToObject(map, OrderDetailsPOJO.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /* 验证是否是自己能操作的订单，保洁员和客户只能作废属于自己的订单 */
        if (!this.orderVerification(odp)) return R.failed(null, "訂單不存在（這可能不是你的訂單）");
        /* 检测当前订单状态是否正确 */
        if (!odp.getOrderState().equals(CommonConstants.ORDER_STATE_PAYMENT_PROCESSING)) return R.failed(null, "订单状态不是处理中");

        redisTemplate.opsForHash().put(key, "orderStatus", CommonConstants.ORDER_STATE_HAVE_IN_HAND);

        return R.ok(null, "已将订单状态由支付处理中变更为未支付状态");
    }

    @Override
    public R payment3(String number) {
        OrderDetails od = this.getById(number);
        if (CommonUtils.isEmpty(od)) return R.failed(null, "订单不存在");
        /* 获取调用者信息 */
        Integer userId = TokenUtils.getCurrentUserId();
        Integer employeesId = employeesDetailsService.getEmployeesIdByUserId(userId);
        /* 检查订单是不是你的 */
        if (!employeesId.equals(od.getEmployeesId())) return R.failed(null, "这不是你的订单");
        /* 检查订单初试状态 */
        if (!od.getOrderState().equals(CommonConstants.ORDER_STATE_HAVE_IN_HAND)) return R.failed(null, "订单不是进行中的状态，无法变更为待评价状态");
        /* 开始修改数据 修改订单状态和完成时间 */
        LocalDateTime now = LocalDateTime.now();
        baseMapper.statusAndTime(Long.valueOf(number), CommonConstants.ORDER_STATE_TO_BE_EVALUATED, now);
        return R.ok(null, "成功将进行中订单转变为待评价状态");
    }

    @Override
    public R payment4() {
        return null;
    }

    @Override
    public R payment5() {
        return null;
    }

    @Override
    public Boolean orderVerification(OrderDetailsPOJO odp) {
        /* 获取调用者信息 */
        Integer userId = TokenUtils.getCurrentUserId();
        String role = TokenUtils.getRoleType();
        QueryWrapper qw = new QueryWrapper();
        qw.eq("user_id", userId);
        if (role.equals(CommonConstants.REQUEST_ORIGIN_EMPLOYEES)){
            EmployeesDetails ed = employeesDetailsService.getOne(qw);
            Integer employeesId = ed.getId();
            if (employeesId.equals(odp.getEmployeesId())) return true;
            return false;
        }
        if (role.equals(CommonConstants.REQUEST_ORIGIN_CUSTOMER)){
            CustomerDetails cd = customerDetailsService.getOne(qw);
            Integer customerId = cd.getId();
            if (customerId.equals(odp.getCustomerId())) return true;
            return false;
        }
        //走到这儿说明调用者出了问题
        return false;
    }

    @Override
    public OrderDetailsPOJO odp(String number) {
        OrderDetails od = orderDetailsService.getById(Long.valueOf(number));
        QueryWrapper qw = new QueryWrapper();
        qw.eq("number", number);
        List<WorkDetails> wds = workDetailsService.list(qw);
        List<OrderPhotos> ops = orderPhotosService.list(qw);
        OrderDetailsPOJO odp = odp(od, wds, ops);
        return odp;
    }

    /* 转换到数据库存储 */
    private List<OrderPhotos> orderPhotos(List<OrderPhotoPOJO> photos, String number){
        if (CommonUtils.isEmpty(photos)) return new ArrayList<>();
        List<OrderPhotos> ops = photos.stream().map(x -> {
            return new OrderPhotos(null, Long.valueOf(number), x.getPhotoUrl(), x.getEvaluate());
        }).collect(Collectors.toList());
        return ops;
    }

    /* 转换到数据库存储 */
    private List<WorkDetails> workDetails(List<WorkDetailsPOJO> workDetails, String number){
        if (CommonUtils.isEmpty(workDetails)) return new ArrayList<>();
        List<WorkDetails> wds = workDetails.stream().map(x -> {
            if (x.getCanBeOnDuty()) {
                StringBuilder sb = new StringBuilder();
                x.getTimeSlots().forEach(timeSlot -> {
                    String s = timeSlot.getTimeSlotStart()+"+"+timeSlot.getTimeSlotLength().toString()+" ";
                    sb.append(s);
                });
                return new WorkDetails(null, Long.valueOf(number), x.getDate(), x.getWeek(), sb.toString().trim(), x.getCanBeOnDuty(), x.getTodayPrice());
            }
            return null;
        }).collect(Collectors.toList());
        return wds;
    }

    /*转到pojo实际的应用*/
    private List<WorkDetailsPOJO> workDetailsPOJOs(List<WorkDetails> wds){
        List<WorkDetailsPOJO> workDetailsPOJOs = wds.stream().map(wd -> {
            String[] times = wd.getTimeSlots().split(" ");
            List<TimeSlot> timeSlots = new ArrayList<>();
            for (int i = 0; i < times.length; i++) {
                TimeSlot ts = ts(times[i]);
                timeSlots.add(ts);
            }
            return new WorkDetailsPOJO(wd.getDate(), wd.getWeek(), timeSlots, wd.getCanBeOnDuty(), wd.getTodayPrice());
        }).collect(Collectors.toList());
        return workDetailsPOJOs;
    }

    private List<OrderPhotoPOJO> orderPhotosPOJOs(List<OrderPhotos> ops){
        List<OrderPhotoPOJO> orderPhotosPOJOs = ops.stream().map(wd -> {
            return new OrderPhotoPOJO(wd.getPhotoUrl(), wd.getEvaluate());
        }).collect(Collectors.toList());
        return orderPhotosPOJOs;
    }

    private OrderDetailsPOJO odp(OrderDetails od, List<WorkDetails> wds, List<OrderPhotos> ops){
        OrderDetailsPOJO odp = new OrderDetailsPOJO(od);
        List<WorkDetailsPOJO> workDetailsPOJOs = workDetailsPOJOs(wds);
        List<OrderPhotoPOJO> orderPhotosPOJOs = orderPhotosPOJOs(ops);
        odp.setWorkDetails(workDetailsPOJOs);
        odp.setPhotos(orderPhotosPOJOs);
        return odp;
    }

    private TimeSlot ts(String s){
        String[] strings = s.split("\\+");
        String s1 = strings[0];
        String s2 = strings[1];
        LocalTime time = LocalTime.of(Integer.valueOf(s1.substring(0,2)), Integer.valueOf(s1.substring(3,5)));
        Float length = Float.valueOf(s2);
        TimeSlot ts = new TimeSlot(time, length);
        return ts;
    }

    @Test
    public void k(){
        String s = "09:00+3.5";
        System.out.println(ts(s));
    }

}
