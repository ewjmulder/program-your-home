package com.programyourhome.shop.model;

public interface PyhProductAggregationState {

    public int getId();

    /**
     * Get the combined size of all products in this aggregation.
     *
     * @return the 'available' size
     */
    public PyhProductSize getSize();

}
