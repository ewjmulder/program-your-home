package com.programyourhome.shop.model;

import java.math.BigDecimal;

public interface PyhProductAggregationPart {

    /**
     * Get the product that is part of the aggregation. It's assumed that in the API
     * this object will always be 'navigated to' from the aggregation side, never from
     * the product side. That's why there is no getAggregation method.
     *
     * @return the product
     */
    public PyhProduct getProduct();

    public BigDecimal getQuantity();

    public Integer getPreference();

}
