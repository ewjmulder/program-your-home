package com.programyourhome.shop;

import java.math.BigDecimal;
import java.util.Collection;

import com.programyourhome.shop.model.PyhProduct;
import com.programyourhome.shop.model.PyhProductAggregation;
import com.programyourhome.shop.model.PyhProductAggregationState;

public interface Shopping {

    public Collection<PyhProduct> getProducts();

    public PyhProduct getProduct(int productId);

    public PyhProduct addProduct(String barcode, String name, String description);

    // TODO: addImageToProduct

    public PyhProduct updateProduct(int id, String barcode, String name, String description);

    public void deleteProduct(int id);

    public Collection<PyhProductAggregation> getProductAggregations();

    public PyhProductAggregation getProductAggregation(int productAggregationId);

    public PyhProductAggregation addProductAggregation(String name, String description, BigDecimal minimumAmount, BigDecimal maximumAmount);

    public PyhProductAggregation addProductToProductAggregation(int productId, int productAggregationId, BigDecimal quantity, Integer preference);

    public Collection<PyhProductAggregationState> getProductAggregationStates();

    public PyhProductAggregationState getProductAggregationState(int productAggregationId);

    public PyhProductAggregationState addToProductAggregationState(int productAggregationId, BigDecimal amount);

    public PyhProductAggregationState removeFromProductAggregationState(int productAggregationId, BigDecimal amount);

    /*
     * TODO: add other data as getters to service
     * Open questions: duplicate all data in Pyh...Impl classes or let JPA classes implement interfaces?
     * Pro: easy, no duplication
     * Con: lazy loading problems
     * --> Idea/tryout: split state in sep interface and API call. Eventually for all resources.
     * Adapt BasePage.js for this new behavior (and whole UI of course)
     * TODO:
     * - remove methods
     * - update methods
     * - all data creation and linking that is possible
     * - company
     * - shop
     * - department
     * - etc
     */

}
