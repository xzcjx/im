package com.xzccc.server.config;

import com.xzccc.server.interceptor.LoginHandlerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
    @Autowired
    LoginHandlerInterceptor loginHandlerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginHandlerInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/**/login")
                .excludePathPatterns("/**/sign").order(1);
    }
}
