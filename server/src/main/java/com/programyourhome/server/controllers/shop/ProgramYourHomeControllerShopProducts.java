package com.programyourhome.server.controllers.shop;

import java.util.Collection;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.programyourhome.common.response.ServiceResult;
import com.programyourhome.server.controllers.AbstractProgramYourHomeServerController;
import com.programyourhome.shop.Shopping;
import com.programyourhome.shop.model.PyhBarcodeSearchResult;
import com.programyourhome.shop.model.PyhBulkProduct;
import com.programyourhome.shop.model.PyhBulkProductProperties;
import com.programyourhome.shop.model.PyhCompanyProductToCompany;
import com.programyourhome.shop.model.PyhProduct;
import com.programyourhome.shop.model.PyhProductAggregationPartToProductAggregation;
import com.programyourhome.shop.model.PyhProductImage;
import com.programyourhome.shop.model.PyhProductProperties;
import com.programyourhome.shop.model.PyhProductState;

@RestController
@RequestMapping("shop/products")
public class ProgramYourHomeControllerShopProducts extends AbstractProgramYourHomeServerController {

    @Inject
    private Shopping shopping;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ServiceResult<Collection<? extends PyhProduct>> getProducts() {
        return this.produce("Products", () -> this.shopping.getProducts());
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ServiceResult<PyhProduct> getProduct(@PathVariable("id") final int productId) {
        return this.produce("Product", () -> this.shopping.getProduct(productId));
    }

    @RequestMapping(value = "barcode/{barcode}", method = RequestMethod.GET)
    public ServiceResult<PyhBarcodeSearchResult> searchProductByBarcode(@PathVariable("barcode") final String barcode) {
        return this.produce("BarcodeSearchResult", () -> this.shopping.searchProductByBarcode(barcode));
    }

    @RequestMapping(value = "", method = RequestMethod.POST, consumes = MIME_JSON)
    public ServiceResult<PyhProduct> createProduct(@RequestBody final PyhProductProperties productProperties) {
        return this.produce("Product", () -> this.shopping.createProduct(productProperties));
    }

    @RequestMapping(value = "{id}", method = RequestMethod.PUT, consumes = MIME_JSON)
    public ServiceResult<PyhProduct> updateProduct(@PathVariable("id") final int productId, @RequestBody final PyhProductProperties productProperties) {
        return this.produce("Product", () -> this.shopping.updateProduct(productId, productProperties));
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public ServiceResult<Void> deleteProduct(@PathVariable("id") final int productId) {
        return this.run(() -> this.shopping.deleteProduct(productId));
    }

    @RequestMapping(value = "{id}/bulkProducts", method = RequestMethod.GET)
    public ServiceResult<Collection<? extends PyhBulkProduct>> getBulkProducts(@PathVariable("id") final int productId) {
        return this.produce("BulkProducts", () -> this.shopping.getBulkProducts(productId));
    }

    @RequestMapping(value = "{id}/bulkProducts/{bulkProductId}", method = RequestMethod.GET)
    public ServiceResult<PyhBulkProduct> getBulkProduct(@PathVariable("id") final int productId, @PathVariable("bulkProductId") final int bulkProductId) {
        return this.produce("BulkProduct", () -> this.shopping.getBulkProduct(productId, bulkProductId));
    }

    @RequestMapping(value = "{id}/bulkProducts", method = RequestMethod.POST, consumes = MIME_JSON)
    public ServiceResult<PyhBulkProduct> createBulkProduct(@PathVariable("id") final int productId,
            @RequestBody final PyhBulkProductProperties bulkProductProperties) {
        return this.produce("BulkProduct", () -> this.shopping.addBulkProduct(productId, bulkProductProperties));
    }

    @RequestMapping(value = "{id}/bulkProducts/{bulkProductId}", method = RequestMethod.PUT, consumes = MIME_JSON)
    public ServiceResult<PyhBulkProduct> updateBulkProduct(@PathVariable("id") final int productId, @PathVariable("bulkProductId") final int bulkProductId,
            @RequestBody final PyhBulkProductProperties bulkProductProperties) {
        return this.produce("BulkProduct", () -> this.shopping.updateBulkProduct(productId, bulkProductId, bulkProductProperties));
    }

    @RequestMapping(value = "{id}/bulkProducts/{bulkProductId}", method = RequestMethod.DELETE)
    public ServiceResult<Void> deleteBulkProduct(@PathVariable("id") final int productId, @PathVariable("bulkProductId") final int bulkProductId) {
        return this.run(() -> this.shopping.deleteBulkProduct(productId, bulkProductId));
    }

    @RequestMapping(value = "{id}/productAggregationParts", method = RequestMethod.GET)
    public ServiceResult<Collection<? extends PyhProductAggregationPartToProductAggregation>> getProductAggregationParts(
            @PathVariable("id") final int productId) {
        return this.produce("ProductAggregationParts", () -> this.shopping.getProductAggregationPartsToProductAggregation(productId));
    }

    @RequestMapping(value = "{id}/companyProducts", method = RequestMethod.GET)
    public ServiceResult<Collection<? extends PyhCompanyProductToCompany>> getCompanyProducts(@PathVariable("id") final int productId) {
        return this.produce("CompanyProducts", () -> this.shopping.getCompanyProductsToCompany(productId));
    }

    @RequestMapping(value = "{id}/image", method = RequestMethod.GET)
    public ServiceResult<PyhProductImage> getProductImage(@PathVariable("id") final int productId) {
        return this.produce("ProductImage", () -> this.shopping.getProductImage(productId));
    }

    @RequestMapping(value = "{id}/image", method = RequestMethod.POST, consumes = MIME_JSON)
    public ServiceResult<PyhProductImage> setImageForProduct(@PathVariable("id") final int productId, @RequestBody final PyhProductImage image) {
        return this.produce("ProductImage", () -> this.shopping.setImageForProduct(productId, image));
    }

    @RequestMapping(value = "{id}/image", method = RequestMethod.DELETE)
    public ServiceResult<Void> deleteProductImage(@PathVariable("id") final int productId) {
        return this.run(() -> this.shopping.removeImageFromProduct(productId));
    }

    @RequestMapping(value = "{id}/state", method = RequestMethod.GET)
    public ServiceResult<PyhProductState> getProductState(@PathVariable("id") final int productId) {
        return this.produce("ProductState", () -> this.shopping.getProductState(productId));
    }

    @RequestMapping(value = "addBarcodeToStock/{barcode}", method = RequestMethod.POST)
    public ServiceResult<PyhProductState> addBarcodeToStock(@PathVariable("barcode") final String barcode) {
        return this.produce("ProductState", () -> this.shopping.increaseByBarcode(barcode));
    }

    @RequestMapping(value = "removeBarcodeFromStock/{barcode}", method = RequestMethod.POST)
    public ServiceResult<PyhProductState> removeBarcodeFromStock(@PathVariable("barcode") final String barcode) {
        return this.produce("ProductState", () -> this.shopping.decreaseByBarcode(barcode));
    }

    @RequestMapping(value = "{id}/addToStock", method = RequestMethod.POST)
    public ServiceResult<PyhProductState> addProductToStock(@PathVariable("id") final int productId) {
        return this.produce("ProductState", () -> this.shopping.increaseByProductId(productId, 1));
    }

    @RequestMapping(value = "{id}/removeFromStock", method = RequestMethod.POST)
    public ServiceResult<PyhProductState> removeProductFromStock(@PathVariable("id") final int productId) {
        return this.produce("ProductState", () -> this.shopping.decreaseByProductId(productId, 1));
    }

}
