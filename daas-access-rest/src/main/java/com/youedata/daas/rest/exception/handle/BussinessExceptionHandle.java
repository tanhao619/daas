package com.youedata.daas.rest.exception.handle;

import com.youedata.daas.core.aop.BaseControllerExceptionHandler;
import com.youedata.daas.core.base.tips.Tip;
import com.youedata.daas.rest.exception.BizExceptionEnum;
import com.youedata.daas.rest.exception.BussinessException;
import com.youedata.daas.rest.util.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author sijianmeng
 * Created by cdyoue on 2017/11/27.
 */
@RestControllerAdvice
public class BussinessExceptionHandle extends BaseControllerExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public Tip handle(Exception e) {
        if (e instanceof BussinessException){
            BussinessException exception = (BussinessException)e;
            logger.error(exception.getMessage() + "{}",exception);
            return ResultUtil.result(exception.getCode(),exception.getMessage());
        }
        logger.error("未知错误: {}",e);
        return ResultUtil.result(BizExceptionEnum.SYS_ERROR.getCode(), BizExceptionEnum.SYS_ERROR.getMessage());
    }
}
