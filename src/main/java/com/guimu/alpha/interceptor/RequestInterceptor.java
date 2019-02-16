package com.guimu.alpha.interceptor;

import com.guimu.alpha.utils.ThreUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * @description: http拦截器
 * @author: Guimu
 * @create: 2018/08/13 01:57:44
 **/
@Component
public class RequestInterceptor extends HandlerInterceptorAdapter {

    private final Logger LOGGER = LoggerFactory.getLogger(RequestInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler)
        throws Exception {
        /* //TODO 做是否登录验证，验证通过返回true。验证失败返回false。
         */
        String val = request.getHeader("tokenId");
        ThreUtils.threadLocal.set(val);
        LOGGER.info("请求访问接口:" + request.getRequestURL());
        return true;
    }

}


