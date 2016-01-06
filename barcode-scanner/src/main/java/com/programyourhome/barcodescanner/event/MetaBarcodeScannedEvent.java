package com.programyourhome.barcodescanner.event;

import java.util.Date;

import org.springframework.context.ApplicationEvent;

import com.programyourhome.barcodescanner.model.MetaBarcode;

@SuppressWarnings("serial")
public class MetaBarcodeScannedEvent extends ApplicationEvent {

    private static final Object DUMMY_EVENT_SOURCE = new Object();

    private final MetaBarcode metaBarcode;

    public MetaBarcodeScannedEvent(final MetaBarcode metaBarcode) {
        super(DUMMY_EVENT_SOURCE);
        this.metaBarcode = metaBarcode;
    }

    public MetaBarcode getMetaBarcode() {
        return this.metaBarcode;
    }

    @Override
    public String toString() {
        return new Date(this.getTimestamp()).toString() + " - META - " + this.metaBarcode;
    }

}
