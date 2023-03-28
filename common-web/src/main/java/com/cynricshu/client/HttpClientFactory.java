package com.cynricshu.client;

import com.baidu.acg.feed.common.Constants;
import com.baidu.acg.feed.common.domain.property.EndpointProperties;
import com.baidu.acg.feed.common.util.JsonUtil;
import com.baidu.acg.feed.exception.ThirdPartyException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.slf4j.MDC;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
public class SimpleHttpClientFactory {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SimpleHttpClientFactory() {
        JsonUtil.configureObjectMapper(this.objectMapper);
    }

    public SimpleHttpClientFactory(Consumer<ObjectMapper> configureFunc) {
        JsonUtil.configureObjectMapper(this.objectMapper);
        this.postConfigureObjectMapper(configureFunc);
    }

    /**
     * 允许使用者为 objectMapper 配置自定义的序列化/反序列化规则
     * 注意：可以引入新配置，但是不要修改原有的配置，否则可能会带来问题
     *
     * @param configureFunc
     */
    public void postConfigureObjectMapper(Consumer<ObjectMapper> configureFunc) {
        configureFunc.accept(objectMapper);
    }

    private Retrofit createRetrofit(HttpUrl url, EndpointProperties endpointProperties) {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(endpointProperties.getConnectTimeoutMs(), TimeUnit.MILLISECONDS)
                .readTimeout(endpointProperties.getReadTimeoutMs(), TimeUnit.MILLISECONDS)
                .writeTimeout(endpointProperties.getWriteTimeoutMs(), TimeUnit.MILLISECONDS)
                .addNetworkInterceptor(new HttpHeaderTraceIdInterceptor())
                .build();

        return new Retrofit.Builder()
                .baseUrl(url)
                .addCallAdapterFactory(new ResultCallAdaptorFactory())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(ResultConverterFactory.create(objectMapper))
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .client(httpClient)
                .build();
    }

    public <T> T createRetrofitService(EndpointProperties endpointProperties, Class<T> service) {
        log.info("start to create client {}, properties={}", service.getCanonicalName(), endpointProperties);

        if (endpointProperties.getHost().equals(EndpointProperties.DEFAULT_HOST)) {
            // 如果用户没有在配置文件中配置 host 的值，则去判断 ip 的值
            if (!endpointProperties.getIp().equals(EndpointProperties.DEFAULT_HOST)) {
                // 如果 ip 不等于默认值（说明用户设置过了），则把 ip 赋值给 host
                log.info("change host value from {}, to {}",
                        endpointProperties.getHost(), endpointProperties.getIp());
                endpointProperties.setHost(endpointProperties.getIp());
            }
        }

        HttpUrl url = new HttpUrl.Builder()
                .scheme(endpointProperties.getSchema())
                .host(endpointProperties.getHost())
                .port(endpointProperties.getPort())
                .build();

        Retrofit retrofit = createRetrofit(url, endpointProperties);

        return retrofit.create(service);
    }

    /**
     * 2020.08.12 该方法已不推荐使用，通过 retrofit 自带的 interceptor 和 callAdaptor 实现了同样的效果。
     * 并且新的方式可以少写一个interface，请参考 feed-recommend-client 模块里的新用法
     * <p>
     * 这个动态代理将 ***Client 代理到 Retrofit 创建出来的 ***RetrofitService 上
     * 请注意看下***Client 和 ***RetrofitService 的区别。
     * <p>
     * 好处如下：
     * 1. 调用者不用处理任何 Retrofit 相关的东西了，比如 call.execute().body()
     * <p>
     * 2. 由于中间多了一层，也可以做一些通用的操作
     * 比如在每个请求头里带上线程的 traceId
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public <T, U> T createClientProxy(EndpointProperties endpointProperties, Class<T> clientClass,
                                      Class<U> retrofitProxyClass) {
        U retrofitService = createRetrofitService(endpointProperties, retrofitProxyClass);

        return (T) Proxy.newProxyInstance(clientClass.getClassLoader(), new Class<?>[]{clientClass},
                (proxy, method, args) -> {
                    // If the method is a method from Object then defer to normal invocation.
                    if (method.getDeclaringClass() == Object.class) {
                        return method.invoke(this, args);
                    }

                    Method targetMethod = null;

                    Method[] methods = retrofitService.getClass().getMethods();
                    for (Method m : methods) {
                        if (m.getName().equals(method.getName())) {
                            targetMethod = m;
                        }
                    }

                    if (targetMethod == null) {
                        String message = "cannot find Corresponding method [" + method.getName()
                                + "] in retrofitProxyClass, check you code please";
                        log.error(message);
                        throw new RuntimeException(message);
                    }

                    // 在原来参数的基础上，额外加入一个 requestId 参数，外部调用者无感知
                    if (targetMethod.getParameterCount() == args.length + 1) {
                        Object[] newArgs = Arrays.copyOf(args, args.length + 1);
                        newArgs[args.length] = MDC.get(Constants.TRACE_ID);
                        args = newArgs;
                    }

                    Call call = (Call<?>) targetMethod.invoke(retrofitService, args);
                    Response response;
                    try {
                        response = call.execute();
                    } catch (IOException e) {
                        // http 请求由于IO原因没有成功
                        String errorMsg = "failed to connect to server: " + call.request().url();
                        log.error(errorMsg, e);
                        throw new ThirdPartyException(errorMsg);
                    }

                    // 请求成功，并且返回了 200-300 之间的状态码
                    if (response.isSuccessful()) {
                        return response.body();
                    } else {
                        // 返回了诸如 400，500 的状态码
                        String errMsg = "http status code is not 2xx";
                        log.error("{}, response: {}, body: {}", errMsg,
                                response.toString(), response.errorBody().string());
                        throw new ThirdPartyException(errMsg);
                    }
                });
    }
}