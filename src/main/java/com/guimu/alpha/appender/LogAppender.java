package com.guimu.alpha.appender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Layout;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @Description: 自定义appender
 * @Author: Guimu
 * @Create: 2019/02/15 22:12:26
 **/
@Data
@Service
public class LogAppender extends AppenderBase<ILoggingEvent> {

    Layout<ILoggingEvent> layout;

    @Override
    protected void append(ILoggingEvent event) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://127.0.0.1:9900/hello";
        String txt = this.layout.doLayout(event);
        String restlt = restTemplate
            .getForObject(url + "?msg={1}", String.class, txt);

    }
}
