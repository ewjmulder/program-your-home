package com.programyourhome;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.programyourhome.server.ProgramYourHomeServer;

@SpringBootApplication
public class PyhSpringBootApplication {

    public static void startApplication() {
        ProgramYourHomeServer.startServer(PyhSpringBootApplication.class);
    }

}