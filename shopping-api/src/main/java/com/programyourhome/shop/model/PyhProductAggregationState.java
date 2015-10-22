package com.programyourhome.shop.model;

import java.math.BigDecimal;

public interface PyhProductAggregationState {

    public int getId();

    /**
     * Get the combined amount of all products in this aggregation.
     * That means the sum of: for every product the amount times the contribution of that product in the aggregation.
     *
     * @return the amount 'available'
     */
    public BigDecimal getAmount();

}
