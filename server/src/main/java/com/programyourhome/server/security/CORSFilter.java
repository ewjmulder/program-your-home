package com.programyourhome.server.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

@Component
public class CORSFilter implements Filter {

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse respsone, final FilterChain chain) throws IOException, ServletException {
        final HttpServletResponse response = (HttpServletResponse) respsone;
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Origin", ((HttpServletRequest) request).getHeader("origin"));
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with, " +
                SecurityConstants.PYH_BASIC_AUTHENTICATION_USERNAME_HEADER + ", " +
                SecurityConstants.PYH_BASIC_AUTHENTICATION_PASSWORD_HEADER);
        chain.doFilter(request, respsone);
    }

    @Override
    public void init(final FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }

}