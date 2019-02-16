package com.guimu.alpha.config;

import com.guimu.alpha.interceptor.RequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @description: 对requestMapping方法进行拦截, 除了登陆方法
 * @author: Guimu
 * @create: 2018/04/18 18:52:16
 **/
@Configuration
public class WebMvcAppConfig extends WebMvcConfigurerAdapter {

    @Autowired
    RequestInterceptor requestInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestInterceptor).addPathPatterns("/**");
        super.addInterceptors(registry);
    }
}


