package com.programyourhome.server.security;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(final AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .inMemoryAuthentication()
                .withUser("user").password("password").roles("USER")
                .and()
                // Guest account when no credentials are supplied.
                .withUser(SecurityConstants.PYH_GUEST_ACCOUNT_USERNAME).password(SecurityConstants.PYH_GUEST_ACCOUNT_PASSWORD).roles("USER");
    }

    @Override
    protected void configure(final HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf().disable()
                .sessionManagement().disable()
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                // Add our own (custom) basic authentication filter to grab the username/password to the chain,
                // before the filter that would normally do that for basic authentication.
                .addFilterBefore(new PyhBasicAuthenticationFilter(this.authenticationManager()), BasicAuthenticationFilter.class);
    }
}