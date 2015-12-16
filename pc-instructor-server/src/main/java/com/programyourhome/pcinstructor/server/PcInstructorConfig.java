package com.programyourhome.pcinstructor.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;

@Configuration
public class PcInstructorConfig {

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
