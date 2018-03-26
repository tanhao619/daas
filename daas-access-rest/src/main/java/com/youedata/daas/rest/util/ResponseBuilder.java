package com.youedata.daas.rest.util;

import com.youedata.daas.rest.common.Page;
import com.youedata.daas.rest.common.Response;
import com.youedata.daas.rest.common.RetCode;
import com.youedata.daas.rest.common.RetCodeConst;

/**
 * 功能简单描述
 *
 * @author: lucky
 * @since: 2017-8-29 11:32
 */
public final class ResponseBuilder<T> {
    private transient Response<T> resp;
    public static final Response PARAM_ERR = new Response(RetCodeConst.CLIENT_PARAM_ERROR);
    public static ResponseBuilder newResponse(){
        ResponseBuilder instance = new ResponseBuilder();
        instance.resp = new Response();
        instance.resp.setRetCode(RetCodeConst.UN_KNOWN);
        return instance;
    }

    public ResponseBuilder<T> withRetCodeBy(boolean isInvokedSuccessfully){
        return isInvokedSuccessfully
                ? withRetCode(RetCodeConst.OK)
                : withRetCode(RetCodeConst.RPC_ERROR);
    }

    public ResponseBuilder<T> withRetCode(RetCode retCode){
        this.resp.setRetCode(retCode);
        return this;
    }

    public ResponseBuilder<T> withPage(Page page){
        this.resp.setPage(page);
        return this;
    }

    public ResponseBuilder<T> withData(T t){
        this.resp.setData(t);
        return this;
    }
    public Response<T> build(){
        return this.resp;
    }
}
