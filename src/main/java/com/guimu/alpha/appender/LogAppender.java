package com.guimu.alpha.appender;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Layout;
import com.guimu.alpha.dto.ReLogDto;
import com.guimu.alpha.model.LogEsBase;
import com.guimu.alpha.service.EsService;
import com.guimu.alpha.utils.RedisUtils;
import com.guimu.alpha.utils.ThreadUtils;
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

    private final String LOG_DIR = "log_ticket:";
    private static ApplicationContext applicationContext;
    private EsService esService;
    private RedisUtils redisUtils;
    Layout<ILoggingEvent> layout;

    @Override
    protected void append(ILoggingEvent event) {
        Level targetLevel = Level.toLevel(ThreadUtils.threadLocal.get().getLevelStr());
        //如果当前需要的日志级别比event的日志界别低,则返回
        if (!event.getLevel().isGreaterOrEqual(targetLevel)) {
            return;
        }
        this.init();
        String txt = this.layout.doLayout(event);
        LogEsBase logEsBase = this.reLogDtoConventer(ThreadUtils.threadLocal.get());
        logEsBase.setLogMsg(txt);
        if (StringUtils.isEmpty(logEsBase.getUserId()) || StringUtils
            .isEmpty(logEsBase.getBatchNo())) {
            return;
        }
        if (!StringUtils.isEmpty(redisUtils.get(LOG_DIR + logEsBase.getBatchNo()))) {
            esService.addOne(logEsBase);
        }
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

    private LogEsBase reLogDtoConventer(ReLogDto reLogDto) {
        LogEsBase logEsBase = new LogEsBase();
        logEsBase.setUserId(reLogDto.getUserId());
        logEsBase.setBatchNo(reLogDto.getBatchNo());
        return logEsBase;
    }

}