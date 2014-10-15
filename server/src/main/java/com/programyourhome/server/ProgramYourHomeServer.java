package com.programyourhome.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import com.programyourhome.ComponentScanBase;

@ComponentScan(basePackageClasses = ComponentScanBase.class)
@EnableAutoConfiguration
public class ProgramYourHomeServer {

    public static void startServer() {
        SpringApplication.run(ProgramYourHomeServer.class, new String[0]);
    }

}
