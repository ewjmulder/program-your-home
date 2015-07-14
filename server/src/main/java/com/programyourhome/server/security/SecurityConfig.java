package com.programyourhome.server.security;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.programyourhome.common.constants.HttpMethods;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(final AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .inMemoryAuthentication()
                .withUser("user").password("password").roles("USER", "ADMIN")
                .and()
                // Guest account when no credentials are supplied.
                .withUser(SecurityConstants.PYH_GUEST_ACCOUNT_USERNAME).password(SecurityConstants.PYH_GUEST_ACCOUNT_PASSWORD).roles("USER");
    }

    @Override
    protected void configure(final HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // Allow cross-site requests.
                .csrf().disable()
                // We want to be completely stateless, so no sessions allowed.
                .sessionManagement().disable()
                // Configure access restriction.
                .authorizeRequests()
                // Allow all OPTIONS requests, since those cannot have credentials.
                .requestMatchers(request -> HttpMethods.isOptions(request.getMethod())).permitAll()
                // Specific paths require specific roles. TODO: real config, dummy testing for now...
                .antMatchers("/ir").permitAll()
                .antMatchers("/meta/**").hasRole("ADMIN")
                // All other paths do need at least successful authentication.
                .anyRequest().authenticated()
                .and()
                // Add our own (custom) basic authentication filter to grab the username/password to the chain,
                // before the filter that would normally do that for basic authentication.
                .addFilterBefore(new PyhBasicAuthenticationFilter(this.authenticationManager()), BasicAuthenticationFilter.class);
    }
}