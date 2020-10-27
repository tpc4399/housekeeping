package com.housekeeping.common.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.housekeeping.common.entiity.HkUser;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
                        hkUser.getAuthType().toString()
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
        return new HkUser();
    }

//
//    public static void main(String[] args) {
//        TokenUtils.getToken(new HkUser());
//    }
}
