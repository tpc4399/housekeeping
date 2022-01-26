package com.housekeeping.admin.service.impl;

import com.aliyun.oss.OSSClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.*;
import com.housekeeping.admin.entity.*;
import com.housekeeping.admin.mapper.CompanyDetailsMapper;
import com.housekeeping.admin.mapper.OrderDetailsMapper;
import com.housekeeping.admin.mapper.PaymentCallbackMapper;
import com.housekeeping.admin.mapper.WorkDetailsMapper;
import com.housekeeping.admin.pojo.*;
import com.housekeeping.admin.service.*;
import com.housekeeping.admin.vo.OrderPhotoVO;
import com.housekeeping.admin.vo.TimeSlot;
import com.housekeeping.admin.vo.WorkClockVO;
import com.housekeeping.admin.vo.WorkTimeTableDateVO;
import com.housekeeping.common.entity.Message;
import com.housekeeping.common.utils.*;
import ecpay.payment.integration.AllInOne;
import ecpay.payment.integration.domain.AioCheckOutOneTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
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
    @Resource
    private PaymentCallbackMapper paymentCallbackMapper;
    @Resource
    private ISysJobContendService sysJobContendService;
    @Resource
    private ICompanyDetailsService companyDetailsService;
    @Resource
    private ICardPayCallbackService cardPayCallbackService;
    @Resource
    private IGroupEmployeesService groupEmployeesService;
    @Resource
    private DelayingQueueService delayingQueueService;
    @Resource
    private ISysConfigService sysConfigService;
    @Resource
    private IOrderEvaluationService orderEvaluationService;
    @Resource
    private WorkClockService workClockService;
    @Resource
    private ISysJobNoteService sysJobNoteService;
    @Resource
    private WorkDetailsMapper workDetailsMapper;
    @Resource
    private IOrderRefundService orderRefundService;
    @Resource
    private TokenOrderService tokenOrderService;
    @Resource
    private CompanyDetailsMapper companyDetailsMapper;
    @Resource
    private ISysConfigService configService;

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
            Object[] keysArr = keys.toArray();
            String key = keysArr[0].toString();
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
                OrderPhotoPOJO pojo = new OrderPhotoPOJO(null, urlPrefix + fileAbstractPath, evaluates[count.get()]);
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
        redisTemplate.opsForHash().put(key, "orderState", CommonConstants.ORDER_STATE_PAYMENT_PROCESSING);


        return R.ok(null, "修改成功");
    }

    @Override
    public void paymentCallback(PaymentCallback pc){
        System.out.println("PaymentCallback:" + LocalDateTime.now() + "  " + pc.toString());
        this.savePCInfo(pc); //存储回调信息
        this.inputSql(pc.getDataId(), true); //将处理中订单，转变为已支付订单
    }

    @Override
    public void savePCInfo(PaymentCallback pc) {
        paymentCallbackMapper.savePCInfo(pc);
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
        Set<String> keys2 = redisTemplate.keys("OrderToBePaid:companyId*:" + number);
        if (keys.isEmpty()&&keys2.isEmpty()){
            return R.failed(null, "訂單編號不存在于redis");
        }
        if(!keys.isEmpty()&& keys2.isEmpty()){
            Object[] keysArr = keys.toArray();
            String key = keysArr[0].toString();
            OrderDetailsPOJO odp = null;
            Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
            try {
                odp = (OrderDetailsPOJO) CommonUtils.mapToObject(map, OrderDetailsPOJO.class);
            } catch (Exception e) {
                e.printStackTrace();
            }

            /* 验证是否是自己能操作的订单，保洁员和客户只能作废属于自己的订单 */
            //if (!this.orderVerification(odp)) return R.failed(null, "訂單不存在（這可能不是你的訂單）");

            OrderDetails od = new OrderDetails(odp);

            //佣金
            QueryWrapper qw = new QueryWrapper();
            qw.eq("config_key", "bonus");
            SysConfig one = configService.getOne(qw);
            od.setBonus(new BigDecimal(one.getConfigValue()));

            if (status) od.setOrderState(CommonConstants.ORDER_STATE_TO_BE_SERVED);//订单已支付，待服务
            else od.setOrderState(CommonConstants.ORDER_STATE_VOID);//取消订单

            List<OrderPhotos> ops = new ArrayList<>();
            List<WorkDetails> wds = new ArrayList<>();
            Object photoObj = map.get("photos");
            Object workDetailsObj = map.get("workDetails");
            if (!photoObj.equals("")) ops = orderPhotos((List<OrderPhotoPOJO>) photoObj, number);
            if (!workDetailsObj.equals("")) wds = workDetails2((List<WorkDetailsPOJO>) workDetailsObj, number);

            synchronized (this){
                orderDetailsService.save(od);
                if (CollectionUtils.isNotEmpty(ops)) {
                    System.out.println();
                    orderPhotosService.saveBatch(ops);
                }

                for (int i = 0; i < wds.size(); i++) {
                    synchronized (this){
                        workDetailsService.add(wds.get(i));
                    }
                    WorkDetails workDetails = (WorkDetails) CommonUtils.getMaxId("work_details", workDetailsService);
                    WorkClock workClock = new WorkClock();
                    workClock.setWorkId(workDetails.getId());
                    workClockService.save(workClock);
                }

                redisTemplate.delete(key);
            }
            return R.ok(number, "操作成功");
        }
        else {
            Object[] keysArr = keys2.toArray();
            String key = keysArr[0].toString();
            TokenOrderParent odp = null;
            Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
            try {
                odp = (TokenOrderParent) CommonUtils.mapToObject(map, TokenOrderParent.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //公司增加代幣
            CompanyDetails one = companyDetailsService.getById(odp.getCompanyId());
            companyDetailsMapper.buyTokens(one.getTokens(),one.getId(),odp.getTokens());
            //保存訂單信息
            TokenOrder tokenOrder = new TokenOrder(odp);
            tokenOrder.setOrderState(CommonConstants.ORDER_STATE_COMPLETED);
            tokenOrderService.save(tokenOrder);
            //刪除緩存
            redisTemplate.delete(key);
            return R.ok(number, "操作成功");
        }
    }

    @Override
    public R queryByCus(Integer type) {
        /* type = 0全部 1待付款 2待服务 3进行中 4待评价 5已完成 */
        CustomerDetails cd = customerDetailsService.getByUserId(TokenUtils.getCurrentUserId());
        Integer customerId = cd.getId();

        List<OrderDetailsPOJO> res = new ArrayList<>();
        if (type.equals(0)){
            res.addAll(this.order1ByCustomer(customerId));
            res.addAll(this.order2ByCustomer(customerId));
            res.addAll(this.order3ByCustomer(customerId));
            res.addAll(this.order4ByCustomer(customerId));
            res.addAll(this.order5ByCustomer(customerId));
        }else if (type.equals(1)){
            res.addAll(this.order1ByCustomer(customerId));
        }else if (type.equals(2)){
            res.addAll(this.order2ByCustomer(customerId));
        }else if (type.equals(3)){
            res.addAll(this.order3ByCustomer(customerId));
        }else if (type.equals(4)){
            res.addAll(this.order4ByCustomer(customerId));
        }else if (type.equals(5)){
            res.addAll(this.order5ByCustomer(customerId));
        }
        SortListUtil<OrderDetailsPOJO> sort = new SortListUtil<>();
        sort.Sort(res,"getStartDateTime","desc");
        List<OrderDetailsParent> sons = res.stream().map(x -> {
            /* 工作内容二次加工处理 */
            OrderDetailsParent son = x;
            List<Integer> jobIds = CommonUtils.stringToList(x.getJobIds());
            List<SysJobContend> jobs = new ArrayList<>();
            if (!jobIds.isEmpty()) jobs = sysJobContendService.listByIds(jobIds);
            son.setJobs(jobs);
            /* 保洁员头像二次加工处理 */
            EmployeesDetails byId = employeesDetailsService.getById(x.getEmployeesId());
            son.setEmployeesHeadUrl(byId==null?"":byId.getHeadUrl());
            /* 第一次工作内容 */
            List<WorkDetailsPOJO> wdp = x.getWorkDetails();
            if (wdp.isEmpty()){
                son.setWdp(new WorkDetailsPOJO());
            }else {
                son.setWdp(x.getWorkDetails().get(0));
            }
            /* 保洁员和客户是否已评价 */
            Boolean yes1 = orderEvaluationService.getEvaluationStatusOfCustomer(x.getNumber());
            Boolean yes2 = orderEvaluationService.getEvaluationStatusOfEmployees(x.getNumber());
            son.setYes1(yes1);
            son.setYes2(yes2);
            return son;
        }).collect(Collectors.toList());
        return R.ok(sons, "获取成功");
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
        List<OrderDetailsPOJO> res = new ArrayList<>();
        if (type.equals(0)){
            res.addAll(this.order1ByEmployees(employeesId));
            res.addAll(this.order2ByEmployees(employeesId));
            res.addAll(this.order3ByEmployees(employeesId));
            res.addAll(this.order4ByEmployees(employeesId));
            res.addAll(this.order5ByEmployees(employeesId));
        }else if (type.equals(1)){
            res.addAll(this.order1ByEmployees(employeesId));
        }else if (type.equals(2)){
            res.addAll(this.order2ByEmployees(employeesId));
        }else if (type.equals(3)){
            res.addAll(this.order3ByEmployees(employeesId));
        }else if (type.equals(4)){
            res.addAll(this.order4ByEmployees(employeesId));
        }else if (type.equals(5)){
            res.addAll(this.order5ByEmployees(employeesId));
        }
        SortListUtil<OrderDetailsPOJO> sort = new SortListUtil<>();
        sort.Sort(res,"getStartDateTime","desc");
        List<OrderDetailsParent> sons = res.stream().map(x -> {
            /* 工作内容二次加工处理 */
            OrderDetailsParent son = x;
            List<Integer> jobIds = CommonUtils.stringToList(x.getJobIds());
            List<SysJobContend> jobs = new ArrayList<>();
            if (!jobIds.isEmpty()) jobs = sysJobContendService.listByIds(jobIds);
            son.setJobs(jobs);
            /* 客户头像二次加工处理 */
            son.setCustomerHeadUrl(customerDetailsService.getById(x.getCustomerId()).getHeadUrl());
            son.setCustomerHeadUrl(customerDetailsService.getById(x.getCustomerId()).getHeadUrl());
            /* 第一次工作内容 */
            List<WorkDetailsPOJO> wdp = x.getWorkDetails();
            if (wdp.isEmpty()){
                son.setWdp(new WorkDetailsPOJO());
            }else {
                son.setWdp(x.getWorkDetails().get(0));
            }
            /* 保洁员和客户是否已评价 */
            Boolean yes1 = orderEvaluationService.getEvaluationStatusOfCustomer(x.getNumber());
            Boolean yes2 = orderEvaluationService.getEvaluationStatusOfEmployees(x.getNumber());
            son.setYes1(yes1);
            son.setYes2(yes2);
            return son;
        }).collect(Collectors.toList());
        return R.ok(sons, "获取成功");
    }

    @Override
    public R queryByCom(Integer type) {
        /* type = 0全部 1待付款 2待服务 3进行中 4待评价 5已完成 */
        Integer companyId = companyDetailsService.getCompanyIdByUserId(TokenUtils.getCurrentUserId());
        QueryWrapper qw = new QueryWrapper();
        qw.select("id");
        qw.eq("company_id", companyId);
        List<Integer> empIds = employeesDetailsService.listObjs(qw);

        List<OrderDetailsPOJO> res = new ArrayList<>();
        if (type.equals(0)){
            res.addAll(this.order1ByEmployeesAll(empIds));
            res.addAll(this.order2ByEmployeesAll(empIds));
            res.addAll(this.order3ByEmployeesAll(empIds));
            res.addAll(this.order4ByEmployeesAll(empIds));
            res.addAll(this.order5ByEmployeesAll(empIds));
        }else if (type.equals(1)){
            res.addAll(this.order1ByEmployeesAll(empIds));
        }else if (type.equals(2)){
            res.addAll(this.order2ByEmployeesAll(empIds));
        }else if (type.equals(3)){
            res.addAll(this.order3ByEmployeesAll(empIds));
        }else if (type.equals(4)){
            res.addAll(this.order4ByEmployeesAll(empIds));
        }else if (type.equals(5)){
            res.addAll(this.order5ByEmployeesAll(empIds));
        }
        SortListUtil<OrderDetailsPOJO> sort = new SortListUtil<>();
        sort.Sort(res,"getStartDateTime","desc");
        List<OrderDetailsParent> sons = res.stream().map(x -> {
            /* 工作内容二次加工处理 */
            OrderDetailsParent son = x;
            List<Integer> jobIds = CommonUtils.stringToList(x.getJobIds());
            List<SysJobContend> jobs = new ArrayList<>();
            if (!jobIds.isEmpty()) jobs = sysJobContendService.listByIds(jobIds);
            son.setJobs(jobs);
            /* 保洁员头像二次加工处理 */
            son.setEmployeesHeadUrl(employeesDetailsService.getById(x.getEmployeesId()).getHeadUrl());
            /* 第一次工作内容 */
            List<WorkDetailsPOJO> wdp = x.getWorkDetails();
            if (wdp.isEmpty()){
                son.setWdp(new WorkDetailsPOJO());
            }else {
                son.setWdp(x.getWorkDetails().get(0));
            }
            /* 保洁员和客户是否已评价 */
            Boolean yes1 = orderEvaluationService.getEvaluationStatusOfCustomer(x.getNumber());
            Boolean yes2 = orderEvaluationService.getEvaluationStatusOfEmployees(x.getNumber());
            son.setYes1(yes1);
            son.setYes2(yes2);
            return son;
        }).collect(Collectors.toList());
        return R.ok(sons, "获取成功");
    }

    @Override
    public R queryEmpByCom(Integer type, Integer employeesId) {
        /* type = 0全部 1待付款 2待服务 3进行中 4待评价 5已完成 */
        List<Integer> empIds = new ArrayList<>();
        empIds.add(employeesId);

        List<OrderDetailsPOJO> res = new ArrayList<>();
        if (type.equals(0)){
            res.addAll(this.order1ByEmployeesAll(empIds));
            res.addAll(this.order2ByEmployeesAll(empIds));
            res.addAll(this.order3ByEmployeesAll(empIds));
            res.addAll(this.order4ByEmployeesAll(empIds));
            res.addAll(this.order5ByEmployeesAll(empIds));
        }else if (type.equals(1)){
            res.addAll(this.order1ByEmployeesAll(empIds));
        }else if (type.equals(2)){
            res.addAll(this.order2ByEmployeesAll(empIds));
        }else if (type.equals(3)){
            res.addAll(this.order3ByEmployeesAll(empIds));
        }else if (type.equals(4)){
            res.addAll(this.order4ByEmployeesAll(empIds));
        }else if (type.equals(5)){
            res.addAll(this.order5ByEmployeesAll(empIds));
        }
        SortListUtil<OrderDetailsPOJO> sort = new SortListUtil<>();
        sort.Sort(res,"getStartDateTime","desc");
        List<OrderDetailsParent> sons = res.stream().map(x -> {
            /* 工作内容二次加工处理 */
            OrderDetailsParent son = x;
            List<Integer> jobIds = CommonUtils.stringToList(x.getJobIds());
            List<SysJobContend> jobs = new ArrayList<>();
            if (!jobIds.isEmpty()) jobs = sysJobContendService.listByIds(jobIds);
            son.setJobs(jobs);
            /* 保洁员头像二次加工处理 */
            son.setEmployeesHeadUrl(employeesDetailsService.getById(x.getEmployeesId()).getHeadUrl());
            son.setCustomerHeadUrl(customerDetailsService.getById(x.getCustomerId()).getHeadUrl());
            /* 第一次工作内容 */
            List<WorkDetailsPOJO> wdp = x.getWorkDetails();
            if (wdp.isEmpty()){
                son.setWdp(new WorkDetailsPOJO());
            }else {
                son.setWdp(x.getWorkDetails().get(0));
            }
            /* 保洁员和客户是否已评价 */
            Boolean yes1 = orderEvaluationService.getEvaluationStatusOfCustomer(x.getNumber());
            Boolean yes2 = orderEvaluationService.getEvaluationStatusOfEmployees(x.getNumber());
            son.setYes1(yes1);
            son.setYes2(yes2);
            return son;
        }).collect(Collectors.toList());
        return R.ok(sons, "获取成功");
    }

    @Override
    public R queryByManager(Integer type) {
        /* 獲取經理旗下保潔員的Ids */
        List<Integer> empIds = groupEmployeesService.getEmployeesIdsByManager();

        List<OrderDetailsPOJO> res = new ArrayList<>();
        if (type.equals(0)){
            res.addAll(this.order1ByEmployeesAll(empIds));
            res.addAll(this.order2ByEmployeesAll(empIds));
            res.addAll(this.order3ByEmployeesAll(empIds));
            res.addAll(this.order4ByEmployeesAll(empIds));
            res.addAll(this.order5ByEmployeesAll(empIds));
        }else if (type.equals(1)){
            res.addAll(this.order1ByEmployeesAll(empIds));
        }else if (type.equals(2)){
            res.addAll(this.order2ByEmployeesAll(empIds));
        }else if (type.equals(3)){
            res.addAll(this.order3ByEmployeesAll(empIds));
        }else if (type.equals(4)){
            res.addAll(this.order4ByEmployeesAll(empIds));
        }else if (type.equals(5)){
            res.addAll(this.order5ByEmployeesAll(empIds));
        }
        SortListUtil<OrderDetailsPOJO> sort = new SortListUtil<>();
        sort.Sort(res,"getStartDateTime","desc");
        List<OrderDetailsParent> sons = res.stream().map(x -> {
            /* 工作内容二次加工处理 */
            OrderDetailsParent son = x;
            List<Integer> jobIds = CommonUtils.stringToList(x.getJobIds());
            List<SysJobContend> jobs = new ArrayList<>();
            if (!jobIds.isEmpty()) jobs = sysJobContendService.listByIds(jobIds);
            son.setJobs(jobs);
            /* 保洁员头像二次加工处理 */
            son.setEmployeesHeadUrl(employeesDetailsService.getById(x.getEmployeesId()).getHeadUrl());
            son.setCustomerHeadUrl(customerDetailsService.getById(x.getCustomerId()).getHeadUrl());
            /* 第一次工作内容 */
            List<WorkDetailsPOJO> wdp = x.getWorkDetails();
            if (wdp.isEmpty()){
                son.setWdp(new WorkDetailsPOJO());
            }else {
                son.setWdp(x.getWorkDetails().get(0));
            }
            /* 保洁员和客户是否已评价 */
            Boolean yes1 = orderEvaluationService.getEvaluationStatusOfCustomer(x.getNumber());
            Boolean yes2 = orderEvaluationService.getEvaluationStatusOfEmployees(x.getNumber());
            son.setYes1(yes1);
            son.setYes2(yes2);
            return son;
        }).collect(Collectors.toList());
        return R.ok(sons, "获取成功");
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

            List<OrderPhotoPOJO> ops = new ArrayList<>();
            List<WorkDetailsPOJO> wds = new ArrayList<>();
            Object photoObj = map.get("photos");
            Object workDetailsObj = map.get("workDetails");
            if (!photoObj.equals("")) ops = (List<OrderPhotoPOJO>) map.get("photos");;
            if (!workDetailsObj.equals("")) wds = (List<WorkDetailsPOJO>) map.get("workDetails");
            odp.setPhotos(ops);
            odp.setWorkDetails(wds);
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
        qw.eq("order_state", CommonConstants.ORDER_STATE_TO_BE_SERVED);
        List<OrderDetails> ods = orderDetailsService.list(qw);
        pojoList = ods.stream().map(od -> {
            QueryWrapper qw2 = new QueryWrapper();
            qw2.eq("number", od.getNumber());
            List<WorkDetails> wds = workDetailsService.list(qw2);
            List<OrderPhotos> ops = orderPhotosService.listByNumber(od.getNumber().toString());
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
        qw.eq("order_state", CommonConstants.ORDER_STATE_HAVE_IN_HAND);
        List<OrderDetails> ods = orderDetailsService.list(qw);
        pojoList = ods.stream().map(od -> {
            QueryWrapper qw2 = new QueryWrapper();
            qw2.eq("number", od.getNumber());
            List<WorkDetails> wds = workDetailsService.list(qw2);
            List<OrderPhotos> ops = orderPhotosService.listByNumber(od.getNumber().toString());
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
        qw.eq("order_state", CommonConstants.ORDER_STATE_TO_BE_EVALUATED);
        List<OrderDetails> ods = orderDetailsService.list(qw);
        pojoList = ods.stream().map(od -> {
            QueryWrapper qw2 = new QueryWrapper();
            qw2.eq("number", od.getNumber());
            List<WorkDetails> wds = workDetailsService.list(qw2);
            List<OrderPhotos> ops = orderPhotosService.listByNumber(od.getNumber().toString());
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
        qw.eq("order_state", CommonConstants.ORDER_STATE_COMPLETED);
        List<OrderDetails> ods = orderDetailsService.list(qw);
        pojoList = ods.stream().map(od -> {
            QueryWrapper qw2 = new QueryWrapper();
            qw2.eq("number", od.getNumber());
            List<WorkDetails> wds = workDetailsService.list(qw2);
            List<OrderPhotos> ops = orderPhotosService.listByNumber(od.getNumber().toString());
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
                List<OrderPhotoPOJO> ops = new ArrayList<>();
                List<WorkDetailsPOJO> wds = new ArrayList<>();
                Object photoObj = map.get("photos");
                Object workDetailsObj = map.get("workDetails");
                if (!photoObj.equals("")) ops = (List<OrderPhotoPOJO>) map.get("photos");;
                if (!workDetailsObj.equals("")) wds = (List<WorkDetailsPOJO>) map.get("workDetails");
                odp.setPhotos(ops);
                odp.setWorkDetails(wds);
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
        qw.eq("order_state", CommonConstants.ORDER_STATE_TO_BE_SERVED);
        List<OrderDetails> ods = orderDetailsService.list(qw);
        pojoList = ods.stream().map(od -> {
            QueryWrapper qw2 = new QueryWrapper();
            qw2.eq("number", od.getNumber());
            List<WorkDetails> wds = workDetailsService.list(qw2);
            List<OrderPhotos> ops = orderPhotosService.listByNumber(od.getNumber().toString());
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
        qw.eq("order_state", CommonConstants.ORDER_STATE_HAVE_IN_HAND);
        List<OrderDetails> ods = orderDetailsService.list(qw);
        pojoList = ods.stream().map(od -> {
            QueryWrapper qw2 = new QueryWrapper();
            qw2.eq("number", od.getNumber());
            List<WorkDetails> wds = workDetailsService.list(qw2);
            List<OrderPhotos> ops = orderPhotosService.listByNumber(od.getNumber().toString());
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
        qw.eq("order_state", CommonConstants.ORDER_STATE_TO_BE_EVALUATED);
        List<OrderDetails> ods = orderDetailsService.list(qw);
        pojoList = ods.stream().map(od -> {
            QueryWrapper qw2 = new QueryWrapper();
            qw2.eq("number", od.getNumber());
            List<WorkDetails> wds = workDetailsService.list(qw2);
            List<OrderPhotos> ops = orderPhotosService.listByNumber(od.getNumber().toString());
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
        qw.eq("order_state", CommonConstants.ORDER_STATE_COMPLETED);
        List<OrderDetails> ods = orderDetailsService.list(qw);
        pojoList = ods.stream().map(od -> {
            QueryWrapper qw2 = new QueryWrapper();
            qw2.eq("number", od.getNumber());
            List<WorkDetails> wds = workDetailsService.list(qw2);
            List<OrderPhotos> ops = orderPhotosService.listByNumber(od.getNumber().toString());
            OrderDetailsPOJO odp = this.odp(od, wds, ops);
            return odp;
        }).collect(Collectors.toList());
        return pojoList;
    }

    @Override
    public List<OrderDetailsPOJO> order1ByEmployeesAll(List<Integer> empIds) {
        List<OrderDetailsPOJO> pojoList = new ArrayList<>();
        empIds.forEach(empId -> {
            Set<String> keys = redisTemplate.keys("OrderToBePaid:employeesId"+empId+"*");
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
                List<OrderPhotoPOJO> ops = new ArrayList<>();
                List<WorkDetailsPOJO> wds = new ArrayList<>();
                Object photoObj = map.get("photos");
                Object workDetailsObj = map.get("workDetails");
                if (!photoObj.equals("")) ops = (List<OrderPhotoPOJO>) map.get("photos");;
                if (!workDetailsObj.equals("")) wds = (List<WorkDetailsPOJO>) map.get("workDetails");
                odp.setPhotos(ops);
                odp.setWorkDetails(wds);
                pojoList.add(odp);//
            }
        });
        return pojoList;
    }

    @Override
    public List<OrderDetailsPOJO> order2ByEmployeesAll(List<Integer> empIds) {
        List<OrderDetailsPOJO> pojoList = new ArrayList<>();
        empIds.forEach(empId -> {
            QueryWrapper qw = new QueryWrapper();
            qw.eq("employees_id", empId);
            qw.eq("order_state", CommonConstants.ORDER_STATE_TO_BE_SERVED);
            List<OrderDetails> ods = orderDetailsService.list(qw);
            ods.forEach(od -> {
                QueryWrapper qw2 = new QueryWrapper();
                qw2.eq("number", od.getNumber());
                List<WorkDetails> wds = workDetailsService.list(qw2);
                List<OrderPhotos> ops = orderPhotosService.listByNumber(od.getNumber().toString());
                OrderDetailsPOJO odp = this.odp(od, wds, ops);
                pojoList.add(odp);
            });
        });
        return pojoList;
    }

    @Override
    public List<OrderDetailsPOJO> order3ByEmployeesAll(List<Integer> empIds) {
        List<OrderDetailsPOJO> pojoList = new ArrayList<>();
        empIds.forEach(empId -> {
            QueryWrapper qw = new QueryWrapper();
            qw.eq("employees_id", empId);
            qw.eq("order_state", CommonConstants.ORDER_STATE_HAVE_IN_HAND);
            List<OrderDetails> ods = orderDetailsService.list(qw);
            ods.forEach(od -> {
                QueryWrapper qw2 = new QueryWrapper();
                qw2.eq("number", od.getNumber());
                List<WorkDetails> wds = workDetailsService.list(qw2);
                List<OrderPhotos> ops = orderPhotosService.listByNumber(od.getNumber().toString());
                OrderDetailsPOJO odp = this.odp(od, wds, ops);
                pojoList.add(odp);
            });
        });
        return pojoList;
    }

    @Override
    public List<OrderDetailsPOJO> order4ByEmployeesAll(List<Integer> empIds) {
        List<OrderDetailsPOJO> pojoList = new ArrayList<>();
        empIds.forEach(empId -> {
            QueryWrapper qw = new QueryWrapper();
            qw.eq("employees_id", empId);
            qw.eq("order_state", CommonConstants.ORDER_STATE_TO_BE_EVALUATED);
            List<OrderDetails> ods = orderDetailsService.list(qw);
            ods.forEach(od -> {
                QueryWrapper qw2 = new QueryWrapper();
                qw2.eq("number", od.getNumber());
                List<WorkDetails> wds = workDetailsService.list(qw2);
                List<OrderPhotos> ops = orderPhotosService.listByNumber(od.getNumber().toString());
                OrderDetailsPOJO odp = this.odp(od, wds, ops);
                pojoList.add(odp);
            });
        });
        return pojoList;
    }

    @Override
    public List<OrderDetailsPOJO> order5ByEmployeesAll(List<Integer> empIds) {
        List<OrderDetailsPOJO> pojoList = new ArrayList<>();
        empIds.forEach(empId -> {
            QueryWrapper qw = new QueryWrapper();
            qw.eq("employees_id", empId);
            qw.eq("order_state", CommonConstants.ORDER_STATE_COMPLETED);
            List<OrderDetails> ods = orderDetailsService.list(qw);
            ods.forEach(od -> {
                QueryWrapper qw2 = new QueryWrapper();
                qw2.eq("number", od.getNumber());
                List<WorkDetails> wds = workDetailsService.list(qw2);
                List<OrderPhotos> ops = orderPhotosService.listByNumber(od.getNumber().toString());
                OrderDetailsPOJO odp = this.odp(od, wds, ops);
                pojoList.add(odp);
            });
        });
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
        Object[] keysArr = keys.toArray();
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
        /* 检测当前订单状态是否正确 */
        if (!odp.getOrderState().equals(CommonConstants.ORDER_STATE_PAYMENT_PROCESSING)) return R.failed(null, "订单状态不是处理中");

        redisTemplate.opsForHash().put(key, "orderStatus", CommonConstants.ORDER_STATE_HAVE_IN_HAND);

        return R.ok(null, "已将订单状态由支付处理中变更为未支付状态");
    }

    @Override
    public R payment3(String number) {
        QueryWrapper qw = new QueryWrapper();
        qw.eq("number", number);
        OrderDetails od = this.getOne(qw);
        if (CommonUtils.isEmpty(od)) return R.failed(null, "订单不存在");
        /* 获取调用者信息 *//*
        Integer userId = TokenUtils.getCurrentUserId();
        Integer employeesId = employeesDetailsService.getEmployeesIdByUserId(userId);
        *//* 检查订单是不是你的 *//*
        if (!employeesId.equals(od.getEmployeesId())) return R.failed(null, "这不是你的订单");*/
        /* 检查订单初试状态 */
        if (!od.getOrderState().equals(CommonConstants.ORDER_STATE_HAVE_IN_HAND)) return R.failed(null, "订单不是进行中的状态，无法变更为待评价状态");
        /* 开始修改数据 修改订单状态和完成时间 */
        LocalDateTime now = LocalDateTime.now();
        baseMapper.statusAndTime(number, CommonConstants.ORDER_STATE_TO_BE_EVALUATED, now);
        /* 生成空的评价记录 */
        baseMapper.insertEvaluation(number);
        /* 生成七天自动评价消息 */
        //TODO 生成七天自动评价消息
        String delaySeconds = sysConfigService.getAutomaticEvaluationTime();
        String seqId = UUID.randomUUID().toString();
        String channel1 = CommonConstants.MESSAGE_CHANNEL_CUSTOMER;
        String channel2 = CommonConstants.MESSAGE_CHANNEL_EMPLOYEES;
        String body = number;
        Long delayTime = Long.valueOf(delaySeconds) * 1000 * 60; //延时时间，分钟
        Long nowMilliSecond = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        Long deadLine = nowMilliSecond + delayTime;   //一分钟后的时间戳
        Message message1 = new Message(seqId, channel1, body, deadLine, LocalDateTime.now()); //客户 自动评价订单的延时消息
        Message message2 = new Message(seqId, channel2, body, deadLine, LocalDateTime.now()); //保洁员 自动评价订单的延时消息

        delayingQueueService.push(message1);
        delayingQueueService.push(message2);

        return R.ok(null, "成功将进行中订单转变为待评价状态,成功生成评价记录");
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
    public R payment7(String number) {
        QueryWrapper qw = new QueryWrapper();
        qw.eq("number", number);
        OrderDetails od = this.getOne(qw);
        if (CommonUtils.isEmpty(od)) return R.failed(null, "订单不存在");
        /* 获取调用者信息 *//*
        Integer userId = TokenUtils.getCurrentUserId();
        Integer employeesId = employeesDetailsService.getEmployeesIdByUserId(userId);
        *//* 检查订单是不是你的 *//*
        if (!employeesId.equals(od.getEmployeesId())) return R.failed(null, "这不是你的订单");*/
        /* 检查订单初试状态 */
        if (!od.getOrderState().equals(CommonConstants.ORDER_STATE_TO_BE_SERVED)) return R.failed(null, "订单不是待服務的状态，无法变更为進行中状态");
        /* 开始修改数据 修改订单状态 */
        baseMapper.status(number, CommonConstants.ORDER_STATE_HAVE_IN_HAND);
        return R.ok(null, "成功将待服务订单转变为进行中状态");
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
        if (role.equals(CommonConstants.REQUEST_ORIGIN_ADMIN)) return true;
        //走到这儿说明调用者出了问题
        return false;
    }

    @Override
    public OrderDetailsPOJO odp(String number) {
        OrderDetails od = orderDetailsService.getById(Long.valueOf(number));
        QueryWrapper qw = new QueryWrapper();
        qw.eq("number", number);
        List<WorkDetails> wds = workDetailsService.list(qw);
        List<OrderPhotos> ops = orderPhotosService.listByNumber(od.getNumber().toString());
        OrderDetailsPOJO odp = odp(od, wds, ops);
        return odp;
    }

    @Override
    public Integer getState(String number) {
        /* 查redis */
        Set<String> keys = redisTemplate.keys("OrderToBePaid:employeesId*:" + number);
        if (!keys.isEmpty()) {
            Object[] keysArr = (Object[]) keys.toArray();
            String key = keysArr[0].toString();
            OrderDetailsPOJO odp = null;
            Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
            try {
                odp = (OrderDetailsPOJO) CommonUtils.mapToObject(map, OrderDetailsPOJO.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return odp.getOrderState();
        }

        /* 查數據庫 */
        QueryWrapper qw = new QueryWrapper();
        qw.eq("number", number);
        OrderDetails od = orderDetailsService.getOne(qw);
        if (CommonUtils.isNotEmpty(od)) return od.getOrderState();

        return -1;
    }

    @Override
    public R getOrder(String number) {
        /* 查redis */
        Set<String> keys = redisTemplate.keys("OrderToBePaid:employeesId*:" + number);
        if (!keys.isEmpty()) {
            Object[] keysArr = (Object[]) keys.toArray();
            String key = keysArr[0].toString();
            OrderDetailsPOJO odp = null;
            Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
            try {
                odp = (OrderDetailsPOJO) CommonUtils.mapToObject(map, OrderDetailsPOJO.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            List<OrderPhotoPOJO> ops = new ArrayList<>();
            List<WorkDetailsPOJO> wds = new ArrayList<>();
            Object photoObj = map.get("photos");
            Object workDetailsObj = map.get("workDetails");
            if (!photoObj.equals("")) ops = (List<OrderPhotoPOJO>) map.get("photos");;
            if (!workDetailsObj.equals("")) wds = (List<WorkDetailsPOJO>) map.get("workDetails");
            odp.setPhotos(ops);
            odp.setWorkDetails(wds);

            OrderDetailsParent parent = odp;
            List<Integer> jobIds = CommonUtils.stringToList(odp.getJobIds());
            List<SysJobContend> jobs = sysJobContendService.listByIds(jobIds);
            parent.setJobs(jobs);

            List<Integer> noteIds = CommonUtils.stringToList(odp.getNoteIds());
            if(CollectionUtils.isNotEmpty(noteIds)){
                List<SysJobNote> sysJobNotes = sysJobNoteService.listByIds(noteIds);
                parent.setNotes(sysJobNotes);
            }

            EmployeesDetails ed = employeesDetailsService.getById(odp.getEmployeesId());
            CustomerDetails cd = customerDetailsService.getById(odp.getCustomerId());
            if (CommonUtils.isNotEmpty(ed)){
                /* 保洁员头像二次加工处理 */
                parent.setEmployeesHeadUrl(ed.getHeadUrl());
                /* 保洁员地址二次加工 */
                parent.setAddressEmployees(ed.getAddress2()+ed.getAddress3()+ed.getAddress4());
                parent.setLatEmployees(new Float(ed.getLat()));
                parent.setLngEmployees(new Float(ed.getLng()));
            }
            if (CommonUtils.isNotEmpty(cd)){
                /* 客户头像二次加工处理 */
                parent.setCustomerHeadUrl(cd.getHeadUrl());
            }
            /* 第一次工作内容 */
            if (wds.isEmpty()){
                parent.setWdp(new WorkDetailsPOJO());
            }else {
                parent.setWdp(wds.get(0));
            }
            /* 保洁员和客户是否已评价 */
            Boolean yes1 = orderEvaluationService.getEvaluationStatusOfCustomer(number);
            Boolean yes2 = orderEvaluationService.getEvaluationStatusOfEmployees(number);
            parent.setYes1(yes1);
            parent.setYes2(yes2);

            return R.ok(parent);
        }

        /* 查數據庫 */
        QueryWrapper qw = new QueryWrapper();
        qw.eq("number", number);
        OrderDetails od = orderDetailsService.getOne(qw);
        if (CommonUtils.isNotEmpty(od)) {
            QueryWrapper qw2 = new QueryWrapper();
            qw2.eq("number", od.getNumber());
            List<WorkDetails> wds = workDetailsService.list(qw2);
            List<OrderPhotos> ops = orderPhotosService.listByNumber(od.getNumber().toString());
            OrderDetailsPOJO odp = this.odp(od, wds, ops);

            OrderDetailsParent parent = odp;

            List<Integer> noteIds = CommonUtils.stringToList(odp.getNoteIds());
            if(CollectionUtils.isNotEmpty(noteIds)){
                List<SysJobNote> sysJobNotes = sysJobNoteService.listByIds(noteIds);
                parent.setNotes(sysJobNotes);
            }

            List<Integer> jobIds = CommonUtils.stringToList(odp.getJobIds());
            List<SysJobContend> jobs = sysJobContendService.listByIds(jobIds);
            parent.setJobs(jobs);

            EmployeesDetails ed = employeesDetailsService.getById(odp.getEmployeesId());
            CustomerDetails cd = customerDetailsService.getById(odp.getCustomerId());
            if (CommonUtils.isNotEmpty(ed)){
                /* 保洁员头像二次加工处理 */
                parent.setEmployeesHeadUrl(ed.getHeadUrl());
                /* 保洁员地址二次加工 */
                parent.setAddressEmployees(ed.getAddress2()+ed.getAddress3()+ed.getAddress4());
                parent.setLatEmployees(new Float(ed.getLat()));
                parent.setLngEmployees(new Float(ed.getLng()));
            }
            if (CommonUtils.isNotEmpty(cd)){
                /* 客户头像二次加工处理 */
                parent.setCustomerHeadUrl(cd.getHeadUrl());
            }

            return R.ok(odp);
        }
        return R.failed(null, "訂單不存在");
    }

    @Override
    public R queryByAdmin(Integer type, Page page) {
        /* type = 0全部 1待付款 2待服务 3进行中 4待评价 5已完成 */
        QueryWrapper qw = new QueryWrapper();
        qw.select("id");
        List<Integer> empIds = employeesDetailsService.listObjs(qw);

        List<OrderDetailsPOJO> res = new ArrayList<>();
        if (type.equals(0)){
            res.addAll(this.order1ByEmployeesAll(empIds));
            res.addAll(this.order2ByEmployeesAll(empIds));
            res.addAll(this.order3ByEmployeesAll(empIds));
            res.addAll(this.order4ByEmployeesAll(empIds));
            res.addAll(this.order5ByEmployeesAll(empIds));
        }else if (type.equals(1)){
            res.addAll(this.order1ByEmployeesAll(empIds));
        }else if (type.equals(2)){
            res.addAll(this.order2ByEmployeesAll(empIds));
        }else if (type.equals(3)){
            res.addAll(this.order3ByEmployeesAll(empIds));
        }else if (type.equals(4)){
            res.addAll(this.order4ByEmployeesAll(empIds));
        }else if (type.equals(5)){
            res.addAll(this.order5ByEmployeesAll(empIds));
        }
        SortListUtil<OrderDetailsPOJO> sort = new SortListUtil<>();
        sort.Sort(res,"getStartDateTime","desc");
        List<OrderDetailsParent> sons = res.stream().map(x -> {
            /* 工作内容二次加工处理 */
            OrderDetailsParent son = x;
            List<Integer> jobIds = CommonUtils.stringToList(x.getJobIds());
            List<SysJobContend> jobs = sysJobContendService.listByIds(jobIds);
            son.setJobs(jobs);
            /* 保洁员头像二次加工处理 */
            son.setEmployeesHeadUrl(employeesDetailsService.getById(x.getEmployeesId()).getHeadUrl());
            return son;
        }).collect(Collectors.toList());
        Page pages = PageUtils.getPages((int) page.getCurrent(), (int) page.getSize(), sons);
        return R.ok(pages, "获取成功");
    }

    @Override
    public String cardPay(String number, String callBackUrl) {
//        Integer userId = TokenUtils.getCurrentUserId();
//        CustomerDetails cd = customerDetailsService.getByUserId(userId);
//        Integer customerId = cd.getId();

        //获取订单详情
        OrderDetailsPOJO odp = (OrderDetailsPOJO) this.getOrder(number).getData();

//        //判斷是否是自己的訂單
//        if (!odp.getCustomerId().equals(customerId)) return "待支付訂單："+number+" 不存在";

        //订单状态判断_是否是未支付状态
        Boolean isNoPay = odp.getOrderState().equals(2);
        if (!isNoPay) return "待支付訂單："+number+" 不存在";

        //生成支付页面
        String doc = this.odpToPaymentPage(odp, callBackUrl);

        //返回支付页面
        return doc;
    }

    @Override
    public String odpToPaymentPage(OrderDetailsPOJO odp, String callBackUrl) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        String nowP = dtf2.format(now);
        String price = odp.getPriceBeforeDiscount().toString();

        AllInOne all = new AllInOne("");
        AioCheckOutOneTime obj = new AioCheckOutOneTime();
        obj.setMerchantTradeNo(odp.getNumber());
        obj.setMerchantTradeDate(nowP);
        obj.setTotalAmount(price);

        //订单来源 0钟点工 1包工 2需求单
        if (odp.getOrderOrigin().equals(0)){
            obj.setTradeDesc("訂單來源，鐘點工");
            obj.setItemName("家政鐘點服務");
        }
        if (odp.getOrderOrigin().equals(1)){
            obj.setTradeDesc("訂單來源，單次服務");
            obj.setItemName("家政單次服務");
        }
        if (odp.getOrderOrigin().equals(2)){
            obj.setTradeDesc("訂單來源，需求單");
            obj.setItemName("家政需求單服務");
        }

        obj.setReturnURL(callBackUrl);
        obj.setNeedExtraPaidInfo("N");
        obj.setRedeem("Y");
        String form = all.aioCheckOut(obj, null);
        return form;
    }

    @Override
    public String cardPayCallback(CardPayCallbackParams params) {
        /* 保存回调信息 */
        CardPayCallback cpc = new CardPayCallback(params);
        cardPayCallbackService.save(cpc);
        /* 订单状态转变 */
        this.inputSql(cpc.getMerchantTradeNo(), true); //将处理中订单，转变为待支付订单
        return "1|OK";
    }

    @Override
    public R queryByManagerId(Integer manId, Integer type) {
        /* 獲取經理旗下保潔員的Ids */
        List<Integer> empIds = groupEmployeesService.getEmployeesIdsByManagerId(manId);

        List<OrderDetailsPOJO> res = new ArrayList<>();
        if (type.equals(0)){
            res.addAll(this.order1ByEmployeesAll(empIds));
            res.addAll(this.order2ByEmployeesAll(empIds));
            res.addAll(this.order3ByEmployeesAll(empIds));
            res.addAll(this.order4ByEmployeesAll(empIds));
            res.addAll(this.order5ByEmployeesAll(empIds));
        }else if (type.equals(1)){
            res.addAll(this.order1ByEmployeesAll(empIds));
        }else if (type.equals(2)){
            res.addAll(this.order2ByEmployeesAll(empIds));
        }else if (type.equals(3)){
            res.addAll(this.order3ByEmployeesAll(empIds));
        }else if (type.equals(4)){
            res.addAll(this.order4ByEmployeesAll(empIds));
        }else if (type.equals(5)){
            res.addAll(this.order5ByEmployeesAll(empIds));
        }
        SortListUtil<OrderDetailsPOJO> sort = new SortListUtil<>();
        sort.Sort(res,"getStartDateTime","desc");
        List<OrderDetailsParent> sons = res.stream().map(x -> {
            /* 工作内容二次加工处理 */
            OrderDetailsParent son = x;
            List<Integer> jobIds = CommonUtils.stringToList(x.getJobIds());
            List<SysJobContend> jobs = new ArrayList<>();
            if (!jobIds.isEmpty()) jobs = sysJobContendService.listByIds(jobIds);
            son.setJobs(jobs);
            /* 保洁员头像二次加工处理 */
            son.setEmployeesHeadUrl(employeesDetailsService.getById(x.getEmployeesId()).getHeadUrl());
            son.setCustomerHeadUrl(customerDetailsService.getById(x.getCustomerId()).getHeadUrl());
            return son;
        }).collect(Collectors.toList());
        return R.ok(sons, "获取成功");
    }

    @Override
    public R setWorkDetails(SetOrderWorkDetailsDTO dto) {
        /* 先做数据处理 */
        List<WorkDetailsPOJO> wdp = dto.getWorkDetails().stream().map(wd -> {
            return new WorkDetailsPOJO(wd.getDate(), wd.getWeek(), wd.getTimeSlots(), true, wd.getTodayPrice());
        }).collect(Collectors.toList());

        /* 数据修改,对hash操控，直接修改workDetails */
        Set<String> keys = redisTemplate.keys("OrderToBePaid:employeesId*:" + dto.getNumber());
        Object[] keysArr = keys.toArray();
        String key = keysArr[0].toString();
        redisTemplate.opsForHash().put(key, "workDetails", wdp);
        return R.ok(null, "成功修改工作安排");
    }

    @Override
    public R setJobs(SetOrderJobsDTO dto) {
        /* 先做数据处理 */
        String jobIds = CommonUtils.listToString(dto.getJobIds());

        /* 数据修改,对hash操控，直接修改jobIds */
        Set<String> keys = redisTemplate.keys("OrderToBePaid:employeesId*:" + dto.getNumber());
        Object[] keysArr = keys.toArray();
        String key = keysArr[0].toString();
        redisTemplate.opsForHash().put(key, "jobIds", jobIds);
        return R.ok(null, "成功修改工作內容");
    }

    @Override
    public R setDiscountPrice(SetOrderDiscountPriceDTO dto) {
        /* 数据修改,对hash操控，直接修改priceAfterDiscount */
        Set<String> keys = redisTemplate.keys("OrderToBePaid:employeesId*:" + dto.getNumber());
        Object[] keysArr = keys.toArray();
        String key = keysArr[0].toString();
        redisTemplate.opsForHash().put(key, "priceAfterDiscount", dto.getDiscountPrice());
        return R.ok(null, "成功修改折後價");
    }

    @Override
    public R setCustomerInformation(SetOrderCustomerInformationDTO dto) {
        /* 数据修改,对hash操控，直接修改priceAfterDiscount */
        Set<String> keys = redisTemplate.keys("OrderToBePaid:employeesId*:" + dto.getOrderNumber());
        Object[] keysArr = keys.toArray();
        String key = keysArr[0].toString();
        redisTemplate.opsForHash().put(key, "name2", dto.getName());
        redisTemplate.opsForHash().put(key, "phone2", dto.getPhone());
        redisTemplate.opsForHash().put(key, "phPrefix2", dto.getPhonePrefix());
        redisTemplate.opsForHash().put(key, "address", dto.getAddress());
        redisTemplate.opsForHash().put(key, "lat", dto.getLat());
        redisTemplate.opsForHash().put(key, "lng", dto.getLng());
        return R.ok(null, "成功修改客戶信息");
    }

    @Override
    public R setOrderInformation(SetOrderInformationDTO dto) {
        synchronized (this){
            //修改訂單工作安排
            /* 先做数据处理 */
            List<WorkDetailsPOJO> wdp = dto.getWorkDetails().stream().map(wd -> {
                return new WorkDetailsPOJO(wd.getDate(), wd.getWeek(), wd.getTimeSlots(), true, wd.getTodayPrice());
            }).collect(Collectors.toList());

            /* 数据修改,对hash操控，直接修改workDetails */
            Set<String> keys = redisTemplate.keys("OrderToBePaid:employeesId*:" + dto.getNumber());
            Object[] keysArr = keys.toArray();
            String key = keysArr[0].toString();
            redisTemplate.opsForHash().put(key, "workDetails", wdp);

            //修改待支付订单的工作内容
            /* 先做数据处理 */
            String jobIds = CommonUtils.listToString(dto.getJobIds());

            /* 数据修改,对hash操控，直接修改jobIds */
            Set<String> keys1 = redisTemplate.keys("OrderToBePaid:employeesId*:" + dto.getNumber());
            Object[] keysArr1 = keys1.toArray();
            String key1 = keysArr1[0].toString();
            redisTemplate.opsForHash().put(key1, "jobIds", jobIds);

            //修改待支付订单的折后价格
            /* 数据修改,对hash操控，直接修改priceAfterDiscount */
            Set<String> keys2 = redisTemplate.keys("OrderToBePaid:employeesId*:" + dto.getNumber());
            Object[] keysArr2 = keys2.toArray();
            String key2 = keysArr2[0].toString();
            redisTemplate.opsForHash().put(key2, "priceAfterDiscount", dto.getDiscountPrice());

            //修改待支付订单的客戶信息
            /* 数据修改,对hash操控，直接修改priceAfterDiscount */
            Set<String> keys3 = redisTemplate.keys("OrderToBePaid:employeesId*:" + dto.getNumber());
            Object[] keysArr3 = keys3.toArray();
            String key3 = keysArr3[0].toString();
            redisTemplate.opsForHash().put(key3, "name2", dto.getName());
            redisTemplate.opsForHash().put(key3, "phone2", dto.getPhone());
            redisTemplate.opsForHash().put(key3, "phPrefix2", dto.getPhonePrefix());
            redisTemplate.opsForHash().put(key3, "address", dto.getAddress());
            redisTemplate.opsForHash().put(key3, "lat", dto.getLat());
            redisTemplate.opsForHash().put(key3, "lng", dto.getLng());
            return R.ok(null, "成功修改訂單信息");
        }
    }

    @Override
    public R getWorkTimeTableByCus(TimeTableByCusDTO dto) {

        if (dto.getMonth()>12 || dto.getMonth()<1) return R.failed(null, "月份錯誤");

        LocalDate thisMonthFirstDay = LocalDate.of(dto.getYear(), dto.getMonth(), 1);//這個月第一天
        LocalDate thisMonthLastDay = thisMonthFirstDay.plusMonths(1).plusDays(-1);//這個月最後一天 第一天加一個月然後減去一天

        Integer startWeek = thisMonthFirstDay.getDayOfWeek().getValue();
        Integer startMakeUp = startWeek == 7 ? 0 : startWeek;                 //   星期几 7 1 2 3 4 5 6
        //   需要補 0 1 2 3 4 5 6 天
        Integer endWeek = thisMonthLastDay.getDayOfWeek().getValue();
        Integer endMakeUp = endWeek == 7 ? 6 : 6-endWeek;              //   星期几 7 1 2 3 4 5 6
        //   需要補 6 5 4 3 2 1 0 天
        LocalDate startDate = thisMonthFirstDay.plusDays(-startMakeUp);
        LocalDate endDate = thisMonthLastDay.plusDays(endMakeUp);

        QueryWrapper<OrderDetails> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("customer_id",dto.getCustomerId());
        List<Long> numbers = orderDetailsService.list(queryWrapper).stream().map(x -> {
            return x.getNumber();
        }).collect(Collectors.toList());

        List<WorkTimeTableDateVO> workTimeTables = workDetailsService.getWorkTables(numbers,startDate,endDate, dto.getMonth());
        return R.ok(workTimeTables);

    }

    /* 转换到数据库存储 */
    private List<OrderPhotos> orderPhotos(List<OrderPhotoPOJO> photos, String number){
        if (CommonUtils.isEmpty(photos)) return new ArrayList<>();
        List<OrderPhotos> ops = photos.stream().map(x -> {
            return new OrderPhotos(null, Long.valueOf(number), x.getPhotoUrl(), x.getEvaluate());
        }).collect(Collectors.toList());
        return ops;
    }

    /* 转换到数据库存储 *//*
    private List<WorkDetails> workDetails(List<WorkDetailsPOJO> workDetails, String number){
        if (CommonUtils.isEmpty(workDetails)) return new ArrayList<>();
        List<WorkDetails> wds = workDetails.stream().map(x -> {
            if (x.getCanBeOnDuty()) {
                StringBuilder sb = new StringBuilder();
                x.getTimeSlots().forEach(timeSlot -> {
                    String s = timeSlot.getTimeSlotStart()+"+"+timeSlot.getTimeSlotLength().toString()+"+"+timeSlot.getThisSlotPrice()+" ";
                    sb.append(s);
                });
                return new WorkDetails(null, Long.valueOf(number), x.getDate(), x.getWeek(), sb.toString().trim(), x.getCanBeOnDuty(), x.getTodayPrice());
            }
            return null;
        }).collect(Collectors.toList());
        *//* 去null *//*
        wds = wds.stream().filter(wd -> {
            return wd != null;
        }).collect(Collectors.toList());
        return wds;
    }
*/
    /* 转换到数据库存储 */
    private List<WorkDetails> workDetails2(List<WorkDetailsPOJO> workDetails, String number){
        if (CommonUtils.isEmpty(workDetails)) return new ArrayList<>();
        List<WorkDetails> wds = workDetails.stream().map(x -> {
            if (x.getCanBeOnDuty()) {
                List<TimeSlot> timeSlots = x.getTimeSlots();
                for (int i = 0; i < timeSlots.size(); i++) {
                    return new WorkDetails(null, Long.valueOf(number), x.getDate(), x.getWeek(), timeSlots.get(i).getTimeSlotStart(),timeSlots.get(i).getTimeSlotLength(),timeSlots.get(i).getThisSlotPrice()==null?x.getTodayPrice():new BigDecimal(timeSlots.get(i).getThisSlotPrice()), x.getCanBeOnDuty(), x.getTodayPrice());
                }
            }
            return null;
        }).collect(Collectors.toList());
        /* 去null */
        wds = wds.stream().filter(wd -> {
            return wd != null;
        }).collect(Collectors.toList());
        return wds;
    }

    /*转到pojo实际的应用*/
   /* private List<WorkDetailsPOJO> workDetailsPOJOs(List<WorkDetails> wds){
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
    }*/

    private List<WorkDetailsPOJO> workDetailsPOJOs2(List<WorkDetails> wds){
        List<WorkDetailsPOJO> workDetailsPOJOS = new ArrayList<>();
        for (int i = 0; i < wds.size(); i++) {
            ArrayList<TimeSlot> timeSlots = new ArrayList<>();
            timeSlots.add(new TimeSlot(wds.get(i).getTimeSlots(),wds.get(i).getTimeLength(),wds.get(i).getTimePrice().toString()));
            WorkDetailsPOJO workDetailsPOJO = new WorkDetailsPOJO(wds.get(i).getDate(), wds.get(i).getWeek(), timeSlots, wds.get(i).getCanBeOnDuty(), wds.get(i).getTodayPrice());
            workDetailsPOJOS.add(workDetailsPOJO);
        }
        return workDetailsPOJOS;
    }

    private List<OrderPhotoPOJO> orderPhotosPOJOs(List<OrderPhotos> ops){
        List<OrderPhotoPOJO> orderPhotosPOJOs = ops.stream().map(wd -> {
            OrderPhotoPOJO opp = new OrderPhotoPOJO(wd.getId(), wd.getPhotoUrl(), wd.getEvaluate());
            return opp;
        }).collect(Collectors.toList());
        return orderPhotosPOJOs;
    }

    private OrderDetailsPOJO odp(OrderDetails od, List<WorkDetails> wds, List<OrderPhotos> ops){
        OrderDetailsPOJO odp = new OrderDetailsPOJO(od);
        List<WorkDetailsPOJO> workDetailsPOJOs = workDetailsPOJOs2(wds);
        List<OrderPhotoPOJO> orderPhotosPOJOs = orderPhotosPOJOs(ops);
        odp.setWorkDetails(workDetailsPOJOs);
        odp.setPhotos(orderPhotosPOJOs);
        return odp;
    }

    private TimeSlot ts(String s){
        String[] strings = s.split("\\+");
        String s1 = strings[0];
        String s2 = strings[1];
        String s3 = strings[2];
        LocalTime time = LocalTime.of(Integer.valueOf(s1.substring(0,2)), Integer.valueOf(s1.substring(3,5)));
        Float length = Float.valueOf(s2);
        TimeSlot ts = new TimeSlot(time, length);
        ts.setThisSlotPrice(s3);
        return ts;
    }

    @Override
    public OrderDetailsPOJO getByNumber(String number) {
        /* 查數據庫 */
        QueryWrapper qw = new QueryWrapper();
        qw.eq("number", number);
        OrderDetails od = orderDetailsService.getOne(qw);
        if (CommonUtils.isNotEmpty(od)) {
            QueryWrapper qw2 = new QueryWrapper();
            qw2.eq("number", od.getNumber());
            List<WorkDetails> wds = workDetailsService.list(qw2);
            List<OrderPhotos> ops = orderPhotosService.listByNumber(od.getNumber().toString());
            OrderDetailsPOJO odp = this.odp(od, wds, ops);
            OrderDetailsParent parent = odp;

            List<Integer> jobIds = CommonUtils.stringToList(odp.getJobIds());
            List<SysJobContend> jobs = sysJobContendService.listByIds(jobIds);
            parent.setJobs(jobs);

            List<Integer> noteIds = CommonUtils.stringToList(odp.getNoteIds());
            if(CollectionUtils.isNotEmpty(noteIds)){
                List<SysJobNote> sysJobNotes = sysJobNoteService.listByIds(noteIds);
                parent.setNotes(sysJobNotes);
            }

            EmployeesDetails ed = employeesDetailsService.getById(odp.getEmployeesId());
            CustomerDetails cd = customerDetailsService.getById(odp.getCustomerId());
            if (CommonUtils.isNotEmpty(ed)) {
                /* 保洁员头像二次加工处理 */
                parent.setEmployeesHeadUrl(ed.getHeadUrl());
                /* 保洁员地址二次加工 */
                parent.setAddressEmployees(ed.getAddress2() + ed.getAddress3() + ed.getAddress4());
                parent.setLatEmployees(new Float(ed.getLat()));
                parent.setLngEmployees(new Float(ed.getLng()));
            }
            if (CommonUtils.isNotEmpty(cd)) {
                /* 客户头像二次加工处理 */
                parent.setCustomerHeadUrl(cd.getHeadUrl());
            }

            return odp;
        }
        return null;
    }

    @Override
    public R getWorkTimeTableByEmp(TimeTableByEmpDTO dto) {
        if (dto.getMonth()>12 || dto.getMonth()<1) return R.failed(null, "月份錯誤");

        LocalDate thisMonthFirstDay = LocalDate.of(dto.getYear(), dto.getMonth(), 1);//這個月第一天
        LocalDate thisMonthLastDay = thisMonthFirstDay.plusMonths(1).plusDays(-1);//這個月最後一天 第一天加一個月然後減去一天

        Integer startWeek = thisMonthFirstDay.getDayOfWeek().getValue();
        Integer startMakeUp = startWeek == 7 ? 0 : startWeek;                 //   星期几 7 1 2 3 4 5 6
        //   需要補 0 1 2 3 4 5 6 天
        Integer endWeek = thisMonthLastDay.getDayOfWeek().getValue();
        Integer endMakeUp = endWeek == 7 ? 6 : 6-endWeek;              //   星期几 7 1 2 3 4 5 6
        //   需要補 6 5 4 3 2 1 0 天
        LocalDate startDate = thisMonthFirstDay.plusDays(-startMakeUp);
        LocalDate endDate = thisMonthLastDay.plusDays(endMakeUp);

        QueryWrapper<OrderDetails> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("employees_id",dto.getEmployeesId());
        List<Long> numbers = orderDetailsService.list(queryWrapper).stream().map(x -> {
            return x.getNumber();
        }).collect(Collectors.toList());

        List<WorkTimeTableDateVO> workTimeTables = workDetailsService.getWorkTables(numbers,startDate,endDate, dto.getMonth());
        return R.ok(workTimeTables);
    }

    @Override
    public R getWorkTimeTableByMan(TimeTableByManDTO dto) {
        if (dto.getMonth()>12 || dto.getMonth()<1) return R.failed(null, "月份錯誤");

        LocalDate thisMonthFirstDay = LocalDate.of(dto.getYear(), dto.getMonth(), 1);//這個月第一天
        LocalDate thisMonthLastDay = thisMonthFirstDay.plusMonths(1).plusDays(-1);//這個月最後一天 第一天加一個月然後減去一天

        Integer startWeek = thisMonthFirstDay.getDayOfWeek().getValue();
        Integer startMakeUp = startWeek == 7 ? 0 : startWeek;                 //   星期几 7 1 2 3 4 5 6
        //   需要補 0 1 2 3 4 5 6 天
        Integer endWeek = thisMonthLastDay.getDayOfWeek().getValue();
        Integer endMakeUp = endWeek == 7 ? 6 : 6-endWeek;              //   星期几 7 1 2 3 4 5 6
        //   需要補 6 5 4 3 2 1 0 天
        LocalDate startDate = thisMonthFirstDay.plusDays(-startMakeUp);
        LocalDate endDate = thisMonthLastDay.plusDays(endMakeUp);

        /* 獲取經理旗下保潔員的Ids */
        List<Integer> empIds = groupEmployeesService.getEmployeesIdsByManagerId(dto.getManagerId());

        List<Long> numbers = new ArrayList<>();
        for (int i = 0; i < empIds.size(); i++) {
            List<Long> number = new ArrayList<>();
            QueryWrapper<OrderDetails> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("employees_id",empIds.get(i));
            for (OrderDetails x : orderDetailsService.list(queryWrapper)) {
                Long orderNumber = x.getNumber();
                number.add(orderNumber);
            }
            numbers.addAll(number);
        }
        List<WorkTimeTableDateVO> workTimeTables = workDetailsService.getWorkTables(numbers,startDate,endDate, dto.getMonth());
        return R.ok(workTimeTables);
    }

    @Override
    public R getWorkTimeTableByCom(TimeTableByComDTO dto) {
        if (dto.getMonth()>12 || dto.getMonth()<1) return R.failed(null, "月份錯誤");

        Integer companyId = companyDetailsService.getCompanyIdByUserId(TokenUtils.getCurrentUserId());

        LocalDate thisMonthFirstDay = LocalDate.of(dto.getYear(), dto.getMonth(), 1);//這個月第一天
        LocalDate thisMonthLastDay = thisMonthFirstDay.plusMonths(1).plusDays(-1);//這個月最後一天 第一天加一個月然後減去一天

        Integer startWeek = thisMonthFirstDay.getDayOfWeek().getValue();
        Integer startMakeUp = startWeek == 7 ? 0 : startWeek;                 //   星期几 7 1 2 3 4 5 6
        //   需要補 0 1 2 3 4 5 6 天
        Integer endWeek = thisMonthLastDay.getDayOfWeek().getValue();
        Integer endMakeUp = endWeek == 7 ? 6 : 6-endWeek;              //   星期几 7 1 2 3 4 5 6
        //   需要補 6 5 4 3 2 1 0 天
        LocalDate startDate = thisMonthFirstDay.plusDays(-startMakeUp);
        LocalDate endDate = thisMonthLastDay.plusDays(endMakeUp);

        QueryWrapper<OrderDetails> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("company_id",companyId);
        List<Long> numbers = orderDetailsService.list(queryWrapper).stream().map(x -> {
            return x.getNumber();
        }).collect(Collectors.toList());

        List<WorkTimeTableDateVO> workTimeTables = workDetailsService.getWorkTables(numbers,startDate,endDate,dto.getMonth());
        return R.ok(workTimeTables);
    }

    @Override
    public R getWorkTimeDetails(Integer id) {
        WorkClock byId = workClockService.getById(id);
        WorkDetails workDetails = workDetailsService.getById(byId.getWorkId());

        QueryWrapper<WorkDetails> qw2 = new QueryWrapper<>();
        qw2.eq("number",workDetails.getNumber());
        int total = workDetailsService.count(qw2);
        int hasWork = workDetailsMapper.countWork(workDetails.getNumber());
        String workProgress = hasWork+"/"+total;

        OrderDetailsPOJO orderDetailsPOJO = this.getByNumber(workDetails.getNumber().toString());
        List<OrderPhotoPOJO> photos = orderDetailsPOJO.getPhotos();

        List<OrderPhotoVO> orderPhotoVOS = new ArrayList<>();
        for (int i = 0; i < photos.size(); i++) {
            OrderPhotoVO orderPhotoVO = new OrderPhotoVO();
            orderPhotoVO.setEvaluate(photos.get(i).getEvaluate());
            orderPhotoVO.setPhotoUrl(photos.get(i).getPhotoUrl());
            orderPhotoVO.setOrderPhotoId(photos.get(i).getOrderPhotoId());
            if(i==0){
                orderPhotoVO.setEmpPhoto(byId.getPhoto1());
            }
            if(i==1){
                orderPhotoVO.setEmpPhoto(byId.getPhoto2());
            }
            if(i==2){
                orderPhotoVO.setEmpPhoto(byId.getPhoto3());
            }
            if(i==3){
                orderPhotoVO.setEmpPhoto(byId.getPhoto4());
            }
            if(i==4){
                orderPhotoVO.setEmpPhoto(byId.getPhoto5());
            }
            orderPhotoVOS.add(orderPhotoVO);
        }

        WorkClockVO workClockVO = new WorkClockVO(byId.getId(),workProgress, byId.getWorkStatus(), byId.getToWorkStatus(), byId.getToWorkTime(), byId.getOffWorkStatus(), byId.getOffWorkTime(),
                byId.getPhoto1(),byId.getPhoto1Status(),byId.getPhoto2(),byId.getPhoto2Status(),byId.getPhoto3(),byId.getPhoto3Status(),byId.getPhoto4(),byId.getPhoto4Status(),byId.getPhoto5(),byId.getPhoto4Status(), byId.getStaffCheck(), byId.getCustomerConfirm(),byId.getStaffSummary(),byId.getStaffPhoto(), byId.getCustomerStarRating(), byId.getCustomerPhoto(),byId.getCustomerEvaluation(), workDetails, orderDetailsPOJO,orderPhotoVOS);
        return R.ok(workClockVO);
    }

    @Override
    public R getOrder2(String number) {
        /* 查redis */
        Set<String> keys = redisTemplate.keys("OrderToBePaid:employeesId*:" + number);
        if (!keys.isEmpty()) {
            Object[] keysArr = (Object[]) keys.toArray();
            String key = keysArr[0].toString();
            OrderDetailsPOJO odp = null;
            Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
            try {
                odp = (OrderDetailsPOJO) CommonUtils.mapToObject(map, OrderDetailsPOJO.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            List<OrderPhotoPOJO> ops = new ArrayList<>();
            List<WorkDetailsPOJO> wds = new ArrayList<>();
            Object photoObj = map.get("photos");
            Object workDetailsObj = map.get("workDetails");
            if (!photoObj.equals("")) ops = (List<OrderPhotoPOJO>) map.get("photos");;
            if (!workDetailsObj.equals("")) wds = (List<WorkDetailsPOJO>) map.get("workDetails");
            List<WorkDetailsPOJO> workDetailsPOJOS = new ArrayList<>();
            for (int i = 0; i < wds.size(); i++) {
                List<TimeSlot> timeSlots = wds.get(i).getTimeSlots();
                for (int i1 = 0; i1 < timeSlots.size(); i1++) {
                    List<TimeSlot> timeSlots1 = new ArrayList<>();
                    timeSlots1.add(timeSlots.get(i1));
                    WorkDetailsPOJO workDetailsPOJO = new WorkDetailsPOJO();
                    workDetailsPOJO.setDate(wds.get(i).getDate());
                    workDetailsPOJO.setCanBeOnDuty(wds.get(i).getCanBeOnDuty());
                    workDetailsPOJO.setWeek(wds.get(i).getWeek());
                    workDetailsPOJO.setTodayPrice(wds.get(i).getTodayPrice());
                    workDetailsPOJO.setTimeSlots(timeSlots1);
                    workDetailsPOJOS.add(workDetailsPOJO);
                }
            }
            odp.setPhotos(ops);
            odp.setWorkDetails(workDetailsPOJOS);

            OrderDetailsParent parent = odp;
            List<Integer> jobIds = CommonUtils.stringToList(odp.getJobIds());
            List<SysJobContend> jobs = sysJobContendService.listByIds(jobIds);
            parent.setJobs(jobs);

            List<Integer> noteIds = CommonUtils.stringToList(odp.getNoteIds());
            if(CollectionUtils.isNotEmpty(noteIds)){
                List<SysJobNote> sysJobNotes = sysJobNoteService.listByIds(noteIds);
                parent.setNotes(sysJobNotes);
            }

            EmployeesDetails ed = employeesDetailsService.getById(odp.getEmployeesId());
            CustomerDetails cd = customerDetailsService.getById(odp.getCustomerId());
            if (CommonUtils.isNotEmpty(ed)){
                /* 保洁员头像二次加工处理 */
                parent.setEmployeesHeadUrl(ed.getHeadUrl());
                /* 保洁员地址二次加工 */
                parent.setAddressEmployees(ed.getAddress2()+ed.getAddress3()+ed.getAddress4());
                parent.setLatEmployees(new Float(ed.getLat()));
                parent.setLngEmployees(new Float(ed.getLng()));
            }
            if (CommonUtils.isNotEmpty(cd)){
                /* 客户头像二次加工处理 */
                parent.setCustomerHeadUrl(cd.getHeadUrl());
            }
            /* 第一次工作内容 */
            if (wds.isEmpty()){
                parent.setWdp(new WorkDetailsPOJO());
            }else {
                parent.setWdp(wds.get(0));
            }
            /* 保洁员和客户是否已评价 */
            Boolean yes1 = orderEvaluationService.getEvaluationStatusOfCustomer(number);
            Boolean yes2 = orderEvaluationService.getEvaluationStatusOfEmployees(number);
            parent.setYes1(yes1);
            parent.setYes2(yes2);

            return R.ok(parent);
        }

        /* 查數據庫 */
        QueryWrapper qw = new QueryWrapper();
        qw.eq("number", number);
        OrderDetails od = orderDetailsService.getOne(qw);
        if (CommonUtils.isNotEmpty(od)) {
            QueryWrapper qw2 = new QueryWrapper();
            qw2.eq("number", od.getNumber());
            List<WorkDetails> wds = workDetailsService.list(qw2);
            List<OrderPhotos> ops = orderPhotosService.listByNumber(od.getNumber().toString());
            OrderDetailsPOJO odp = this.odp(od, wds, ops);

            OrderDetailsParent parent = odp;

            List<WorkDetailsPOJO> workDetails = odp.getWorkDetails();
            List<WorkDetailsPOJO> workDetailsPOJOS = new ArrayList<>();
            for (int i = 0; i < wds.size(); i++) {
                List<TimeSlot> timeSlots = workDetails.get(i).getTimeSlots();
                for (int i1 = 0; i1 < timeSlots.size(); i1++) {
                    List<TimeSlot> timeSlots1 = new ArrayList<>();
                    timeSlots1.add(timeSlots.get(i1));
                    WorkDetailsPOJO workDetailsPOJO = new WorkDetailsPOJO();
                    workDetailsPOJO.setDate(wds.get(i).getDate());
                    workDetailsPOJO.setCanBeOnDuty(wds.get(i).getCanBeOnDuty());
                    workDetailsPOJO.setWeek(wds.get(i).getWeek());
                    workDetailsPOJO.setTodayPrice(wds.get(i).getTodayPrice());
                    workDetailsPOJO.setTimeSlots(timeSlots1);
                    workDetailsPOJOS.add(workDetailsPOJO);
                }
            }
            odp.setWorkDetails(workDetailsPOJOS);
            List<Integer> noteIds = CommonUtils.stringToList(odp.getNoteIds());
            if(CollectionUtils.isNotEmpty(noteIds)){
                List<SysJobNote> sysJobNotes = sysJobNoteService.listByIds(noteIds);
                parent.setNotes(sysJobNotes);
            }

            List<Integer> jobIds = CommonUtils.stringToList(odp.getJobIds());
            List<SysJobContend> jobs = sysJobContendService.listByIds(jobIds);
            parent.setJobs(jobs);

            EmployeesDetails ed = employeesDetailsService.getById(odp.getEmployeesId());
            CustomerDetails cd = customerDetailsService.getById(odp.getCustomerId());
            if (CommonUtils.isNotEmpty(ed)){
                /* 保洁员头像二次加工处理 */
                parent.setEmployeesHeadUrl(ed.getHeadUrl());
                /* 保洁员地址二次加工 */
                parent.setAddressEmployees(ed.getAddress2()+ed.getAddress3()+ed.getAddress4());
                parent.setLatEmployees(new Float(ed.getLat()));
                parent.setLngEmployees(new Float(ed.getLng()));
            }
            if (CommonUtils.isNotEmpty(cd)){
                /* 客户头像二次加工处理 */
                parent.setCustomerHeadUrl(cd.getHeadUrl());
            }

            return R.ok(odp);
        }
        return R.failed(null, "訂單不存在");
    }

    @Override
    public R setNote(Long number, MultipartFile[] photos, String[] evaluates, String remarks) {
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
                OrderPhotoPOJO pojo = new OrderPhotoPOJO(null, urlPrefix + fileAbstractPath, evaluates[count.get()]);
                pojoList.add(pojo);
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                count.getAndSet(count.get() + 1);
            }
        });

        /* 数据修改,对hash操控，直接修改jobIds */
        Set<String> keys = redisTemplate.keys("OrderToBePaid:employeesId*:" + number);
        Object[] keysArr = keys.toArray();
        String key = keysArr[0].toString();
        redisTemplate.opsForHash().put(key, "photos", pojoList);
        redisTemplate.opsForHash().put(key, "remarks", remarks);
        return R.ok(null, "成功修改工作备注");
    }

    @Override
    public R getRefundByNumber(String number) {
        OrderRefund byNumber = orderRefundService.getByNumber(number);
        RefundDTO refundDTO = new RefundDTO();
        if(CommonUtils.isNotEmpty(byNumber)){
            refundDTO.setRefundId(byNumber.getId());
            refundDTO.setRefundStatus(byNumber.getStatus());
        }else {
            refundDTO.setRefundId(null);
            refundDTO.setRefundStatus(4);
        }
        return R.ok(refundDTO);
    }

    @Override
    public R newQueryByCom(Integer type) {
        /* type = 0全部 1待付款 2待服务 3进行中 4待评价 5已完成 */
        Integer companyId = companyDetailsService.getCompanyIdByUserId(TokenUtils.getCurrentUserId());
        QueryWrapper qw = new QueryWrapper();
        qw.select("id");
        qw.eq("company_id", companyId);
        List<Integer> empIds = employeesDetailsService.listObjs(qw);

        //預約單
        List<OrderDetailsPOJO> res = new ArrayList<>();
        //代幣單
        List<TokenOrderParent> res2 = new ArrayList<>();
        if (type.equals(0)){
            res2.addAll(this.order1ByCompany(companyId));
            res2.addAll(this.order5ByCompany(companyId));

            res.addAll(this.order1ByEmployeesAll(empIds));
            res.addAll(this.order2ByEmployeesAll(empIds));
            res.addAll(this.order3ByEmployeesAll(empIds));
            res.addAll(this.order4ByEmployeesAll(empIds));
            res.addAll(this.order5ByEmployeesAll(empIds));
        }else if (type.equals(1)){

            res2.addAll(this.order1ByCompany(companyId));

            res.addAll(this.order1ByEmployeesAll(empIds));
        }else if (type.equals(2)){
            res.addAll(this.order2ByEmployeesAll(empIds));
        }else if (type.equals(3)){
            res.addAll(this.order3ByEmployeesAll(empIds));
        }else if (type.equals(4)){
            res.addAll(this.order4ByEmployeesAll(empIds));
        }else if (type.equals(5)){

            res2.addAll(this.order5ByCompany(companyId));

            res.addAll(this.order5ByEmployeesAll(empIds));
        }
        SortListUtil<OrderDetailsPOJO> sort = new SortListUtil<>();
        sort.Sort(res,"getStartDateTime","desc");
        List<OrderDetailsParent> sons = res.stream().map(x -> {
            /* 工作内容二次加工处理 */
            OrderDetailsParent son = x;
            List<Integer> jobIds = CommonUtils.stringToList(x.getJobIds());
            List<SysJobContend> jobs = new ArrayList<>();
            if (!jobIds.isEmpty()) jobs = sysJobContendService.listByIds(jobIds);
            son.setJobs(jobs);
            /* 保洁员头像二次加工处理 */
            son.setEmployeesHeadUrl(employeesDetailsService.getById(x.getEmployeesId()).getHeadUrl());
            son.setCustomerHeadUrl(customerDetailsService.getById(x.getCustomerId()).getHeadUrl());
            /* 第一次工作内容 */
            List<WorkDetailsPOJO> wdp = x.getWorkDetails();
            if (wdp.isEmpty()){
                son.setWdp(new WorkDetailsPOJO());
            }else {
                son.setWdp(x.getWorkDetails().get(0));
            }
            /* 保洁员和客户是否已评价 */
            Boolean yes1 = orderEvaluationService.getEvaluationStatusOfCustomer(x.getNumber());
            Boolean yes2 = orderEvaluationService.getEvaluationStatusOfEmployees(x.getNumber());
            son.setYes1(yes1);
            son.setYes2(yes2);
            return son;
        }).collect(Collectors.toList());

        List<OrderResult> orderResults = new ArrayList<>();
        sons.forEach(x ->{
            OrderDetailsPOJO orderDetailsPOJO = (OrderDetailsPOJO)x;
            OrderResult orderResult = new OrderResult();
            orderResult.setOrderDetailsParent(x);
            orderResult.setTokenOrder(null);
            orderResult.setCreateTime(orderDetailsPOJO.getStartDateTime());
            orderResult.setType(true);
            orderResult.setNumber(orderDetailsPOJO.getNumber());
            orderResult.setOrderState(orderDetailsPOJO.getOrderState());
            orderResults.add(orderResult);
        });

        res2.forEach(x ->{
            OrderResult orderResult = new OrderResult();
            orderResult.setOrderDetailsParent(null);
            orderResult.setTokenOrder(x);
            orderResult.setCreateTime(x.getPayDateTime());
            orderResult.setType(false);
            orderResult.setNumber(x.getNumber());
            orderResult.setOrderState(x.getOrderState());
            orderResults.add(orderResult);
        });

        SortListUtil<OrderResult> sort2 = new SortListUtil<>();
        sort2.Sort(orderResults,"getCreateTime","desc");
        return R.ok(orderResults);
    }

    @Override
    public R getTokenOrder(String number, Integer type) {
        if(type==1){
            /* 查redis */
            Set<String> keys = redisTemplate.keys("OrderToBePaid:employeesId*:" + number);
            if (!keys.isEmpty()) {
                Object[] keysArr = (Object[]) keys.toArray();
                String key = keysArr[0].toString();
                OrderDetailsPOJO odp = null;
                Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
                try {
                    odp = (OrderDetailsPOJO) CommonUtils.mapToObject(map, OrderDetailsPOJO.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                List<OrderPhotoPOJO> ops = new ArrayList<>();
                List<WorkDetailsPOJO> wds = new ArrayList<>();
                Object photoObj = map.get("photos");
                Object workDetailsObj = map.get("workDetails");
                if (!photoObj.equals("")) ops = (List<OrderPhotoPOJO>) map.get("photos");;
                if (!workDetailsObj.equals("")) wds = (List<WorkDetailsPOJO>) map.get("workDetails");
                List<WorkDetailsPOJO> workDetailsPOJOS = new ArrayList<>();
                for (int i = 0; i < wds.size(); i++) {
                    List<TimeSlot> timeSlots = wds.get(i).getTimeSlots();
                    for (int i1 = 0; i1 < timeSlots.size(); i1++) {
                        List<TimeSlot> timeSlots1 = new ArrayList<>();
                        timeSlots1.add(timeSlots.get(i1));
                        WorkDetailsPOJO workDetailsPOJO = new WorkDetailsPOJO();
                        workDetailsPOJO.setDate(wds.get(i).getDate());
                        workDetailsPOJO.setCanBeOnDuty(wds.get(i).getCanBeOnDuty());
                        workDetailsPOJO.setWeek(wds.get(i).getWeek());
                        workDetailsPOJO.setTodayPrice(wds.get(i).getTodayPrice());
                        workDetailsPOJO.setTimeSlots(timeSlots1);
                        workDetailsPOJOS.add(workDetailsPOJO);
                    }
                }
                odp.setPhotos(ops);
                odp.setWorkDetails(workDetailsPOJOS);

                OrderDetailsParent parent = odp;
                List<Integer> jobIds = CommonUtils.stringToList(odp.getJobIds());
                List<SysJobContend> jobs = sysJobContendService.listByIds(jobIds);
                parent.setJobs(jobs);

                List<Integer> noteIds = CommonUtils.stringToList(odp.getNoteIds());
                if(CollectionUtils.isNotEmpty(noteIds)){
                    List<SysJobNote> sysJobNotes = sysJobNoteService.listByIds(noteIds);
                    parent.setNotes(sysJobNotes);
                }

                EmployeesDetails ed = employeesDetailsService.getById(odp.getEmployeesId());
                CustomerDetails cd = customerDetailsService.getById(odp.getCustomerId());
                if (CommonUtils.isNotEmpty(ed)){
                    /* 保洁员头像二次加工处理 */
                    parent.setEmployeesHeadUrl(ed.getHeadUrl());
                    /* 保洁员地址二次加工 */
                    parent.setAddressEmployees(ed.getAddress2()+ed.getAddress3()+ed.getAddress4());
                    parent.setLatEmployees(new Float(ed.getLat()));
                    parent.setLngEmployees(new Float(ed.getLng()));
                }
                if (CommonUtils.isNotEmpty(cd)){
                    /* 客户头像二次加工处理 */
                    parent.setEmployeesHeadUrl(cd.getHeadUrl());
                }
                /* 第一次工作内容 */
                if (wds.isEmpty()){
                    parent.setWdp(new WorkDetailsPOJO());
                }else {
                    parent.setWdp(wds.get(0));
                }
                /* 保洁员和客户是否已评价 */
                Boolean yes1 = orderEvaluationService.getEvaluationStatusOfCustomer(number);
                Boolean yes2 = orderEvaluationService.getEvaluationStatusOfEmployees(number);
                parent.setYes1(yes1);
                parent.setYes2(yes2);

                return R.ok(parent);
            }

            /* 查數據庫 */
            QueryWrapper qw = new QueryWrapper();
            qw.eq("number", number);
            OrderDetails od = orderDetailsService.getOne(qw);
            if (CommonUtils.isNotEmpty(od)) {
                QueryWrapper qw2 = new QueryWrapper();
                qw2.eq("number", od.getNumber());
                List<WorkDetails> wds = workDetailsService.list(qw2);
                List<OrderPhotos> ops = orderPhotosService.listByNumber(od.getNumber().toString());
                OrderDetailsPOJO odp = this.odp(od, wds, ops);

                OrderDetailsParent parent = odp;

                List<WorkDetailsPOJO> workDetails = odp.getWorkDetails();
                List<WorkDetailsPOJO> workDetailsPOJOS = new ArrayList<>();
                for (int i = 0; i < wds.size(); i++) {
                    List<TimeSlot> timeSlots = workDetails.get(i).getTimeSlots();
                    for (int i1 = 0; i1 < timeSlots.size(); i1++) {
                        List<TimeSlot> timeSlots1 = new ArrayList<>();
                        timeSlots1.add(timeSlots.get(i1));
                        WorkDetailsPOJO workDetailsPOJO = new WorkDetailsPOJO();
                        workDetailsPOJO.setDate(wds.get(i).getDate());
                        workDetailsPOJO.setCanBeOnDuty(wds.get(i).getCanBeOnDuty());
                        workDetailsPOJO.setWeek(wds.get(i).getWeek());
                        workDetailsPOJO.setTodayPrice(wds.get(i).getTodayPrice());
                        workDetailsPOJO.setTimeSlots(timeSlots1);
                        workDetailsPOJOS.add(workDetailsPOJO);
                    }
                }
                odp.setWorkDetails(workDetailsPOJOS);
                List<Integer> noteIds = CommonUtils.stringToList(odp.getNoteIds());
                if(CollectionUtils.isNotEmpty(noteIds)){
                    List<SysJobNote> sysJobNotes = sysJobNoteService.listByIds(noteIds);
                    parent.setNotes(sysJobNotes);
                }

                List<Integer> jobIds = CommonUtils.stringToList(odp.getJobIds());
                List<SysJobContend> jobs = sysJobContendService.listByIds(jobIds);
                parent.setJobs(jobs);

                EmployeesDetails ed = employeesDetailsService.getById(odp.getEmployeesId());
                CustomerDetails cd = customerDetailsService.getById(odp.getCustomerId());
                if (CommonUtils.isNotEmpty(ed)){
                    /* 保洁员头像二次加工处理 */
                    parent.setEmployeesHeadUrl(ed.getHeadUrl());
                    /* 保洁员地址二次加工 */
                    parent.setAddressEmployees(ed.getAddress2()+ed.getAddress3()+ed.getAddress4());
                    parent.setLatEmployees(new Float(ed.getLat()));
                    parent.setLngEmployees(new Float(ed.getLng()));
                }
                if (CommonUtils.isNotEmpty(cd)){
                    /* 客户头像二次加工处理 */
                    parent.setCustomerHeadUrl(cd.getHeadUrl());
                }

                return R.ok(odp);
            }
            return R.failed(null, "訂單不存在");
        }else {
            Set<String> keys2 = redisTemplate.keys("OrderToBePaid:companyId*:" + number);
            if(CollectionUtils.isNotEmpty(keys2)){
                Object[] keysArr = keys2.toArray();
                String key = keysArr[0].toString();
                TokenOrderParent odp = null;
                Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
                try {
                    odp = (TokenOrderParent) CommonUtils.mapToObject(map, TokenOrderParent.class);
                    return R.ok(odp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                QueryWrapper<TokenOrder> qw = new QueryWrapper<>();
                qw.eq("number",number);
                TokenOrder one = tokenOrderService.getOne(qw);
                return R.ok(one);
            }
        }
        return null;
    }

    @Override
    public List<OrderDetailsPOJO> getOrderByEmpId(Integer empId) {
        /* type = 0全部 1待付款 2待服务 3进行中 4待评价 5已完成 */
        List<Integer> empIds = new ArrayList<>();
        empIds.add(empId);

        List<OrderDetailsPOJO> res = new ArrayList<>();
        res.addAll(this.order1ByEmployeesAll(empIds));
        res.addAll(this.order2ByEmployeesAll(empIds));
        res.addAll(this.order3ByEmployeesAll(empIds));
        res.addAll(this.order4ByEmployeesAll(empIds));
        res.addAll(this.order5ByEmployeesAll(empIds));
        SortListUtil<OrderDetailsPOJO> sort = new SortListUtil<>();
        sort.Sort(res,"getStartDateTime","desc");
        return res;
    }

    public List<TokenOrderParent> order1ByCompany(Integer companyId){
        List<TokenOrderParent> pojoList = new ArrayList<>();
            Set<String> keys = redisTemplate.keys("OrderToBePaid:companyId"+companyId+"*");
            Object[] keysArr = keys.toArray();
            for (int i = 0; i < keysArr.length; i++) {
                String key = keysArr[i].toString();
                TokenOrderParent odp = null;
                Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
                try {
                    odp = (TokenOrderParent) CommonUtils.mapToObject(map, TokenOrderParent.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                pojoList.add(odp);//
            }
        return pojoList;
    }

    public List<TokenOrderParent> order5ByCompany(Integer companyId){
            QueryWrapper<TokenOrder> qw = new QueryWrapper();
            qw.eq("company_id", companyId);
        List<TokenOrderParent> collect = tokenOrderService.list(qw).stream().map(x -> {
            TokenOrderParent tokenOrderParent = new TokenOrderParent(x);
            return tokenOrderParent;
        }).collect(Collectors.toList());
        return collect;
    }

}
