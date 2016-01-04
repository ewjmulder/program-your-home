package com.programyourhome.shop.model.pojo;

import com.programyourhome.shop.model.BarcodeSearchResultType;
import com.programyourhome.shop.model.PyhBarcodeSearchResult;
import com.programyourhome.shop.model.PyhBulkProduct;
import com.programyourhome.shop.model.PyhProduct;
import com.programyourhome.shop.model.jpa.BulkProduct;
import com.programyourhome.shop.model.jpa.Product;

public class PyhBarcodeSearchResultImpl implements PyhBarcodeSearchResult {

    private final BarcodeSearchResultType resultType;
    private final PyhProduct product;
    private final PyhBulkProduct bulkProduct;

    private PyhBarcodeSearchResultImpl(final BarcodeSearchResultType resultType, final PyhProduct product, final PyhBulkProduct bulkProduct) {
        this.resultType = resultType;
        this.product = product;
        this.bulkProduct = bulkProduct;
    }

    @Override
    public BarcodeSearchResultType getResultType() {
        return this.resultType;
    }

    @Override
    public PyhProduct getProduct() {
        return this.product;
    }

    @Override
    public PyhBulkProduct getBulkProduct() {
        return this.bulkProduct;
    }

    public static PyhBarcodeSearchResultImpl none() {
        return new PyhBarcodeSearchResultImpl(BarcodeSearchResultType.NONE, null, null);
    }

    public static PyhBarcodeSearchResultImpl product(final Product product) {
        return new PyhBarcodeSearchResultImpl(BarcodeSearchResultType.PRODUCT, product, null);
    }

    public static PyhBarcodeSearchResultImpl bulkProduct(final BulkProduct bulkProduct) {
        return new PyhBarcodeSearchResultImpl(BarcodeSearchResultType.BULK_PRODUCT, bulkProduct.getProduct(), bulkProduct);
    }

}
