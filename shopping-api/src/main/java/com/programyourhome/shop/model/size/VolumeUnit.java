package com.programyourhome.shop.model.size;

import java.math.BigDecimal;

public enum VolumeUnit implements UnitType {

    MILLILITER("ml", BigDecimal.ONE),
    LITER("l", BigDecimal.valueOf(1000));

    private String abbreviation;
    private BigDecimal amountInSmallestUnit;

    private VolumeUnit(final String abbreviation, final BigDecimal amountInSmallestUnit) {
        this.abbreviation = abbreviation;
        this.amountInSmallestUnit = amountInSmallestUnit;
    }

    @Override
    public String getTypeName() {
        return "Volume";
    }

    @Override
    public String getAbbreviation() {
        return this.abbreviation;
    }

    @Override
    public BigDecimal getAmountInSmallestUnit() {
        return this.amountInSmallestUnit;
    }
}
