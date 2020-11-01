package com.housekeeping.common.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.housekeeping.common.entity.HkUser;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @Author su
 * @create 2020/10/26 23:15
 */
public class TokenUtils {
    /***
     * 生成token
     * @param hkUser
     * @return
     */
    public static String getToken(HkUser hkUser){
//        LocalDateTime now = LocalDateTime.now();
//        //有效期至24h后
//        Long endTime = System.currentTimeMillis() + 1000 * 60 * 60 * 24;
//        LocalDateTime end = new Date(endTime).toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime();

        Date now = new Date();
        long endTimeLong = System.currentTimeMillis() + 1000 * 60 * 60 * 24;//24小时有效时间
//        long endTimeLong = System.currentTimeMillis() + 1000 * 60 * 3;//3分鐘有效时间
        Date end = new Date(endTimeLong);

        if (hkUser.getPhone() == null || "".equals(hkUser.getPhone())
                || hkUser.getAuthType() == null){
            throw new RuntimeException("token生成失败, 登入信息不完整，缺少字段email、phone、authType");
        }

        String token = "";
        Map<String, Object> headerClaims = new HashMap<>();
        headerClaims.put("owner",hkUser.getNickName());

        token = JWT
                .create()
                .withHeader(headerClaims)
                .withAudience(
                        (hkUser.getEmail() == null || "".equals(hkUser.getEmail()))
                                ? "" : hkUser.getEmail(),
                        hkUser.getPhone(),
                        hkUser.getAuthType().toString(),
                        hkUser.getId().toString()
                )
                .withIssuedAt(now)
                .withExpiresAt(end)
                .sign(Algorithm.HMAC256(hkUser.getPassword()));
        return token;
    }

    /***
     * 解析token
     * @param token
     * @return
     */
    public static HkUser parsingToken(String token){
        HkUser hkUser = new HkUser();
        List<String> audience = JWT.decode(token).getAudience();
        hkUser.setEmail(audience.get(0));
        hkUser.setPhone(audience.get(1));
        hkUser.setAuthType(Integer.valueOf(audience.get(2)));
        hkUser.setId(Integer.valueOf(audience.get(3)));
        hkUser.setPassword(hkUser.getPassword());

        return hkUser;
    }

    public static Integer getCurrentUserId(){
        HttpServletRequest request = ((ServletRequestAttributes) Objects
                .requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        if (CommonUtils.isNotEmpty(request.getHeader("Authorization"))){
            HkUser hkUser = TokenUtils.parsingToken(request.getHeader("Authorization"));
            return hkUser.getId();
        }else {
            return -1;
        }
    }
}
