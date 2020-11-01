package com.housekeeping.common.logs.utils;

import cn.hutool.core.util.URLUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.HttpUtil;
import com.housekeeping.admin.entity.Log;
import com.housekeeping.common.utils.TokenUtils;
import lombok.experimental.UtilityClass;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;


/**
 * @Author su
 * @create 2020/10/30 14:50
 */
@UtilityClass
public class LogUtils {
	public Log getSysLog() {
		HttpServletRequest request = ((ServletRequestAttributes) Objects
			.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
		Log log = new Log();
		log.setLastReviserId(TokenUtils.getCurrentUserId());
		log.setRemoteAddr(ServletUtil.getClientIP(request));
		log.setRequestUri(URLUtil.getPath(request.getRequestURI()));
		log.setMethod(request.getMethod());
		log.setUserAgent(request.getHeader("user-agent"));
		log.setParams(HttpUtil.toParams(request.getParameterMap()));

		return log;
	}
}
