// Copyright (C) 2023 Baidu Inc. All rights reserved.

import com.baidu.acg.feed.common.Constants;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import java.io.IOException;

/**
 * 2020/8/12 16:49
 *
 * @author Cynric Shu
 */
@Slf4j
public class HttpHeaderTraceIdInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        // 如果用户没有显式在 header 里设置 traceid，则尝试从 MDC 中取 traceid
        if (StringUtils.isBlank(request.header(Constants.TRACE_ID))) {
            // 线程池、future 等场景下面，MDC 会丢失，暂时没有太好的办法，网上查到的针对线程池的改造会引入更多问题
            String traceId = MDC.get(Constants.TRACE_ID);

            if (StringUtils.isNotBlank(traceId)) {
                request = chain.request().newBuilder().addHeader(Constants.TRACE_ID, traceId).build();
            } else {
                log.warn("fail to get traceid from MDC to url {}, will lose trace of the calling chain", request.url());
            }
        }

        return chain.proceed(request);
    }
}