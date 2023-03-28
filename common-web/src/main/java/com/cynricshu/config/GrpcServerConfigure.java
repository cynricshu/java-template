// Copyright (C) 2023 Baidu Inc. All rights reserved.

import io.grpc.BindableService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 2020/12/2 09:56
 *
 * @author Cynric Shu
 */
@Configuration
@ConditionalOnProperty(name = "grpc.server.enabled", havingValue = "true")
@EnableConfigurationProperties(GrpcServerProperties.class)
public class GrpcServerConfigure {

    @Bean
    public GrpcServerLifecycle grpcServerLifecycle(
            List<BindableService> bindableServiceList, GrpcServerProperties grpcServerProperties) {
        return new GrpcServerLifecycle(bindableServiceList, grpcServerProperties);
    }
}