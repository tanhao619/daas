package com.youedata.daas.rest.common.filter.userFilter;

import com.alibaba.fastjson.JSON;
import com.youedata.daas.rest.common.Constant;
import com.youedata.daas.rest.common.JedisClient;
import com.youedata.daas.rest.exception.BizExceptionEnum;
import com.youedata.daas.rest.exception.BussinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserInterceptor implements HandlerInterceptor {

    private static final String HEADER_KEY_USERID = "accountId";

    private static final Integer redisTimeout = 1800;

    @Autowired
    private JedisClient jedisClient;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (!Constant.REQUEST_METHOD_OPTIONS.equals(request.getMethod()) && request.getServletPath().startsWith("/api/v2/daas")) {
            final String uid = request.getHeader(HEADER_KEY_USERID);
            if(StringUtils.isNotEmpty(uid)) {
//                User user = JSON.parseObject(jedisClient.get(uid),User.class);
//                jedisClient.expire(uid,redisTimeout);
                // controller或者service中获取用户信息时，直接使用 【UserThreadLocal.get().getXXX()】
//                UserThreadLocal.set(user);

                User user = new User();
                user.setAccountId(uid);
                user.setAccountName(request.getHeader(Constant.SSO_ACCOUNTNAME));
                user.setTenantId(request.getHeader(Constant.SSO_ACCOUNTTENANTID));
                user.setAccessToken(request.getHeader(Constant.SSO_ACCESSTOKEN));
                UserThreadLocal.set(user);


            } else {
                throw new BussinessException(BizExceptionEnum.PARAM_USER_NOT_EXIST);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        UserThreadLocal.remove();
    }

}