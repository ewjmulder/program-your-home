package com.programyourhome.barcodescanner.event;

import org.springframework.context.ApplicationEvent;

@SuppressWarnings("serial")
public class ProductBarcodeScannedEvent extends ApplicationEvent {

    private static final Object DUMMY_EVENT_SOURCE = new Object();

    private final String barcode;

    public ProductBarcodeScannedEvent(final String barcode) {
        super(DUMMY_EVENT_SOURCE);
        this.barcode = barcode;
    }

    public String getBarcode() {
        return this.barcode;
    }

}
