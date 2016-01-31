package com.programyourhome.barcodescanner.server;

import java.io.File;
import java.io.IOException;

import org.springframework.boot.SpringApplication;

/**
 * The main Spring Boot entry point. Contains annotations about component scan, configuration and property location.
 */
public class BarcodeScannerServer {

    public static void startServer(final Class<?> springBootApplicationClass) throws IOException {
        final String usageMessage = "Please provide the correct path to the barcode scanner property location with: -Dbarcodescanner.properties.location=/path/to/file";
        final String barcodeScannerPropertyLocation = System.getProperty("barcodescanner.properties.location");
        if (barcodeScannerPropertyLocation == null) {
            System.out.println("No value provided for property 'barcodescanner.properties.location'.");
            System.out.println(usageMessage);
            System.exit(-1);
        }
        final File propertiesFile = new File(barcodeScannerPropertyLocation);
        if (!propertiesFile.exists()) {
            System.out.println("Property file not found: '" + barcodeScannerPropertyLocation + "'.");
            System.out.println(usageMessage);
            System.exit(-1);
        }
        SpringApplication.run(springBootApplicationClass, new String[0]);
    }

}