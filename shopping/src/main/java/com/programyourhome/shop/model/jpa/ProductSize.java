package com.programyourhome.shop.model.jpa;

import java.math.BigDecimal;

import com.programyourhome.shop.model.PyhProductSize;
import com.programyourhome.shop.model.size.SizeType;
import com.programyourhome.shop.model.size.SizeUnit;
import com.programyourhome.shop.model.size.UnitType;

public class ProductSize implements PyhProductSize {

    private final BigDecimal amount;
    private final SizeUnit sizeUnit;

    public ProductSize(final BigDecimal amount, final SizeUnit sizeUnit) {
        this.amount = amount;
        this.sizeUnit = sizeUnit;
    }

    @Override
    public BigDecimal getAmount() {
        return this.amount;
    }

    @Override
    public SizeType getSizeType() {
        return this.sizeUnit.getSizeType();
    }

    @Override
    public UnitType getUnit() {
        return this.sizeUnit.getUnit();
    }

}
