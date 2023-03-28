// Copyright (C) 2023 Baidu Inc. All rights reserved.

import com.baidu.acg.imt.rec.common.remote.EndpointProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 2020/12/2 10:06
 *
 * @author Cynric Shu
 */
@Data
@ConfigurationProperties("grpc.server")
public class GrpcServerProperties extends EndpointProperties {

    private boolean enableKeepAlive = false;

    private long keepAliveTimeMs = 60 * 1000L;

    private long keepAliveTimeoutMs = 20 * 1000L;

    /**
     * Whether gRPC health service is enabled or not. Defaults to {@code true}.
     *
     * @param healthServiceEnabled Whether gRPC health service is enabled.
     * @return True, if the health service is enabled. False otherwise.
     */
    private boolean healthServiceEnabled = true;

    /**
     * Whether proto reflection service is enabled or not. Defaults to {@code true}.
     *
     * @param reflectionServiceEnabled Whether gRPC reflection service is enabled.
     * @return True, if the reflection service is enabled. False otherwise.
     */
    private boolean reflectionServiceEnabled = true;
}