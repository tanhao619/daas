package com.youedata.daas.rest.config;

import com.youedata.daas.rest.common.filter.WebContextFilter;
import com.youedata.daas.rest.common.filter.userFilter.UserInterceptor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * web配置
 */
@Configuration
public class WebMvcConfigurer extends WebMvcConfigurerAdapter {

    @Bean
    public WebContextFilter webContextFilter() {
        return new WebContextFilter();
    }

    @Bean
    public FilterRegistrationBean WebContextFilter() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(webContextFilter());
        registrationBean.setOrder(Integer.MAX_VALUE - 1);
        return registrationBean;

    }

    @Bean
    public UserInterceptor userInterceptor() {
        return new UserInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userInterceptor()).addPathPatterns("/**");
        super.addInterceptors(registry);
    }

}