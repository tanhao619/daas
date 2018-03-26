package com.youedata.daas.rest.exception;


import com.youedata.daas.core.exception.DaasException;

/**
 * @author fengshuonan
 * @Description 业务异常的封装
 * @date 2016年11月12日 下午5:05:10
 */
public class BussinessException extends DaasException {

	public BussinessException(BizExceptionEnum bizExceptionEnum) {
		super(bizExceptionEnum.getCode(), bizExceptionEnum.getMessage(),"");
	}
}
