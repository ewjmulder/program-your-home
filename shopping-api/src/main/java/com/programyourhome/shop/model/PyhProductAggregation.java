package com.programyourhome.shop.model;

import java.util.Collection;

public interface PyhProductAggregation extends PyhProductAggregationProperties {

    public int getId();

    public Collection<? extends PyhProductAggregationPart> getAggregationParts();

}
