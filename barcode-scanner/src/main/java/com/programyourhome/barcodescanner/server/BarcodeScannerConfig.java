package com.programyourhome.barcodescanner.server;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.support.TaskUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;

@Configuration
public class BarcodeScannerConfig {

    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private ApplicationContext applicationContext;

    @Value("${rest.connect.timeout}")
    private int connectTimeout;

    @Value("${rest.read.timeout}")
    private int readTimeout;

    @PostConstruct
    public void configErrorHandler() {
        final SimpleApplicationEventMulticaster eventMulticaster = this.applicationContext.getBean(SimpleApplicationEventMulticaster.class);
        eventMulticaster.setErrorHandler(TaskUtils.LOG_AND_SUPPRESS_ERROR_HANDLER);
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

    @Bean
    public RestTemplate createRestTemplate() {
        final RestTemplate restTemplate = new RestTemplate();

        // Set the object mapper used by the Spring rest template to the same one used in Spring MVC.
        // This is needed to let Spring rest take advantage of the MrBean module.
        final List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        final MappingJackson2HttpMessageConverter jsonMessageConverter = new MappingJackson2HttpMessageConverter();
        jsonMessageConverter.setObjectMapper(this.objectMapper);
        messageConverters.add(jsonMessageConverter);
        restTemplate.setMessageConverters(messageConverters);

        final SimpleClientHttpRequestFactory requestFactory = (SimpleClientHttpRequestFactory) restTemplate.getRequestFactory();
        requestFactory.setConnectTimeout(this.connectTimeout * 1000);
        requestFactory.setReadTimeout(this.readTimeout * 1000);

        return restTemplate;
    }
}
