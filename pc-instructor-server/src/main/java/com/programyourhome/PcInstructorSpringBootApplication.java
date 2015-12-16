package com.programyourhome;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

import com.programyourhome.pcinstructor.server.PcInstructorServer;

@SpringBootApplication
@PropertySource("file:${pcinstructor.properties.location}")
public class PcInstructorSpringBootApplication {

    public static void main(final String[] args) {
        PcInstructorServer.startServer(PcInstructorSpringBootApplication.class);
    }

}