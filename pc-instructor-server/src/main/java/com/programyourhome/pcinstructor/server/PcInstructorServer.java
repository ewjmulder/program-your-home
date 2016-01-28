package com.programyourhome.pcinstructor.server;

import java.io.File;

import org.springframework.boot.SpringApplication;

/**
 * The main Spring Boot entry point. Contains annotations about component scan, configuration and property location.
 */
public class PcInstructorServer {

    public static void startServer(final Class<?> springBootApplicationClass) {
        final String usageMessage = "Please provide the correct path to the pc instructor property location with: -Dpcinstructor.properties.location=/path/to/file";
        final String pcinstructorPropertyLocation = System.getProperty("pcinstructor.properties.location");
        if (pcinstructorPropertyLocation == null) {
            System.out.println("No value provided for property 'pcinstructor.properties.location'.");
            System.out.println(usageMessage);
            System.exit(-1);
        }
        final File propertiesFile = new File(pcinstructorPropertyLocation);
        if (!propertiesFile.exists()) {
            System.out.println("Property file not found: '" + pcinstructorPropertyLocation + "'.");
            System.out.println(usageMessage);
            System.exit(-1);
        }
        final SpringApplication application = new SpringApplication(springBootApplicationClass);
        // Headless should be disabled to use the java.awt.Robot.
        application.setHeadless(false);
        // TODO: Solve with real log level settings, doesn't work well with current config.
        application.run(new String[0]);
    }

}