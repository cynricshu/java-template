package com.cynricshu.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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
public class ClientIpFilterConfig {
    public static final String HEADER_CLIENT_IP = "ClientIp";

    @Value("${logging.requestId_urlPattern:/*}")
    private String urlPattern;

    @Bean
    public FilterRegistrationBean clientIpFilterRegistrationBean() {
        ClientIpFilter filter = new ClientIpFilter();
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(filter);
        List<String> urlPatterns = Arrays.asList(urlPattern.split(";"));
        registrationBean.setUrlPatterns(urlPatterns);
        return registrationBean;
    }

    @Slf4j
    private static class ClientIpFilter implements Filter {
        private ThreadLocal<String> threadLocalClientIp = new ThreadLocal<>();

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            preHandle((HttpServletRequest) request, (HttpServletResponse) response);
            chain.doFilter(request, response);
            afterCompletion((HttpServletRequest) request, (HttpServletResponse) response);
        }

        private void preHandle(HttpServletRequest request, HttpServletResponse response) {
            String clientIp = request.getHeader(HEADER_CLIENT_IP);
            if (clientIp == null) {
                clientIp = request.getRemoteAddr();
                log.debug("client ip is null, will use getRemoteAddr");
            }

            threadLocalClientIp.set(clientIp);
            MDC.put(HEADER_CLIENT_IP, clientIp);
        }

        private void afterCompletion(HttpServletRequest request, HttpServletResponse response) {
            MDC.remove(HEADER_CLIENT_IP);
            threadLocalClientIp.remove();
        }

        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
        }

        @Override
        public void destroy() {

        }
    }
}
