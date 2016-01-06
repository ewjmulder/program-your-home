package com.programyourhome.barcodescanner.server;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;

@Configuration
public class BarcodeScannerConfig {

    @Inject
    private ObjectMapper objectMapper;

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
        final List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        final MappingJackson2HttpMessageConverter jsonMessageConverter = new MappingJackson2HttpMessageConverter();
        jsonMessageConverter.setObjectMapper(this.objectMapper);
        messageConverters.add(jsonMessageConverter);
        restTemplate.setMessageConverters(messageConverters);
        return restTemplate;
    }

}
