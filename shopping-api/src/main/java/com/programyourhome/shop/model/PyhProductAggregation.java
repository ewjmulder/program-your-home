package com.programyourhome.shop.model;

import java.util.Collection;

public interface PyhProductAggregation {

    public int getId();

    public String getName();

    public String getDescription();

    public Collection<PyhProductAggregationPart> getAggregationParts();

}
