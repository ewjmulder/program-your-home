package com.programyourhome.barcodescanner;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.programyourhome.barcodescanner.event.MetaBarcodeScannedEvent;
import com.programyourhome.barcodescanner.event.ProductBarcodeScannedEvent;
import com.programyourhome.common.response.ServiceResult;
import com.programyourhome.shop.model.BarcodeSearchResultType;
import com.programyourhome.shop.model.PyhBarcodeSearchResult;

@Component
public class BarcodeProcessor {

    // final PrintStream printer = new PrintStream(new File(args[0]));
    // TODO: throw events and have trigger and log listeners
    // TODO: Use AH.nl or set in ideas or so. -> don't do that directly, but use identifier + script to fill image and/or price
    // Runtime.getRuntime().exec("curl -X POST http://192.168.2.100:3737/shop/products/addBarcodeToStock/" + barcode);
    // this.printer.println(LocalDateTime.now() + ": " + barcode);

    @Value("${pyh.host}")
    private String pyhHost;
    @Value("${pyh.port}")
    private int pyhPort;

    private ProcessorMode mode;
    private final RestTemplate restTemplate;

    public BarcodeProcessor() {
        this.mode = ProcessorMode.INFO;
        this.restTemplate = new RestTemplate();
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
    public void processBarcode(final ProductBarcodeScannedEvent event) throws URISyntaxException {
        final URI uri = new URI("http", null, this.pyhHost, this.pyhPort, "/shop/products/barcode/" + event.getBarcode(), null, null);
        final ResponseEntity<PyhBarcodeSearchResult> searchResult = this.restTemplate.getForEntity(uri, PyhBarcodeSearchResult.class);
        final BarcodeSearchResultType resultType = searchResult.getBody().getResultType();
        System.out.println("resultType: " + resultType);
        if (resultType == BarcodeSearchResultType.NONE) {
            System.out.println("No product found for barcode: " + event.getBarcode());
            // TODO: Display not-found message on 16x2 screen or LED or sound.
        } else {
            System.out.println("Product found for barcode: " + searchResult.getBody().getProduct().getName());
            if (this.mode == ProcessorMode.INFO) {
                // TODO: What to do on info mode? Display product on 16x2 screen or LED or sound.
                System.out.println("Mode == INFO");
            } else {
                final String updatePath;
                if (this.mode == ProcessorMode.ADD_TO_STOCK) {
                    System.out.println("Mode == ADD_TO_STOCK");
                    updatePath = "addBarcodeToStock";
                } else if (this.mode == ProcessorMode.REMOVE_FROM_STOCK) {
                    System.out.println("Mode == REMOVE_FROM_STOCK");
                    updatePath = "removeBarcodeFromStock";
                } else {
                    throw new IllegalStateException("Unknown mode: " + this.mode);
                }
                final ServiceResult<?> updateResult = this.updateStock(event.getBarcode(), updatePath);
                if (updateResult.isSuccess()) {
                    // TODO: Display success message on 16x2 screen or LED or sound.
                    System.out.println("Update success!");
                } else {
                    // updateResult.getError() - contains error message
                    // TODO: Display error message on 16x2 screen or LED or sound.
                    System.out.println("Update error: " + updateResult.getError());
                }
            }
        }
    }

    private ServiceResult<?> updateStock(final String barcode, final String updatePath) throws URISyntaxException {
        final URI uri = new URI("http", null, this.pyhHost, this.pyhPort, "/shop/products/" + updatePath + "/" + barcode, null, null);
        return this.restTemplate.postForEntity(uri, null, ServiceResult.class).getBody();
    }
}
