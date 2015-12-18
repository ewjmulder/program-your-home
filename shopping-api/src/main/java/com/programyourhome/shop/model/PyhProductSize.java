package com.programyourhome.shop.model;

import com.programyourhome.shop.model.size.SizeType;
import com.programyourhome.shop.model.size.UnitType;

public interface PyhProductSize extends PyhProductSizeProperties {

    public SizeType getSizeType();

    @Override
    public UnitType getUnit();

}
