package com.chatapp.webchat.filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter(filterName = "CORSFilter", urlPatterns = {"/api/*"}, asyncSupported = true)
public class CORSFilter implements Filter {
    public void init(FilterConfig config) throws ServletException {
    }

    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {

        ((HttpServletResponse) response).setHeader("Access-Control-Allow-Origin", "*");
        ((HttpServletResponse) response).setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
        ((HttpServletResponse) response).setHeader("Access-Control-Max-Age", "3600");
        ((HttpServletResponse) response).setHeader("Access-Control-Allow-Headers", "x-requested-with, content-type, Upgrade, Sec-WebSocket-Key, Sec-WebSocket-Version");
        chain.doFilter(request, response);
    }
}
