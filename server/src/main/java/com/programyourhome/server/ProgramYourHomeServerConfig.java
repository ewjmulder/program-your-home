package com.programyourhome.server;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;

/**
 * General Spring configuration for the whole program your home server.
 * Warning: to NOT extend WebMvcConfigurationSupport, since that will
 * opt-out on the Spring MVC auto-configuration of Spring Boot,
 * like registering custom Jackson modules.
 */
@Configuration
public class ProgramYourHomeServerConfig {

    @Inject
    private CacheControlWebContentInterceptor cacheControlWebContentInterceptor;

    @Bean
    public WebMvcConfigurerAdapter getAdaptorBean() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addInterceptors(final InterceptorRegistry registry) {
                // Add interceptor for cache control.
                registry.addInterceptor(ProgramYourHomeServerConfig.this.cacheControlWebContentInterceptor);
            }

            @Override
            public void configurePathMatch(final PathMatchConfigurer configurer) {
                // Avoid pathvariable parameters being truncated on dots.
                configurer.setUseSuffixPatternMatch(false);
            }
        };
    }

    /**
     * Add the Mr.Bean module to the Jackson object mapper. It will automagically create
     * implementations of interfaces for DTO data transfer.
     * See also: https://github.com/FasterXML/jackson-module-mrbean
     */
    @Bean
    public Module createMrBeanModule() {
        return new MrBeanModule();
    }

}
