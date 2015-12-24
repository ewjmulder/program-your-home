package com.programyourhome.barcodescanner.server;

import java.io.File;

import org.springframework.boot.SpringApplication;

/**
 * The main Spring Boot entry point. Contains annotations about component scan, configuration and property location.
 */
public class BarcodeScannerServer {

    public static void startServer(final Class<?> springBootApplicationClass) {
        final String usageMessage = "Please provide the correct path to the barcode scanner property location with: -Dbarcodescanner.properties.location=/path/to/file";
        final String simulatorPropertyLocation = System.getProperty("barcodescanner.properties.location");
        if (simulatorPropertyLocation == null) {
            System.out.println("No value provided for property 'barcodescanner.properties.location'.");
            System.out.println(usageMessage);
            System.exit(-1);
        }
        final File propertiesFile = new File(simulatorPropertyLocation);
        if (!propertiesFile.exists()) {
            System.out.println("Property file not found: '" + simulatorPropertyLocation + "'.");
            System.out.println(usageMessage);
            System.exit(-1);
        }
        SpringApplication.run(springBootApplicationClass, new String[0]);
    }

}