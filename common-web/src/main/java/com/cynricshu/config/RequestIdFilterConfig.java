package com.cynricshu.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Configuration
public class RequestIdFilterConfig {
    public static final String REQUEST_ID = "request-id";

    @Value("${logging.requestId_urlPattern:/*}")
    private String urlPattern;

    @Bean
    public FilterRegistrationBean requestIdFilterRegistrationBean() {
        RequestIdFilter filter = new RequestIdFilter();
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(filter);
        List<String> urlPatterns = Arrays.asList(urlPattern.split(";"));
        registrationBean.setUrlPatterns(urlPatterns);
        return registrationBean;
    }

    @Slf4j
    private static class RequestIdFilter implements Filter {
        private ThreadLocal<Long> threadLocalBeginTime = new ThreadLocal<>();
        private ThreadLocal<String> threadLocalRequestId = new ThreadLocal<>();

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            preHandle((HttpServletRequest) request, (HttpServletResponse) response);
            chain.doFilter(request, response);
            afterCompletion((HttpServletRequest) request, (HttpServletResponse) response);
        }

        private void preHandle(HttpServletRequest request, HttpServletResponse response) {
            String requestId = request.getHeader(REQUEST_ID);
            if (requestId == null) {
                requestId = UUID.randomUUID().toString();
            }
            MDC.put(REQUEST_ID, requestId);

            long now = System.currentTimeMillis();
            log.info("[begin] {} {}", request.getMethod(), request.getRequestURI());

            threadLocalBeginTime.set(now);
            threadLocalRequestId.set(requestId);
        }

        private void afterCompletion(HttpServletRequest request, HttpServletResponse response) {
            long requestBeginTime = threadLocalBeginTime.get();
            String requestId = threadLocalRequestId.get();
            response.setHeader(REQUEST_ID, requestId);

            long timeUsed = System.currentTimeMillis() - requestBeginTime;
            log.info("[end] {} {} [status:{},time:{}ms]", request.getMethod(), request.getRequestURI(),
                    response.getStatus(), timeUsed);

            MDC.remove(REQUEST_ID);
            threadLocalRequestId.remove();
            threadLocalBeginTime.remove();
        }

        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
        }

        @Override
        public void destroy() {

        }
    }
}
