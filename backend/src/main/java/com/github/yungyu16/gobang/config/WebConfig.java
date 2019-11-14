package com.github.yungyu16.gobang.config;


import com.github.yungyu16.gobang.annotation.WithoutLogin;
import com.github.yungyu16.gobang.base.LogOperationsBase;
import com.github.yungyu16.gobang.core.SessionOperations;
import com.github.yungyu16.gobang.exeception.BizSessionTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private SessionFilter sessionFilter;

    @Autowired
    private TrackFilter trackFilter;


    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.setAllowCredentials(true);
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**", config);
        return new CorsFilter(configSource);
    }

    @Bean
    public FilterRegistrationBean trackFilter() {
        FilterRegistrationBean<TrackFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(trackFilter);
        registration.addUrlPatterns("/*");
        registration.setName("trackFilter");
        registration.setOrder(1);
        return registration;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionFilter)
                .addPathPatterns("/**");
    }

    @Component
    public static class SessionFilter extends LogOperationsBase implements HandlerInterceptor {
        @Autowired
        private SessionOperations sessionOperations;

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

            if (!(handler instanceof HandlerMethod)) {
                return true;
            }
            log.info("开始进行登陆检查... {}", request.getRequestURI());
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            WithoutLogin withoutLogin = method.getAnnotation(WithoutLogin.class);
            if (withoutLogin != null) {
                log.info("当前接口不需要登陆...");
                return true;
            }
            String sessionToken = sessionOperations.getSessionToken().orElseThrow(BizSessionTimeoutException::new);
            boolean flag = sessionOperations.checkSessionToken(sessionToken);
            if (!flag) {
                log.info("会话失效，跳转登陆...");
                throw new BizSessionTimeoutException();
            }
            return true;
        }
    }

    @Slf4j
    @Component
    public static class TrackFilter extends OncePerRequestFilter {

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            String httpMethod = request.getMethod();
            if (StringUtils.equalsIgnoreCase("options", httpMethod)) {
                filterChain.doFilter(request, response);
                return;
            }
            String uuid = UUID.randomUUID().toString();
            MDC.put("traceId", uuid);
            String requestURI = request.getRequestURI();
            log.info(">>>>>>>>>>>>>>>> processing req:{}", requestURI);
            filterChain.doFilter(request, response);
            log.info("<<<<<<<<<<<<<<<< processed req:{}", requestURI);
        }
    }
}

