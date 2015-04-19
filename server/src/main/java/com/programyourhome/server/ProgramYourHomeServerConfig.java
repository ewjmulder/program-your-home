package com.programyourhome.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class ProgramYourHomeServerConfig {

    @Autowired
    private CacheControlWebContentInterceptor cacheControlWebContentInterceptor;

    @Bean
    public WebMvcConfigurerAdapter getAdaptorBean() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addInterceptors(final InterceptorRegistry registry) {
                registry.addInterceptor(ProgramYourHomeServerConfig.this.cacheControlWebContentInterceptor);
            }
        };
    }

}
