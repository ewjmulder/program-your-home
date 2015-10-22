package com.programyourhome.shop.model;

import java.math.BigDecimal;

public interface PyhProductAggregationPartProperties {

    /**
     * The contribution has type BigDecimal, because it could be that you want to model some product
     * to countas halve or double the 'item' value of a product aggregation.
     *
     * @return contribution
     */
    public BigDecimal getContribution();

    public Integer getPreference();

}
