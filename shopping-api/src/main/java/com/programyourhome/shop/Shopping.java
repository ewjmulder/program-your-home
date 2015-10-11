package com.programyourhome.shop;

import java.util.Collection;

import com.programyourhome.shop.model.PyhProduct;

public interface Shopping {

    public Collection<PyhProduct> getProducts();

    public PyhProduct getProduct(int productId);

    /*
     * TODO: add other data as getters to service
     * Open questions: duplicate all data in Pyh...Impl classes or let JPA classes implement interfaces?
     * Pro: easy, no duplication
     * Con: lazy loading problems
     * public Collection<PyhProductAggregation> getProductAggregations();
     * public PyhProductAggregation getProductAggregation(int productAggregationId);
     */

}
