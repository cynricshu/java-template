package com.cynricshu.service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cynricshu.exception.ThirdPartyException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 2019/10/21 14:51
 *
 * @author Cynric Shu
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WechatService {
    private static final String WECHAT_SERVER_URL = "https://api.weixin.qq.com";
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public byte[] downloadVoice(String mediaId) {
        return makeCall(mediaId).bytes();
    }

    @SneakyThrows
    private ResponseBody makeCall(String mediaId) {
        String accessToken = "accessToken";

        String requestUrl = WECHAT_SERVER_URL + "/cgi-bin/media/get?access_token="
                + accessToken + "&media_id=" + mediaId;

        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(2, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder().url(requestUrl).build();
        Response response = null;

        int retryCount = 0;
        while (retryCount < 2) {
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                throw new ThirdPartyException("failed to connect to wechat server");
            }

            if (response.isSuccessful()) {
                String contentType = response.header("Content-Type");

                if (contentType.contains("audio/amr")) {
                    break; // 如果拿到了音频，直接跳出重试循环
                }

                if (contentType.contains("application/json")) {
                    WechatErrBody wechatErrBody = objectMapper.readValue(response.body().string(), WechatErrBody.class);
                    if (wechatErrBody.errcode == 40001) { // token 过期了，刷新 token 并重试
                        log.warn("token expired, will refresh and retry");
                        retryCount++;
                    } else {
                        // 如果不是 40001 的错误码，那么重试也没用，程序处理不了，直接抛异常
                        log.error("failed to get file from wechat server, response body: {}", wechatErrBody);
                        throw new ThirdPartyException(
                                "failed to get file from wechat server, unexpected errcode " + wechatErrBody.errcode);
                    }
                } else {
                    // 如果 content-type 既不是音频，又不是 json，重试也没用，直接打log抛异常
                    log.error("failed to get file from wechat server, unexpected response: {}",
                            response.body().string());
                    throw new ThirdPartyException(
                            "failed to get file from wechat server, unexpected content-type " + contentType);
                }
            } else {
                log.error("http status is not 2xx, response: {}", response);
                retryCount++;
            }
        }
        return response.body();
    }

    @Data
    public static class WechatErrBody {
        private int errcode;
        private String errmsg;
    }
}