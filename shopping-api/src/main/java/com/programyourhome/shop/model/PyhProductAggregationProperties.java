package com.programyourhome.shop.model;

import java.math.BigDecimal;

public interface PyhProductAggregationProperties extends SizeUnitIdentification {

    public String getName();

    public String getDescription();

    /**
     * Get the minimum amount of items that should be 'available' of this product aggregation.
     *
     * @return the minimum amount
     */
    public BigDecimal getMinimumAmount();

    /**
     * Get the maximum amount of items that should be 'available' of this product aggregation.
     *
     * @return the maximum amount
     */
    public BigDecimal getMaximumAmount();

}
