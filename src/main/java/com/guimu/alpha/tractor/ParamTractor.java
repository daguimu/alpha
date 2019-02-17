package com.guimu.alpha.tractor;

import com.guimu.alpha.dto.ReLogDto;
import com.guimu.alpha.utils.ThreadUtils;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.StringJoiner;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.SpanTextMap;
import org.springframework.cloud.sleuth.instrument.web.HttpSpanExtractor;
import org.springframework.cloud.sleuth.instrument.web.HttpSpanInjector;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @Description:
 * @Author: Guimu
 * @Create: 2019/02/16 20:53:27
 **/
@Component
public class ParamTractor implements HttpSpanInjector, HttpSpanExtractor {

    private final String LOG_KEY = "re_log_batch_no_key";

    @Override
    public void inject(Span span, SpanTextMap carrier) {
        ReLogDto curThreadReg = ThreadUtils.threadLocal.get();
        if (this.checkIsEmpty(curThreadReg)) {
            return;
        }
        StringJoiner stringJoiner = new StringJoiner("-", "", "");
        stringJoiner.add(curThreadReg.getUserId());
        stringJoiner.add(curThreadReg.getBatchNo());
        stringJoiner.add(curThreadReg.getLevelStr());
        carrier.put(LOG_KEY, stringJoiner.toString());
    }

    @Override
    public Span joinTrace(SpanTextMap entries) {
        Iterator<Entry<String, String>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Entry<String, String> tempEntry = iterator.next();
            if (tempEntry.getKey().equalsIgnoreCase(LOG_KEY)) {
                String[] valArr = tempEntry.getValue().split("-");
                ReLogDto reLogDto = new ReLogDto();
                reLogDto.setUserId(valArr[0]);
                reLogDto.setBatchNo(valArr[1]);
                reLogDto.setLevelStr(valArr[2]);
                ThreadUtils.threadLocal.set(reLogDto);
                break;
            }
        }
        return null;
    }

    /**
     * @Author: Guimu
     * @Description: 检查ReLogDto是否合法, 不合法返回true
     * @Param: [reLogDto]
     * @Return: boolean
     * @Date: 2019-02-17 22:41
     */
    private boolean checkIsEmpty(ReLogDto reLogDto) {
        return StringUtils.isEmpty(reLogDto.getLevelStr()) || StringUtils
            .isEmpty(reLogDto.getBatchNo()) || StringUtils.isEmpty(reLogDto.getUserId());
    }
}
