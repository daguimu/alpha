package com.guimu.alpha.interceptor;

import com.guimu.alpha.dto.ReLogDto;
import com.guimu.alpha.service.ReLogDtoStrConventor;
import com.guimu.alpha.utils.ThreadUtils;
import java.util.StringJoiner;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * @description: http拦截器
 * @author: Guimu
 * @create: 2018/08/13 01:57:44
 **/
@Component
public class RequestInterceptor extends HandlerInterceptorAdapter implements ReLogDtoStrConventor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler)
        throws Exception {
        /* //TODO 做是否登录验证，验证通过返回true。验证失败返回false。
         */
        this.setTicket(request);
//        LOGGER.info("请求访问接口:" + request.getRequestURL());
        return true;
    }

    private void setTicket(HttpServletRequest request) {
        StringJoiner stringJoiner = new StringJoiner("-", "", "");
        stringJoiner.add(request.getHeader("userId"))
            .add(request.getHeader("batchNo"))
            .add(request.getHeader("levelStr"));

        ReLogDto reLogDto = toReLogFromStr(stringJoiner.toString());
        if (checkIsEmpty(reLogDto)) {
            return;
        }
        ThreadUtils.threadLocal.set(reLogDto);
    }
}


