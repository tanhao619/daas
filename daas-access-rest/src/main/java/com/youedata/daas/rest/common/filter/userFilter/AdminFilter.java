package com.youedata.daas.rest.common.filter.userFilter;

import com.youedata.account.authentication.client.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户单点登录过滤器
 *
 * @author zhangkai
 */
@Configuration
public class AdminFilter extends WebMvcConfigurerAdapter {

    @Value("${SSOUrl}")
    private String SSOUrl;
    @Bean
    public FilterRegistrationBean SSOFilter() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(ssoFilter());
        Map<String , String> param = new HashMap<String , String>();
        param.put("ServiceCheckURL",SSOUrl);
        //此处属性为配置过滤器是否信任Cookie中的accessToken
        param.put("TrustCookie", "false");
        //此属性为配置过滤器排除过滤路径，多个路径之间用逗号隔开
        param.put("excludePaths", "/api/v2/daas/admin/sessions,/swagger-ui.html,/swagger-resources,/webjars,/v2/api-docs,/configuration");
        registrationBean.setInitParameters(param);
        List<String> urls = new ArrayList<String>();
        urls.add("/*");
        registrationBean.setUrlPatterns(urls);
        return registrationBean;

    }

    @Bean
    public AuthenticationFilter ssoFilter() {
        return new AuthenticationFilter();
    }
}
