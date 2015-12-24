package com.programyourhome;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

import com.programyourhome.barcodescanner.server.BarcodeScannerServer;

@SpringBootApplication
@PropertySource("file:${barcodescanner.properties.location}")
public class BarcodeScannerSpringBootApplication {

    public static void main(final String[] args) {
        BarcodeScannerServer.startServer(BarcodeScannerSpringBootApplication.class);
    }

}