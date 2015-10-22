package com.programyourhome.shop;

import java.util.Collection;

import com.programyourhome.shop.model.PyhProduct;
import com.programyourhome.shop.model.PyhProductAggregation;
import com.programyourhome.shop.model.PyhProductAggregationPartProperties;
import com.programyourhome.shop.model.PyhProductAggregationProperties;
import com.programyourhome.shop.model.PyhProductAggregationState;
import com.programyourhome.shop.model.PyhProductImage;
import com.programyourhome.shop.model.PyhProductProperties;
import com.programyourhome.shop.model.PyhProductState;

public interface Shopping {

    // TODO: Use API interface values instead of sep params?

    public Collection<? extends PyhProduct> getProducts();

    public PyhProduct getProduct(int productId);

    public PyhProduct createProduct(PyhProductProperties productProperties);

    public PyhProduct updateProduct(int productId, PyhProductProperties product);

    public void deleteProduct(int productId);

    public PyhProductImage getProductImage(int productId);

    public PyhProductImage setImageForProduct(int productId, PyhProductImage image);

    public void removeImageFromProduct(int productId);

    public Collection<? extends PyhProductAggregation> getProductAggregations();

    public PyhProductAggregation getProductAggregation(int productAggregationId);

    public PyhProductAggregation createProductAggregation(PyhProductAggregationProperties productAggregation);

    public PyhProductAggregation updateProductAggregation(int productAggregationId, PyhProductAggregationProperties productAggregation);

    public void deleteProductAggregation(int productAggregationId);

    public PyhProductAggregation setProductInProductAggregation(int productId, int productAggregationId, PyhProductAggregationPartProperties aggregationPart);

    public PyhProductAggregation removeProductFromProductAggregation(int productId, int productAggregationId);

    public Collection<PyhProductState> getProductStates();

    public PyhProductState getProductState(int productId);

    public Collection<PyhProductAggregationState> getProductAggregationStates();

    public PyhProductAggregationState getProductAggregationState(int productAggregationId);

    public PyhProductState addProductItem(int productId);

    public PyhProductState addProductItem(String barcode);

    public PyhProductState removeProductItem(int productId);

    public PyhProductState removeProductItem(String barcode);

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
