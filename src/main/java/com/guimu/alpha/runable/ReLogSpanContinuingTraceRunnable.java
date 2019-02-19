package com.guimu.alpha.runable;

import com.guimu.alpha.consts.ThreadLocalConstEnum;
import com.guimu.alpha.service.ReLogDtoStrConventor;
import com.guimu.alpha.utils.ThreadUtils;
import java.lang.ref.Reference;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.SpanNamer;
import org.springframework.cloud.sleuth.TraceKeys;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.instrument.async.SpanContinuingTraceRunnable;

/**
 * @Description:
 * @Author: Guimu 主要重写run方法,以加入initReLogThreadLocal()方法,从异步线程中提取变量数据到线程变量中
 * @Create: 2019/02/18 22:07:32
 **/

public class ReLogSpanContinuingTraceRunnable extends SpanContinuingTraceRunnable implements
    ReLogDtoStrConventor {

    public ReLogSpanContinuingTraceRunnable(Tracer tracer,
        TraceKeys traceKeys,
        SpanNamer spanNamer, Runnable delegate) {
        super(tracer, traceKeys, spanNamer, delegate);
    }

    public ReLogSpanContinuingTraceRunnable(Tracer tracer,
        TraceKeys traceKeys, SpanNamer spanNamer, Runnable delegate, String name) {
        super(tracer, traceKeys, spanNamer, delegate, name);
    }

    @Override
    public void run() {
        Span span = this.startSpan();
        try {
            initReLogThreadLocal();
            this.getDelegate().run();
        } finally {
            this.close(span);
        }
    }

    private void initReLogThreadLocal() {
        try {
            Thread thread = Thread.currentThread();
            Field threadLocalsField = Thread.class.getDeclaredField("threadLocals");
            threadLocalsField.setAccessible(true);
            Object threadLocalTable = threadLocalsField.get(thread);
            Class threadLocalMapClass = Class.forName(threadLocalTable.getClass().getName());
            Field tableField = threadLocalMapClass.getDeclaredField("table");
            tableField.setAccessible(true);
            Object table = tableField.get(threadLocalTable);
            Field referentField = Reference.class.getDeclaredField("referent");
            referentField.setAccessible(true);
            int len = Array.getLength(table);
            for (int i = 0; i < len; i++) {
                Object entry = Array.get(table, i);
                if (entry != null) {
                    ThreadLocal threadLocal = (ThreadLocal) referentField.get(entry);
                    if (threadLocal != null && threadLocal.get() != null && threadLocal.get()
                        .getClass().getName().contains("SpanContext")) {
                        Object o = threadLocal.get();
                        Field field = Class.forName(o.getClass().getName())
                            .getDeclaredField("span");
                        field.setAccessible(true);
                        Span span = (Span) field.get(o);
                        span.getBaggage().forEach((k, v) -> {
                            if (k.equalsIgnoreCase(
                                ThreadLocalConstEnum.RE_LOG_BATCH_NO.getDescStr())) {
                                //如果该线程变量中已经有对应的值,将旧的值移除掉放入新的值,以此避免不同的请求复用线程池线程的时候复用线程变量
                                if (!this.checkIsEmpty(ThreadUtils.threadLocal.get())) {
                                    ThreadUtils.threadLocal.remove();
                                }
                                ThreadUtils.threadLocal.set(this.toReLogFromStr(v));
                            }
                        });
                    }
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
