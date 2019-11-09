package com.github.yungyu16.gobang.config;

import cn.xiaoshidai.common.toolkit.base.StringTools;
import cn.xiaoshidai.common.toolkit.exception.BizSessionTimeOutException;
import com.github.yungyu16.gobang.annotation.WithoutLogin;
import com.github.yungyu16.gobang.base.SessionOperationBase;
import lombok.extern.slf4j.Slf4j;
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
    public static class SessionFilter extends SessionOperationBase implements HandlerInterceptor {

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            if (!(handler instanceof HandlerMethod)) {
                return true;
            }
            log.info("开始进行登陆检查...");
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            WithoutLogin withoutLogin = method.getAnnotation(WithoutLogin.class);
            if (withoutLogin != null) {
                log.info("当前接口不需要登陆...");
                return true;
            }
            String sessionToken = getSessionToken().orElseThrow(BizSessionTimeOutException::new);
            boolean flag = checkSessionToken(sessionToken);
            log.info("登陆检查结果：{}", flag);
            return flag;
        }
    }

    @Slf4j
    @Component
    public static class TrackFilter extends OncePerRequestFilter {

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            String uuid = StringTools.timestampUUID();
            MDC.put("traceId", uuid);
            String requestURI = request.getRequestURI();
            log.info(">>>>>>>>>>>>>>>> processing req:{}", requestURI);
            filterChain.doFilter(request, response);
            response.addHeader("traceId", uuid);
            log.info("<<<<<<<<<<<<<<<< processed req:{}", requestURI);
        }
    }
}

