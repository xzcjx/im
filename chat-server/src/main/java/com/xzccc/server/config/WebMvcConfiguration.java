package com.xzccc.server.config;

import com.xzccc.server.interceptor.LoginHandlerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
    @Autowired
    LoginHandlerInterceptor loginHandlerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry
                .addInterceptor(loginHandlerInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/swagger-resources/**", "/webjars/**", "/swagger-ui.html/**")
                .excludePathPatterns("/**/auth/**")
                //                .excludePathPatterns("/**/sign")
                //                .excludePathPatterns("/**/wx/**")
                .order(1);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry
                .addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
