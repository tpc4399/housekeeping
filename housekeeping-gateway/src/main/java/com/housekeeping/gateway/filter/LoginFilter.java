package com.housekeeping.gateway.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.gateway.client.AuthClient;
import com.housekeeping.gateway.config.FilterProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Component
@EnableConfigurationProperties({FilterProperties.class})
public class LoginFilter extends ZuulFilter {

    @Autowired
    private FilterProperties filterProp;

    @Autowired
    private AuthClient authClient;

    private static final Logger logger = LoggerFactory.getLogger(LoginFilter.class);

    public LoginFilter() {
        logger.info("过滤器实例化");
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        // 获取上下文
        RequestContext ctx = RequestContext.getCurrentContext();
        // 获取request
        HttpServletRequest req = ctx.getRequest();
        // 获取路径
        String requestURI = req.getRequestURI();
        // 判断白名单
        // 遍历允许访问的路径
        for (String path : this.filterProp.getAllowPaths()) {
            // 然后判断是否是符合
            if(requestURI.startsWith(path)){
                return false;
            }
        }
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        logger.info("拦截器运行");
        // 获取zuul提供的上下文对象
        RequestContext context = RequestContext.getCurrentContext();
        // 从上下文对象中获取请求对象
        HttpServletRequest request = context.getRequest();
        // 获取token信息
        String token = request.getHeader("Authorization");
        // 判断
        if (StringUtils.isBlank(token)) {
            // 过滤该请求，不对其进行路由
            context.setSendZuulResponse(false);
            // 设置响应状态码，401
            context.setResponseStatusCode(HttpStatus.SC_UNAUTHORIZED);
            // 设置响应信息
            context.setResponseBody("{\n\t\"status\":\"401\", \n\t\"text\":\"Forget the token!\"\n}");
            return null;
        }
        /** token信息提取，格式校验 */
        List<String> audience = new ArrayList<>();
        Object user = null;
        try {
            audience = JWT.decode(token).getAudience();
            //手机号不能为空
            if (audience.get(1) == null) {
                throw new RuntimeException("token waring, null phone, please again");
            }
            //登录类型需要符合规范
            if (!"0".equals(audience.get(2)) && !"1".equals(audience.get(2)) && !"2".equals(audience.get(2))) {
                throw new RuntimeException("token waring, error authType, please again");
            }
        } catch (JWTDecodeException j) {
            throw new RuntimeException("token error, please again");
        }
        if ("0".equals(audience.get(2))) {
            //email+password登入方式
            user = authClient.getUserByEmail(audience.get(0), audience.get(4));
        } else if ("1".equals(audience.get(2))) {
            //phone+password登入方式
            user = authClient.getUserByPhone(audience.get(1), audience.get(4));
        } else if ("2".equals(audience.get(2))) {
            //phone+code登入方式
            user = authClient.getUserByPhone(audience.get(1), audience.get(4));
            if (CommonUtils.isNotEmpty(user)) {
                return null;
            }
        } else {
            throw new RuntimeException("authType error, please again");
        }
        if (user == null) {
            throw new RuntimeException("no this user ,please again");
        }
        /** token信息提取，格式校验 */


        /**** 验证token，密码正确性 *****/
        String password = (String) ((LinkedHashMap)user).get("password");
        JWTVerifier jwtVerifier =
                JWT.require(Algorithm.HMAC256(password)).build();
        try {
            jwtVerifier.verify(token);
        } catch (JWTVerificationException e) {
            // 1.token過期
            // 2.密碼錯誤
            // 过滤该请求，不对其进行路由
            context.setSendZuulResponse(false);
            // 设置响应状态码，401
            context.setResponseStatusCode(HttpStatus.SC_UNAUTHORIZED);
            // 设置响应信息
            context.setResponseBody("{\n\t\"status\":\"401\", \n\t\"text\":\"Login failed, please login again\"\n}");
            return null;
        }
        /**** 验证token，密码正确性 *****/
        return null;
    }
}
