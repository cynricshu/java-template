package com.cynricshu.client;

import com.cynricshu.common.Constants;
import com.cynricshu.config.OkHttpConfig;
import com.cynricshu.exception.ThirdPartyException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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

@Component
@Slf4j
public class HttpClientFactory {
    @Autowired
    @Setter
    private ObjectMapper objectMapper;

    @Autowired
    @Setter
    private OkHttpConfig initOkHttpConfig;

    private Retrofit createRetrofit(HttpUrl url, OkHttpConfig okHttpConfig) {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(okHttpConfig.getConnectTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(okHttpConfig.getReadTimeout(), TimeUnit.MILLISECONDS)
                .writeTimeout(okHttpConfig.getWriteTimeout(), TimeUnit.MILLISECONDS)
                .build();

        return new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .client(httpClient)
                .build();
    }

    public <T> T createRetrofitService(String serverHost, int serverPort, Class<T> service, OkHttpConfig okHttpConfig) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host(serverHost)
                .port(serverPort)
                .build();

        Retrofit retrofit = createRetrofit(url, okHttpConfig);

        return retrofit.create(service);
    }

    /**
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
    public <T, U> T createClientProxy(String serverIp, int serverPort, Class<T> clientClass,
                                      Class<U> retrofitProxyClass, OkHttpConfig okHttpConfig) {
        if (okHttpConfig == null) {
            okHttpConfig = this.initOkHttpConfig;
        }

        U retrofitService = this.createRetrofitService(serverIp, serverPort, retrofitProxyClass, okHttpConfig);

        return (T) Proxy.newProxyInstance(clientClass.getClassLoader(), new Class<?>[]{clientClass},
                (proxy, method, args) -> {
                    // If the method is a method from Object then defer to normal invocation.
                    if (method.getDeclaringClass() == Object.class) {
                        return method.invoke(this, args);
                    }

                    Method targetMethod = null;

                    var methods = retrofitService.getClass().getMethods();
                    for (Method m : methods) {
                        if (m.getName().equals(method.getName())) {
                            targetMethod = m;
                        }
                    }

                    // 在原来参数的基础上，额外加入一个 requestId 参数，外部调用者无感知
                    Object[] newArgs = Arrays.copyOf(args, args.length + 1);
                    newArgs[args.length] = MDC.get(Constants.TRACE_ID);

                    if (targetMethod == null) {
                        var message = "cannot find Corresponding method [" + method.getName()
                                + "] in retrofitProxyClass, check you code please";
                        log.error(message);
                        throw new RuntimeException(message);
                    }

                    Call call = (Call<?>) targetMethod.invoke(retrofitService, newArgs);
                    Response response;
                    try {
                        response = call.execute();
                    } catch (IOException e) {
                        // http 请求由于IO原因没有成功
                        String errorMsg = "failed to connect to server";
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
