package com.youedata.daas.rest.common.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class WebContextFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletResponse hsr = (HttpServletResponse)response;
        hsr.setHeader("Access-Control-Allow-Origin","*");
        hsr.setHeader("Access-Control-Allow-Credentials","true");
        hsr.setHeader("Access-Control-Allow-Methods","GET,POST,PUT,DELETE,OPTIONS,PATCH");
        hsr.setHeader("Access-Control-Allow-Headers", "Content-Type,Authorization,accessToken,accountId");
        hsr.setHeader("Access-Control-Expose-Headers", "*");
        hsr.setHeader("Access-Control-Max-Age","3600");

        filterChain.doFilter(request, hsr);
    }

    @Override
    public void destroy() {

    }
}
