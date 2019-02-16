package com.guimu.alpha.appender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Layout;
import com.guimu.alpha.model.LogEsBase;
import com.guimu.alpha.service.EsService;
import com.guimu.alpha.utils.RedisUtils;
import com.guimu.alpha.utils.ThreUtils;
import lombok.Data;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @Description: 自定义appender
 * @Author: Guimu
 * @Create: 2019/02/15 22:12:26
 **/
@Data
@Service
public class LogAppender extends AppenderBase<ILoggingEvent> implements ApplicationContextAware {

    private static ApplicationContext applicationContext;
    private EsService esService;
    private RedisUtils redisUtils;
    Layout<ILoggingEvent> layout;

    @Override
    protected void append(ILoggingEvent event) {
        this.init();
        String txt = this.layout.doLayout(event);
        LogEsBase logEsBase = new LogEsBase();
        logEsBase.setLogMsg(txt);
        String val = ThreUtils.threadLocal.get();
        if (StringUtils.isEmpty(val)) {
            return;
        }
        boolean flag = false;
        logEsBase.setUserId(val);
        if (!StringUtils.isEmpty(redisUtils.get("tokenId:" + val))) {
            flag = esService.addOne(logEsBase);
        }
        System.out.println(flag);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        LogAppender.applicationContext = applicationContext;
    }

    private void init() {
        if (redisUtils == null) {
            redisUtils = (RedisUtils) applicationContext.getBean("RedisUtils");
        }
        if (esService == null) {
            esService = (EsService) applicationContext.getBean("EsServiceImpl");
        }
    }
}