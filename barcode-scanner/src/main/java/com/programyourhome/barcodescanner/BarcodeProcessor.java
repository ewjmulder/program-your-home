package com.programyourhome.barcodescanner;

import java.net.URI;
import java.net.URISyntaxException;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.programyourhome.barcodescanner.event.MetaBarcodeScannedEvent;
import com.programyourhome.barcodescanner.event.ProductBarcodeScannedEvent;
import com.programyourhome.barcodescanner.model.BarcodeSearchServiceResult;
import com.programyourhome.barcodescanner.model.MetaBarcode;
import com.programyourhome.barcodescanner.model.ProcessorMode;
import com.programyourhome.barcodescanner.model.ProductStateServiceResult;
import com.programyourhome.barcodescanner.ui.LcdDisplay;
import com.programyourhome.barcodescanner.ui.RgbLedLights;
import com.programyourhome.common.response.ServiceResult;
import com.programyourhome.shop.model.BarcodeSearchResultType;
import com.programyourhome.shop.model.PyhBarcodeSearchResult;
import com.programyourhome.shop.model.PyhProduct;

@Component
public class BarcodeProcessor {

    @Value("${pyh.host}")
    private String pyhHost;
    @Value("${pyh.port}")
    private int pyhPort;

    @Inject
    private LcdDisplay lcdDisplay;

    @Inject
    private RgbLedLights ledLights;

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
            this.lcdDisplay.show("Mode set to:", "Info");
            this.ledLights.setModeInfo();
        } else if (metaBarcode == MetaBarcode.SET_MODE_ADD_TO_STOCK) {
            this.mode = ProcessorMode.ADD_TO_STOCK;
            this.lcdDisplay.show("Mode set to:", "Add to stock");
            this.ledLights.setModeAddToStock();
        } else if (metaBarcode == MetaBarcode.SET_MODE_REMOVE_FROM_STOCK) {
            this.mode = ProcessorMode.REMOVE_FROM_STOCK;
            this.lcdDisplay.show("Mode set to:", "Remove frm stock");
            this.ledLights.setModeRemoveFromStock();
        }
    }

    @EventListener(ProductBarcodeScannedEvent.class)
    public void processBarcode(final ProductBarcodeScannedEvent event) throws URISyntaxException {
        final PyhBarcodeSearchResult result = this.searchProduct(event.getBarcode());
        final BarcodeSearchResultType resultType = result.getResultType();
        System.out.println("resultType: " + resultType);
        if (resultType == BarcodeSearchResultType.NONE) {
            System.out.println("No product found for barcode: " + event.getBarcode());
            this.lcdDisplay.show("Product", "not found");
        } else {
            final PyhProduct product = result.getProduct();
            System.out.println("Product found for barcode: " + product.getName());
            if (this.mode == ProcessorMode.INFO) {
                this.lcdDisplay.show(product.getName(), "[INFO] Stock: " + this.getStock(product));
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
                    this.lcdDisplay.show(product.getName(), "[NEW] Stock: " + this.getStock(product));
                } else {
                    this.lcdDisplay.show("ERROR", updateResult.getError());
                }
            }
        }
    }

    private PyhBarcodeSearchResult searchProduct(final String barcode) throws URISyntaxException {
        final URI uri = new URI("http", null, this.pyhHost, this.pyhPort, "/shop/products/barcode/" + barcode, null, null);
        return this.restTemplate.getForEntity(uri, BarcodeSearchServiceResult.class).getBody().getPayload();
    }

    private int getStock(final PyhProduct product) throws URISyntaxException {
        final URI uri = new URI("http", null, this.pyhHost, this.pyhPort, "/shop/products/" + product.getId() + "/state", null, null);
        return this.restTemplate.getForEntity(uri, ProductStateServiceResult.class).getBody().getPayload().getAmount();
    }

    private ServiceResult<?> updateStock(final String barcode, final String updatePath) throws URISyntaxException {
        final URI uri = new URI("http", null, this.pyhHost, this.pyhPort, "/shop/products/" + updatePath + "/" + barcode, null, null);
        return this.restTemplate.postForEntity(uri, null, ServiceResult.class).getBody();
    }
}
