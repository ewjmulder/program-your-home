package com.programyourhome.shop.model;

public interface PyhProductAggregationPart extends PyhProductAggregationPartProperties {

    /**
     * Get the product that is part of the aggregation. It's assumed that in the API
     * this object will always be 'navigated to' from the aggregation side, never from
     * the product side. That's why there is no getAggregation method.
     *
     * @return the product
     */
    public PyhProduct getProduct();

}
