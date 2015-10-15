package com.programyourhome.shop.model;

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
    public int getMinimumAmount();

    /**
     * Get the maximum amount of items that should be 'available' of this product aggregation.
     *
     * @return the maximum amount
     */
    public int getMaximumAmount();

    public Collection<? extends PyhProductAggregationPart> getAggregationParts();

}
