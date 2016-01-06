package com.programyourhome.barcodescanner;

import java.util.Arrays;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.programyourhome.barcodescanner.event.MetaBarcodeScannedEvent;
import com.programyourhome.barcodescanner.event.ProductBarcodeScannedEvent;
import com.programyourhome.barcodescanner.model.MetaBarcode;

@Component
public class BarcodeEventPublisher {

    @Inject
    private ApplicationEventPublisher eventPublisher;

    public void publishBarcodeEvent(final String barcode) {
        final Optional<MetaBarcode> optionalMetaBarcode = Arrays.stream(MetaBarcode.values())
                .filter(metaBarcode -> metaBarcode.getBarcode().equals(barcode))
                .findAny();
        // If this is a meta barcode, publish a meta barcode event, otherwise a product barcode event.
        if (optionalMetaBarcode.isPresent()) {
            this.eventPublisher.publishEvent(new MetaBarcodeScannedEvent(optionalMetaBarcode.get()));
        } else {
            this.eventPublisher.publishEvent(new ProductBarcodeScannedEvent(barcode));
        }
    }

}
