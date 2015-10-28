package com.programyourhome.server;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;

/**
 * General Spring configuration for the whole program your home server.
 * Warning: to NOT extend WebMvcConfigurationSupport, since that will
 * opt-out on the Spring MVC auto-configuration of Spring Boot,
 * like registering custom Jackson modules.
 */
@Configuration
@EnableAspectJAutoProxy
public class ProgramYourHomeServerConfig {

    @Inject
    private CacheControlWebContentInterceptor cacheControlWebContentInterceptor;

    @Inject
    private ObjectMapper objectMapper;

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

    @PostConstruct
    public void objectMapperConfiguration() {
        // Do not serialize any fields, even public ones. The PYH classes do not contain public fields,
        // but the generated classes by MrBean do, so to avoid them being serialized we set field serialization to none.
        this.objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.NONE);
    }

}
