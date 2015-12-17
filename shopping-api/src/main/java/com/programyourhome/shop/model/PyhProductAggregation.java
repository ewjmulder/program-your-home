package com.programyourhome.shop.model;

import com.programyourhome.shop.model.size.UnitType;

public interface PyhProductAggregation extends PyhProductAggregationProperties {

    public int getId();

    @Override
    public UnitType getUnit();

}
