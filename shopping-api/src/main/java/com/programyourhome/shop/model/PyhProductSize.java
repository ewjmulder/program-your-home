package com.programyourhome.shop.model;

import com.programyourhome.shop.model.size.UnitType;

public interface PyhProductSize extends PyhProductSizeProperties {

    @Override
    public UnitType getUnit();

}
