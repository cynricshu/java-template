package com.cynricshu.client;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cynricshu.config.OkHttpConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

@Component
public class RetrofitFactory {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OkHttpConfig okHttpConfig;

    private Retrofit createRetrofit(HttpUrl url) {
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

    public <T> T createClient(String serverHost, int serverPort, Class<T> client) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host(serverHost)
                .port(serverPort)
                .build();

        Retrofit retrofit = createRetrofit(url);

        return retrofit.create(client);
    }
}
