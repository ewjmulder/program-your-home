package com.programyourhome.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

import com.programyourhome.ComponentScanBase;

@EnableAutoConfiguration
@ComponentScan(basePackageClasses = ComponentScanBase.class)
@PropertySource("classpath:com/programyourhome/config/server/properties/server.properties")
public class ProgramYourHomeServer {

    public static void startServer() {
        SpringApplication.run(ProgramYourHomeServer.class, new String[0]);
    }

}
