package com.cynricshu.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CorsConfiguration
 */
@Configuration
public class CorsConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")    // //匹配访问的路径
                .allowedMethods("POST", "GET")    // 匹配访问的方法
                .allowedOrigins("*")    // 匹配允许跨域访问的源
                .allowedHeaders("*")    // 匹配允许头部访问
                .allowCredentials(true);
    }
}
