package com.guimu.alpha.config;

import com.guimu.alpha.consts.ThreadLocalConstEnum;
import com.guimu.alpha.dto.ReLogDto;
import com.guimu.alpha.runable.ReLogSpanContinuingTraceRunnable;
import com.guimu.alpha.service.ReLogDtoStrConventor;
import com.guimu.alpha.utils.ThreadUtils;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.Executor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.cloud.sleuth.DefaultSpanNamer;
import org.springframework.cloud.sleuth.SpanNamer;
import org.springframework.cloud.sleuth.TraceKeys;
import org.springframework.cloud.sleuth.Tracer;

/**
 * @Description:
 * @Author: Guimu
 * @Create: 2019/02/18 14:23:22
 **/

public class ReLogLazyTraceExecutor implements Executor, ReLogDtoStrConventor {

    private static final Log log = LogFactory.getLog(MethodHandles.lookup().lookupClass());
    private Tracer tracer;
    private final BeanFactory beanFactory;
    private final Executor delegate;
    private TraceKeys traceKeys;
    private SpanNamer spanNamer;

    public ReLogLazyTraceExecutor(BeanFactory beanFactory, Executor delegate) {
        this.beanFactory = beanFactory;
        this.delegate = delegate;
    }

    public void execute(Runnable command) {
        if (this.tracer == null) {
            try {
                this.tracer = (Tracer) this.beanFactory.getBean(Tracer.class);
            } catch (NoSuchBeanDefinitionException var3) {
                this.delegate.execute(command);
                return;
            }
        }
        ReLogDto reLogDto = ThreadUtils.threadLocal.get();
        if (!checkIsEmpty(reLogDto)) {
            this.tracer.getCurrentSpan()
                .setBaggageItem(ThreadLocalConstEnum.RE_LOG_BATCH_NO.getDescStr(),
                    this.toStrFromReLogDto(reLogDto));
        }

//        this.delegate.execute(
//            new SpanContinuingTraceRunnable(this.tracer, this.traceKeys(), this.spanNamer(),
//                command));
        this.delegate.execute(
            new ReLogSpanContinuingTraceRunnable(this.tracer, this.traceKeys(), this.spanNamer(),
                command));
    }

    private TraceKeys traceKeys() {
        if (this.traceKeys == null) {
            try {
                this.traceKeys = (TraceKeys) this.beanFactory.getBean(TraceKeys.class);
            } catch (NoSuchBeanDefinitionException var2) {
                log.warn("TraceKeys bean not found - will provide a manually created instance");
                return new TraceKeys();
            }
        }
        return this.traceKeys;
    }

    private SpanNamer spanNamer() {
        if (this.spanNamer == null) {
            try {
                this.spanNamer = (SpanNamer) this.beanFactory.getBean(SpanNamer.class);
            } catch (NoSuchBeanDefinitionException var2) {
                log.warn("SpanNamer bean not found - will provide a manually created instance");
                return new DefaultSpanNamer();
            }
        }

        return this.spanNamer;
    }
}

