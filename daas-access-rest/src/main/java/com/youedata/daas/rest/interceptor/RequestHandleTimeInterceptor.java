package com.youedata.daas.rest.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * 耗时统计 interceptor
 *
 * @author: lucky
 * @since: 2017-8-29 11:32
 */
public class RequestHandleTimeInterceptor extends HandlerInterceptorAdapter {
    private static final Logger log = LoggerFactory.getLogger(RequestHandleTimeInterceptor.class);
    private static final ThreadLocal<Long> elapsedTime = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        elapsedTime.set(System.nanoTime());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        long st = elapsedTime.get();
        elapsedTime.remove();
        log.info("REQUEST_HANDLE_TIME={}ms", TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - st)));
    }
}
