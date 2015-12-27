package com.programyourhome.barcodescanner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

@Component
public class BarcodeScanDetector {

    @Inject
    private TaskScheduler taskScheduler;

    @PostConstruct
    public void initScanner() {
        this.taskScheduler.schedule(this::processScannedBarcodes, new Date());
    }

    public void processScannedBarcodes() {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        // final PrintStream printer = new PrintStream(new File(args[0]));
        while (true) {
            // final String barcode = this.reader.readLine();
            // TODO: throw events and have trigger and log listeners
            // TODO: Use AH.nl or set in ideas or so. -> don't do that directly, but use identifier + script to fill image and/or price
            // Runtime.getRuntime().exec("curl -X POST http://192.168.2.100:3737/shop/products/addBarcodeToStock/" + barcode);
            // this.printer.println(LocalDateTime.now() + ": " + barcode);
        }
    }

}
