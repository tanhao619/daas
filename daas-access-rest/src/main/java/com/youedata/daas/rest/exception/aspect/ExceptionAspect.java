package com.youedata.daas.rest.exception.aspect;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by cdyoue on 2017/11/27.
 */
@Aspect
@Component
public class ExceptionAspect {

    private final static Logger logger = LoggerFactory.getLogger(ExceptionAspect.class);
    @Pointcut("execution(public * com.youedata.daas.rest.modular.controller.*.*(..))")
    public void exceptionPoint(){}

    @AfterThrowing(value = "exceptionPoint()")
    public void catchException(){}
}
