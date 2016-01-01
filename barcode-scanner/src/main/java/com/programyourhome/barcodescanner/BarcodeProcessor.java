package com.programyourhome.barcodescanner;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.programyourhome.barcodescanner.event.MetaBarcodeScannedEvent;
import com.programyourhome.barcodescanner.event.ProductBarcodeScannedEvent;

@Component
public class BarcodeProcessor {

    // final PrintStream printer = new PrintStream(new File(args[0]));
    // TODO: throw events and have trigger and log listeners
    // TODO: Use AH.nl or set in ideas or so. -> don't do that directly, but use identifier + script to fill image and/or price
    // Runtime.getRuntime().exec("curl -X POST http://192.168.2.100:3737/shop/products/addBarcodeToStock/" + barcode);
    // this.printer.println(LocalDateTime.now() + ": " + barcode);

    private ProcessorMode mode;

    public BarcodeProcessor() {
        this.mode = ProcessorMode.INFO;
    }

    @EventListener(MetaBarcodeScannedEvent.class)
    public void processBarcode(final MetaBarcodeScannedEvent event) {
        final MetaBarcode metaBarcode = event.getMetaBarcode();
        if (metaBarcode == MetaBarcode.SET_MODE_INFO) {
            this.mode = ProcessorMode.INFO;
        } else if (metaBarcode == MetaBarcode.SET_MODE_ADD_TO_STOCK) {
            this.mode = ProcessorMode.ADD_TO_STOCK;
        } else if (metaBarcode == MetaBarcode.SET_MODE_REMOVE_FROM_STOCK) {
            this.mode = ProcessorMode.REMOVE_FROM_STOCK;
        }
    }

    @EventListener(ProductBarcodeScannedEvent.class)
    public void processBarcode(final ProductBarcodeScannedEvent event) {
        if (this.mode == ProcessorMode.INFO) {
            // TODO: What to do on info mode? Display product on 16x2 screen.
        } else {
            // TODO:
            // - First query if the barcode is known in the system (use newly to be created /products/barcodeRegistered/{barcode} REST method
            // - Then based on mode either call addBarcodeToStock or removeBarcodeFromStock (+use properties to get PYH host and port)
        }
    }
}
