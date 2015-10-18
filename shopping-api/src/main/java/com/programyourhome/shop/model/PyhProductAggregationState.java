package com.programyourhome.shop.model;

import java.math.BigDecimal;

public interface PyhProductAggregationState {

    public int getId();

    /**
     * Get the number of items of this type of product that are 'available'. This means either
     * in stock or in use. This amount should be reduced if an item is finished / thrown away / no longer usable.
     * This amount should be increased when new (a) item(s) are bought and put in stock or in use.
     * The amount has type BigDecimal, because it could be that you want to model some product as halve or double
     * the value of a product aggregation.
     *
     * @return the amount 'available'
     */
    public BigDecimal getAmount();

}
