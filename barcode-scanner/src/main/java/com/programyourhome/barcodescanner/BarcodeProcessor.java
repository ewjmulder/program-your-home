package com.programyourhome.barcodescanner;

import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpMethod;
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

    private final Log log = LogFactory.getLog(this.getClass());

    private static final String PRODUCTS_BASE_PATH = "/shop/products/";
    private static final String SEARCH_PRODUCT_PATH = PRODUCTS_BASE_PATH + "barcode/%s";
    private static final String GET_STOCK_PATH = PRODUCTS_BASE_PATH + "%s/state";
    private static final String UPDATE_STOCK_PATH = PRODUCTS_BASE_PATH + "%s/%s";

    private static final String ADD_TO_STOCK_PATH = "addBarcodeToStock";
    private static final String REMOVE_FROM_STOCK_PATH = "removeBarcodeFromStock";

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

    @PostConstruct
    public void init() {
        this.ledLights.setModeInfo();
        this.ledLights.setTransactionNone();
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
        this.ledLights.setTransactionProcessing();
        try {
            this.doProcessBarcode(event);
        } catch (final Exception e) {
            this.log.error("Exception while processing barcode.", e);
            this.ledLights.setTransactionError();
            // Handle some special cases separately.
            if (e.getCause() instanceof SocketTimeoutException) {
                this.lcdDisplay.show("PYH server", "not reachable");
            } else {
                this.lcdDisplay.show("System Error", e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    }

    private void doProcessBarcode(final ProductBarcodeScannedEvent event) {
        final PyhBarcodeSearchResult result = this.searchProduct(event.getBarcode());
        final BarcodeSearchResultType resultType = result.getResultType();
        if (resultType == BarcodeSearchResultType.NONE) {
            this.log.debug("No product found for barcode: " + event.getBarcode());
            this.lcdDisplay.show("Product", "not found");
            this.ledLights.setTransactionError();
        } else {
            final PyhProduct product = result.getProduct();
            this.log.debug("Product found for barcode: " + product.getName());
            if (this.mode == ProcessorMode.INFO) {
                final int currentStockAmount = this.getStock(product);
                this.log.debug("Info mode, stock: " + currentStockAmount);
                this.lcdDisplay.show(product.getName(), "[INFO] Stock: " + currentStockAmount);
                this.ledLights.setTransactionOk();
            } else {
                final String updatePath;
                if (this.mode == ProcessorMode.ADD_TO_STOCK) {
                    this.log.debug("Mode == ADD_TO_STOCK");
                    updatePath = ADD_TO_STOCK_PATH;
                } else if (this.mode == ProcessorMode.REMOVE_FROM_STOCK) {
                    this.log.debug("Mode == REMOVE_FROM_STOCK");
                    updatePath = REMOVE_FROM_STOCK_PATH;
                } else {
                    throw new IllegalStateException("Unknown mode: " + this.mode);
                }
                final ServiceResult<?> updateResult = this.updateStock(event.getBarcode(), updatePath);
                if (updateResult.isSuccess()) {
                    final int newStockAmount = this.getStock(product);
                    this.log.debug("Stock updated to: " + newStockAmount);
                    this.lcdDisplay.show(product.getName(), "[NEW] Stock: " + newStockAmount);
                    this.ledLights.setTransactionOk();
                } else {
                    this.log.error("Error while updating stock: " + updateResult.getError());
                    this.lcdDisplay.show("System Error", updateResult.getError());
                    this.ledLights.setTransactionError();
                }
            }
        }
    }

    private PyhBarcodeSearchResult searchProduct(final String barcode) {
        return this.getServicePayload(HttpMethod.GET, BarcodeSearchServiceResult.class, SEARCH_PRODUCT_PATH, barcode);
    }

    private int getStock(final PyhProduct product) {
        return this.getServicePayload(HttpMethod.GET, ProductStateServiceResult.class, GET_STOCK_PATH, product.getId()).getAmount();
    }

    private ServiceResult<?> updateStock(final String barcode, final String updatePath) {
        return this.getServiceResult(HttpMethod.POST, ServiceResult.class, UPDATE_STOCK_PATH, updatePath, barcode);
    }

    private <P, T extends ServiceResult<P>> P getServicePayload(
            final HttpMethod method, final Class<T> resultClass, final String urlToFormat, final Object... args) {
        return this.getServiceResult(method, resultClass, urlToFormat, args).getPayload();
    }

    private <T extends ServiceResult<?>> T getServiceResult(
            final HttpMethod method, final Class<T> resultClass, final String urlToFormat, final Object... args) {
        try {
            final URI uri = new URI("http", null, this.pyhHost, this.pyhPort, String.format(urlToFormat, args), null, null);
            return this.restTemplate.exchange(uri, method, null, resultClass).getBody();
        } catch (final URISyntaxException e) {
            throw new IllegalStateException("URISyntaxException while calling product service.", e);
        }
    }

}
