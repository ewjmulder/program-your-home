package com.programyourhome.shop;

import java.util.Collection;

import com.programyourhome.shop.model.PyhProduct;
import com.programyourhome.shop.model.PyhProductState;

public interface Shopping {

    public Collection<PyhProduct> getProducts();

    public PyhProduct getProduct(int productId);

    public PyhProductState getProductState(int productId);

    /*
     * TODO: add other data as getters to service
     * Open questions: duplicate all data in Pyh...Impl classes or let JPA classes implement interfaces?
     * Pro: easy, no duplication
     * Con: lazy loading problems
     * public Collection<PyhProductAggregation> getProductAggregations();
     * public PyhProductAggregation getProductAggregation(int productAggregationId);
     * --> Idea/tryout: split state in sep interface and API call. Eventually for all resources.
     * Adapt BasePage.js for this new behavior (and whole UI of course)
     */

}
