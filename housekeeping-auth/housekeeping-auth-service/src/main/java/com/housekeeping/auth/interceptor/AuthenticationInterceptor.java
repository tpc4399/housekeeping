package com.housekeeping.auth.interceptor;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.JWT;
import com.housekeeping.auth.annotation.PassToken;
import com.housekeeping.auth.service.impl.HkUserService;
import com.housekeeping.auth.utils.HkUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * token认证拦截器
 * @Author su
 * @create 2020/10/26 22:57
 */
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Autowired
    private HkUserService hkUserService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        /* 权限越过检查, @PassToken监听 */
        if (method.isAnnotationPresent(PassToken.class)){
            PassToken passToken = method.getAnnotation(PassToken.class);
            if (passToken.require()){
                return true;
            }
        }
        /* 认证过程 */
        if (token == null || "".equals(token)){
            throw new RuntimeException("null token, please again");
        }

        List<String> audience = new ArrayList<>();
        HkUser hkUser = null;
        try {
            audience = JWT.decode(token).getAudience();
            //手机号不能为空
            if (audience.get(1) == null){
                throw new RuntimeException("token waring, null phone, please again");
            }
            //登录类型需要符合规范
            if (!"0".equals(audience.get(2)) && !"1".equals(audience.get(2)) && !"2".equals(audience.get(2))){
                throw new RuntimeException("token waring, error authType, please again");
            }
        }catch (JWTDecodeException j){
            throw new RuntimeException("token error, please again");
        }
        if ("0".equals(audience.get(2))){
            //email+password登入方式
            hkUser = hkUserService.byEmail(audience.get(0));
        }else if ("1".equals(audience.get(2))){
            //phone+password登入方式
            hkUser = hkUserService.byPhone(audience.get(1));
        }else if ("2".equals(audience.get(2))){
            //phone+code登入方式
            hkUser = hkUserService.byPhone(audience.get(1));
        }else {
            throw new RuntimeException("authType error, please again");
        }
        return false;
    }
}
