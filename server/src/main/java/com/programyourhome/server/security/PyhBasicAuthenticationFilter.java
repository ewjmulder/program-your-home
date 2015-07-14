package com.programyourhome.server.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

/**
 * A custom implementation of basic authentication. The principle is very similar:
 * - Every request should provide a username and a password.
 * - The server validates these against the known users.
 * - If successful, the user is known by the system and can continue.
 * - If unsuccessful, an error response will be returned.
 *
 * The rationale behind this is, is the fact that standard basic authentication generates too much
 * trouble regarding cross-site security and browser quirks related to basic authentication and popups.
 * This custom headers solution essentially is the same: no security in the message, but rely on the transportation
 * channel (HTTPS) to secure the credentials. Generally it's a bad idea to use custom authentication methods,
 * but this seems like a good tradeoff. In the future, this might be replaced by OAuth2 Resource Owner Password Flow.
 */
public class PyhBasicAuthenticationFilter implements Filter {

    private final Log log = LogFactory.getLog(this.getClass());

    private final AuthenticationManager authenticationManager;

    public PyhBasicAuthenticationFilter(final AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) req;

        try {
            String username = request.getHeader(SecurityConstants.PYH_BASIC_AUTHENTICATION_USERNAME_HEADER);
            String password = request.getHeader(SecurityConstants.PYH_BASIC_AUTHENTICATION_PASSWORD_HEADER);
            if (username == null && password == null) {
                // For OPTIONS requests, it is expected to not have any credentials in the request.
                if (!request.getMethod().equals(HttpMethod.OPTIONS)) {
                    this.log.warn("No username and password found in the request headers. Using default guest account.");
                }
                username = SecurityConstants.PYH_GUEST_ACCOUNT_USERNAME;
                password = SecurityConstants.PYH_GUEST_ACCOUNT_PASSWORD;
            }
            final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
            token.setDetails(new WebAuthenticationDetails(request));
            final Authentication authentication = this.authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (final AuthenticationException e) {
            this.log.error("AuthenticationException during authentication process.", e);
            SecurityContextHolder.getContext().setAuthentication(null);
        }

        chain.doFilter(req, res);
    }

    @Override
    public void init(final FilterConfig config) throws ServletException {
    }

    @Override
    public void destroy() {
    }

}
