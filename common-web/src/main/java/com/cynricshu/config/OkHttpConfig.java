package com.cynricshu.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "okhttp")
@Data
public class OkHttpConfig {
    private int connectTimeout = 2000;
    private int readTimeout = 2000;
    private int writeTimeout = 2000;
}
