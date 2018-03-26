package com.youedata.daas.core.aop;

import com.youedata.daas.core.base.tips.Tip;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局的的异常拦截器（拦截所有的控制器）（带有@RequestMapping注解的方法上都会拦截）
 *
 * @author fengshuonan
 * @date 2016年11月12日 下午3:19:56
 */
public abstract class BaseControllerExceptionHandler {

    /**
     * 拦截异常
     *
     * @author fengshuonan
     */
    @ExceptionHandler(Exception.class)
    //@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public abstract Tip handle(Exception e);

}
