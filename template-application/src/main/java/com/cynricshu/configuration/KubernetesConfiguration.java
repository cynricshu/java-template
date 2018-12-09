// Copyright (C) 2018 Baidu Inc. All rights reserved.

package com.cynricshu.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.util.Config;
import lombok.SneakyThrows;

/**
 * KubernetesConfiguration
 *
 * @author Xiong Chao(xiongchao@baidu.com)
 */
@Configuration
public class KubernetesConfiguration {
    @SneakyThrows
    @Bean
    public ApiClient apiClient() {
        ApiClient client = Config.defaultClient();
        io.kubernetes.client.Configuration.setDefaultApiClient(client);

        return client;
    }
}
