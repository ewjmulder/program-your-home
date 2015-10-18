package com.programyourhome.server;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
public class ProgramYourHomeServerConfig extends WebMvcConfigurationSupport {

    @Inject
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

    @Override
    @Bean
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        final RequestMappingHandlerMapping mapping = super.requestMappingHandlerMapping();
        mapping.setUseSuffixPatternMatch(false);
        return mapping;
    }

}
