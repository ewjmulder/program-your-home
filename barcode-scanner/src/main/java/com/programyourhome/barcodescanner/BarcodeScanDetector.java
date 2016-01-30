package com.programyourhome.barcodescanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

@Component
public class BarcodeScanDetector {

    private final Log log = LogFactory.getLog(this.getClass());

    @Inject
    private BarcodeEventPublisher barcodeEventPublisher;

    @Inject
    private TaskScheduler taskScheduler;

    private boolean activelyScanning;

    public BarcodeScanDetector() {
        this.activelyScanning = false;
    }

    @PostConstruct
    public void initScanner() {
        this.taskScheduler.schedule(this::detectScannedBarcodes, new Date());
    }

    public boolean isActivelyScanning() {
        return this.activelyScanning;
    }

    /**
     * Detect barcode scanning by forever looping and reading standard in, because the barcodes scanned will be printed to the terminal
     * (like the scanner is a keyboard). If this method crashes, the whole barcode scanner will not work anymore.
     * This class is just detecting scanned barcodes, so we'll just throw an event if we found one.
     */
    public void detectScannedBarcodes() {
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            this.activelyScanning = true;
            // Forever, read standard in to detect barcode scanning.
            while (true) {
                final String barcode = reader.readLine();
                // Perform a sanity check on the line read, because there can be other sources of standard input,
                // like broadcasted user and system messages.
                // If the line consists of just numbers, that is a very strong indication that we've indeed just scanned a barcode.
                if (StringUtils.isNumeric(barcode)) {
                    this.barcodeEventPublisher.publishBarcodeEvent(barcode);
                } else {
                    this.log.info("Non-numeric line read: '" + barcode + "'.");
                }
            }
        } catch (final IOException e) {
            // No way to recover from an IOException on standard in, so we'll just crash.
            throw new IllegalStateException("IOException occured while scanning for barcodes.", e);
        } finally {
            // We're no longer actively scanning when we exit this method.
            this.activelyScanning = false;
        }
    }

}
