package com.programyourhome.barcodescanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import com.programyourhome.barcodescanner.event.MetaBarcodeScannedEvent;
import com.programyourhome.barcodescanner.event.ProductBarcodeScannedEvent;

@Component
public class BarcodeScanDetector {

    @Inject
    private ApplicationEventPublisher eventPublisher;

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
                final Optional<MetaBarcode> optionalMetaBarcode = Arrays.stream(MetaBarcode.values())
                        .filter(metaBarcode -> metaBarcode.getBarcode().equals(barcode))
                        .findAny();
                // If this is a meta barcode, throw a meta barcode event, otherwise a normal one.
                if (optionalMetaBarcode.isPresent()) {
                    this.eventPublisher.publishEvent(new MetaBarcodeScannedEvent(optionalMetaBarcode.get()));
                } else {
                    this.eventPublisher.publishEvent(new ProductBarcodeScannedEvent(barcode));
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
