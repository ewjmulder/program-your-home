package com.programyourhome.shop.model;

public interface PyhProductAggregationPart extends PyhProductAggregationPartProperties {

    /**
     * Get the product that is part of the aggregation. The default is that
     * this object will be 'navigated to' from the aggregation side, not from
     * the product side.
     *
     * @return the product
     */
    public PyhProduct getProduct();

}
