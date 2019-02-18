package com.guimu.alpha.tractor;

import com.guimu.alpha.dto.ReLogDto;
import com.guimu.alpha.serviceimpl.ReLogDtoStrConventor;
import com.guimu.alpha.utils.ThreadUtils;
import java.util.Iterator;
import java.util.Map.Entry;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.SpanTextMap;
import org.springframework.cloud.sleuth.instrument.web.HttpSpanExtractor;
import org.springframework.cloud.sleuth.instrument.web.HttpSpanInjector;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author: Guimu
 * @Create: 2019/02/16 20:53:27
 **/
@Component
public class ParamTractor implements HttpSpanInjector, HttpSpanExtractor, ReLogDtoStrConventor {

    private final String LOG_KEY = "re_log_batch_no_key";

    @Override
    public void inject(Span span, SpanTextMap carrier) {
        ReLogDto curThreadReg = ThreadUtils.threadLocal.get();
        if (this.checkIsEmpty(curThreadReg)) {
            return;
        }
        carrier.put(LOG_KEY, toStrFromReLogDto(curThreadReg));
    }

    @Override
    public Span joinTrace(SpanTextMap entries) {
        Iterator<Entry<String, String>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Entry<String, String> tempEntry = iterator.next();
            if (tempEntry.getKey().equalsIgnoreCase(LOG_KEY)) {
                ReLogDto reLogDto = toReLogFromStr(tempEntry.getValue());
                ThreadUtils.threadLocal.set(reLogDto);
                break;
            }
        }
        return null;
    }
}
