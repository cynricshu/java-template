import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerServiceDefinition;
import io.grpc.netty.NettyServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;
import io.grpc.services.HealthStatusManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;

/**
 * 2020/11/30 23:17
 *
 * @author Cynric Shu
 */
@Slf4j
public class GrpcServerLifecycle implements SmartLifecycle {
    private final List<BindableService> bindableServiceList;
    private final GrpcServerProperties grpcServerProperties;
    private volatile Server server;

    public GrpcServerLifecycle(List<BindableService> bindableServiceList, GrpcServerProperties grpcServerProperties) {
        this.bindableServiceList = bindableServiceList;
        this.grpcServerProperties = grpcServerProperties;
    }

    @Override
    public void start() {
        NettyServerBuilder serverBuilder = NettyServerBuilder.forPort(grpcServerProperties.getPort());

        // configure keepalive
        if (this.grpcServerProperties.isEnableKeepAlive()) {
            serverBuilder.permitKeepAliveWithoutCalls(true);
            serverBuilder.keepAliveTime(this.grpcServerProperties.getKeepAliveTimeMs(), TimeUnit.MILLISECONDS);
            serverBuilder.keepAliveTimeout(this.grpcServerProperties.getKeepAliveTimeoutMs(), TimeUnit.MILLISECONDS);
        }

        // add grpc services
        if (this.grpcServerProperties.isHealthServiceEnabled()) {
            log.info("add gRPC built-in HealthService");
            serverBuilder.addService(new HealthStatusManager().getHealthService());
        }

        if (this.grpcServerProperties.isReflectionServiceEnabled()) {
            log.info("add gRPC built-in ProtoReflectionService");
            serverBuilder.addService(ProtoReflectionService.newInstance());
        }

        for (BindableService bindableService : bindableServiceList) {
            ServerServiceDefinition serverServiceDefinition = bindableService.bindService();

            log.info("add gRPC service [{}]", serverServiceDefinition.getServiceDescriptor().getName());
            serverBuilder.addService(serverServiceDefinition);
        }

        // build grpc server and start it
        this.server = serverBuilder.build();
        try {
            server.start();
        } catch (IOException e) {
            log.error("Fail to start grpc server", e);
        }
        log.info("Success to start gRPC server on port {}", this.grpcServerProperties.getPort());

        // Prevent the JVM from shutting down while the server is running
        Thread awaitThread = new Thread(() -> {
            try {
                server.awaitTermination();
            } catch (InterruptedException e) {
                log.error("gRPC server waiter interrupted.", e);
                Thread.currentThread().interrupt();
            }
        });
        awaitThread.setName("grpc-server-waiter");
        awaitThread.setDaemon(false);
        awaitThread.start();
    }

    @Override
    public void stop() {
        this.stopAndReleaseGrpcServer();
    }

    /**
     * Initiates an orderly shutdown of the grpc server and releases the references to the server. This call does not
     * wait for the server to be completely shut down.
     */
    protected void stopAndReleaseGrpcServer() {
        final Server localServer = this.server;
        if (localServer != null) {
            localServer.shutdown();
            this.server = null;
            log.info("gRPC server shutdown.");
        }
    }

    @Override
    public boolean isRunning() {
        return this.server != null && !this.server.isShutdown();
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }
}