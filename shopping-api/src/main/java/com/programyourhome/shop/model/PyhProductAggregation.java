package com.programyourhome.shop.model;

import java.math.BigDecimal;
import java.util.Collection;

public interface PyhProductAggregation {

    public int getId();

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

    public Collection<? extends PyhProductAggregationPart> getAggregationParts();

}
