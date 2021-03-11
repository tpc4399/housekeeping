package com.housekeeping.admin.auth.aspect;

import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.utils.CommonConstants;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.util.Arrays;
import java.util.List;


/**
 * @Author su
 * @Date 2021/3/11 17:38
 */
@Aspect
public class AccessAspect {

    @Around("@annotation(access)")
    @SneakyThrows
    public Object around(ProceedingJoinPoint point, Access access){
        RolesEnum[] rolesEnum = access.value();
        List<RolesEnum> rolesEnums = Arrays.asList(rolesEnum.clone());
        String roleType = TokenUtils.getRoleType();
        RolesEnum roleEnum = CommonConstants.ROLES_ENUM_MAP.get(roleType);
        if (rolesEnums.contains(roleEnum)){
            return point.proceed();
        }else {
            return R.failed(null, "The role is not eligible to access the interface");
        }
    }

}
