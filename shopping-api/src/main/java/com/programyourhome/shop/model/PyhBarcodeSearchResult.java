package com.programyourhome.shop.model;

public interface PyhBarcodeSearchResult {

    /**
     * Get the type of result that the barcode search returned.
     * Either none, a normal product or a bulk product.
     *
     * @return the type
     */
    public BarcodeSearchResultType getResultType();

    /**
     * Get the product that is the result of the search.
     * In case of type none, this will be null.
     * In case of type product, this will contain the product.
     * In case of type bulk product, this will be contain the product 'inside' the bulk.
     *
     * @return the product
     */
    public PyhProduct getProduct();

    /**
     * Get the bulk product that is the result of the search.
     * In case of type none, this will be null.
     * In case of type product, this will be null.
     * In case of type bulk product, this will contain the bulk product.
     *
     * @return the bulk product
     */
    public PyhBulkProduct getBulkProduct();

}
