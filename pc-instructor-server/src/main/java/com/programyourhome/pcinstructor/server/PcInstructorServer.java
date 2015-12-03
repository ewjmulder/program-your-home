package com.programyourhome.pcinstructor.server;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

import com.programyourhome.ComponentScanBase;

/**
 * The main Spring Boot entry point. Contains annotations about component scan, configuration and property location.
 */
@ComponentScan(basePackageClasses = ComponentScanBase.class)
@EnableAutoConfiguration
@PropertySource("file:${pcinstructor.properties.location}")
public class PcInstructorServer {

    public static void startServer() {
        final String usageMessage = "Please provide the correct path to the simulator property location with: -Dpcinstructor.properties.location=/path/to/file";
        final String simulatorPropertyLocation = System.getProperty("pcinstructor.properties.location");
        if (simulatorPropertyLocation == null) {
            System.out.println("No value provided for property 'pcinstructor.properties.location'.");
            System.out.println(usageMessage);
            System.exit(-1);
        }
        final File propertiesFile = new File(simulatorPropertyLocation);
        if (!propertiesFile.exists()) {
            System.out.println("Property file not found: '" + simulatorPropertyLocation + "'.");
            System.out.println(usageMessage);
            System.exit(-1);
        }
        final SpringApplication application = new SpringApplication(PcInstructorServer.class);
        // Headless should be disabled to use the java.awt.Robot.
        application.setHeadless(false);
        application.run(new String[0]);
    }

}