package com.housekeeping.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.PayToken;
import com.housekeeping.admin.entity.*;
import com.housekeeping.admin.mapper.TokenOrderMapper;
import com.housekeeping.admin.pojo.OrderDetailsPOJO;
import com.housekeeping.admin.pojo.OrderDetailsParent;
import com.housekeeping.admin.pojo.OrderPhotoPOJO;
import com.housekeeping.admin.pojo.WorkDetailsPOJO;
import com.housekeeping.admin.service.*;
import com.housekeeping.common.utils.*;
import ecpay.payment.integration.AllInOne;
import ecpay.payment.integration.domain.AioCheckOutOneTime;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service("tokenOrderService")
public class TokenOrderServiceImpl extends ServiceImpl<TokenOrderMapper, TokenOrder> implements TokenOrderService {

    @Resource
    private IOrderIdService orderIdService;
    @Resource
    private ISysConfigService sysConfigService;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private ICompanyDetailsService companyDetailsService;

    @Override
    public R payToken(PayToken dto) {

        Integer companyIdByUserId = companyDetailsService.getCompanyIdByUserId(TokenUtils.getCurrentUserId());

        LocalDateTime now = LocalDateTime.now();
        TokenOrderParent order = new TokenOrderParent();

        /* 订单编号 */
        Long number = orderIdService.generateId();
        order.setNumber(number.toString());

        /* 消费项目 */
        order.setConsumptionItems(dto.getTokens()+"個代幣");

        /* 公司id */
        order.setCompanyId(companyIdByUserId.toString());

        /* 代筆數 */
        order.setTokens(dto.getTokens());

        /* 價格 */
        order.setPrice(dto.getPrice());

        /* 訂單狀態 */
        order.setOrderState(CommonConstants.ORDER_STATE_TO_BE_PAID);

        /* 支付時間 */
        order.setPayDateTime(LocalDateTime.now());

        /* 支付方式 */
        order.setPayType(dto.getPayType());


        /* 订单截止付款时间 保留时间 */
        QueryWrapper qw = new QueryWrapper();
        qw.eq("config_key", ApplicationConfigConstants.orderRetentionTime);
        SysConfig config = sysConfigService.getOne(qw);
        LocalDateTime payDeadline = now.plusHours(Long.parseLong(config.getConfigValue()));
        order.setPayDeadline(payDeadline);
        int hourly = Integer.parseInt(config.getConfigValue());
        order.setH(hourly);

        String key = "OrderToBePaid:companyId"+dto.getCompanyId()+":" + number;
        Map<String, Object> map = new HashMap<>();
        try {
            map = CommonUtils.objectToMap(order);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        redisTemplate.opsForHash().putAll(key, map);
        redisTemplate.expire(key, hourly, TimeUnit.HOURS);

        return R.ok(order, "预约成功");
    }

    @Override
    public R pay(Long number, String payType) {
        Integer companyIdByUserId = companyDetailsService.getCompanyIdByUserId(TokenUtils.getCurrentUserId());
        String key = "OrderToBePaid:companyId" + companyIdByUserId + ":" + number;
        redisTemplate.opsForHash().put(key, "payType", payType);
        redisTemplate.opsForHash().put(key, "orderState", CommonConstants.ORDER_STATE_PAYMENT_PROCESSING);
        return R.ok(null, "修改成功");
    }

    @Override
    public R inputSql(String number) {
        /* odp 获取订单信息 */
        Integer companyIdByUserId = companyDetailsService.getCompanyIdByUserId(TokenUtils.getCurrentUserId());
        Set<String> keys = redisTemplate.keys("OrderToBePaid:companyId"+companyIdByUserId+":" + number);
        if (keys.isEmpty()) return R.failed(null, "訂單編號不存在于redis");
        Object[] keysArr = keys.toArray();
        String key = keysArr[0].toString();
        redisTemplate.delete(key);
        return R.ok(number, "操作成功");
    }

    @Override
    public String cardPayByToken(String number, String callBackUrl) {

        //获取订单详情
        TokenOrderParent odp = (TokenOrderParent) this.getOrder(number).getData();

        //订单状态判断_是否是未支付状态
        Boolean isNoPay = odp.getOrderState().equals(2);
        if (!isNoPay) return "待支付訂單："+number+" 不存在";

        //生成支付页面
        String doc = this.odpToPaymentPage(odp, callBackUrl);

        //返回支付页面
        return doc;
    }

    public R getOrder(String number) {
        /* 查redis */
        Set<String> keys = redisTemplate.keys("OrderToBePaid:companyId*:" + number);
        if (!keys.isEmpty()) {
            Object[] keysArr = (Object[]) keys.toArray();
            String key = keysArr[0].toString();
            TokenOrderParent odp = null;
            Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
            try {
                odp = (TokenOrderParent) CommonUtils.mapToObject(map, TokenOrderParent.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return R.ok(odp);
        }
        else {
            /* 查數據庫 */
            QueryWrapper qw = new QueryWrapper();
            qw.eq("number", number);
            TokenOrder od = this.getOne(qw);
            if(od==null){
                return R.failed("訂單不存在");
            }
            TokenOrderParent odp = new TokenOrderParent(od);
            return R.ok(odp);
        }
    }

    public String odpToPaymentPage(TokenOrderParent odp, String callBackUrl) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        String nowP = dtf2.format(now);
        String price = odp.getPrice().toString();

        AllInOne all = new AllInOne("");
        AioCheckOutOneTime obj = new AioCheckOutOneTime();
        obj.setMerchantTradeNo(odp.getNumber());
        obj.setMerchantTradeDate(nowP);
        obj.setTotalAmount(price);

        obj.setTradeDesc("訂單來源，代幣購買");
        obj.setItemName("代幣"+odp.getTokens());


        obj.setReturnURL(callBackUrl);
        obj.setNeedExtraPaidInfo("N");
        obj.setRedeem("Y");
        String form = all.aioCheckOut(obj, null);
        return form;
    }
}
