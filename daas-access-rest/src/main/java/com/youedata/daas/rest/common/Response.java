package com.youedata.daas.rest.common;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 返回码
 *
 * @author: lucky
 * @since: 2017-8-29 11:32
 */
public class Response<T> {

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private T data;

    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    private RetCode retCode;

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private Page page;

    public Response(){}
    public Response(RetCode retCode){
        this.retCode =retCode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public RetCode getRetCode() {
        return retCode;
    }

    public void setRetCode(RetCode retCode) {
        this.retCode = retCode;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Response{");
        sb.append("data=").append(data);
        sb.append(", retCode=").append(retCode);
        sb.append(", page=").append(page);
        sb.append('}');
        return sb.toString();
    }
}
