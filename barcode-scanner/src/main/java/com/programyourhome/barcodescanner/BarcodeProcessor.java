package com.programyourhome.barcodescanner;

import java.net.URI;
import java.net.URISyntaxException;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.programyourhome.barcodescanner.event.MetaBarcodeScannedEvent;
import com.programyourhome.barcodescanner.event.ProductBarcodeScannedEvent;
import com.programyourhome.barcodescanner.model.BarcodeSearchServiceResult;
import com.programyourhome.barcodescanner.model.MetaBarcode;
import com.programyourhome.barcodescanner.model.ProcessorMode;
import com.programyourhome.common.response.ServiceResult;
import com.programyourhome.shop.model.BarcodeSearchResultType;

@Component
public class BarcodeProcessor {

    @Value("${pyh.host}")
    private String pyhHost;
    @Value("${pyh.port}")
    private int pyhPort;

    @Inject
    private RestTemplate restTemplate;

    private ProcessorMode mode;

    public BarcodeProcessor() {
        this.mode = ProcessorMode.INFO;
    }

    public ProcessorMode getMode() {
        return this.mode;
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
        final ResponseEntity<BarcodeSearchServiceResult> searchResult = this.restTemplate.getForEntity(uri, BarcodeSearchServiceResult.class);
        final BarcodeSearchResultType resultType = searchResult.getBody().getPayload().getResultType();
        System.out.println("resultType: " + resultType);
        if (resultType == BarcodeSearchResultType.NONE) {
            System.out.println("No product found for barcode: " + event.getBarcode());
            // TODO: Display not-found message on 16x2 screen or LED or sound.
        } else {
            System.out.println("Product found for barcode: " + searchResult.getBody().getPayload().getProduct().getName());
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
