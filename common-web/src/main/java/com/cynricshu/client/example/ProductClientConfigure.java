// Copyright (C) 2023 Baidu Inc. All rights reserved.

import com.baidu.acg.feed.client.SimpleHttpClientFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 2020/8/3 22:30
 *
 * @author Cynric Shu
 */
@Configuration
@EnableConfigurationProperties(ProductClientProperties.class)
@Slf4j
public class ProductClientConfigure {
    @Bean
    public ProductClient productClient(ProductClientProperties productClientProperties) {
        return new SimpleHttpClientFactory()
                .createRetrofitService(
                        productClientProperties, ProductClient.class);
    }
}