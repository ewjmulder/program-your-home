package com.programyourhome.shop.model.size;

import java.math.BigDecimal;

public enum WeightUnit implements UnitType {

    GRAM("g", BigDecimal.ONE),
    KILOGRAM("kg", BigDecimal.valueOf(1_000));

    private String abbreviation;
    private BigDecimal amountInSmallestUnit;

    private WeightUnit(final String abbreviation, final BigDecimal amountInSmallestUnit) {
        this.abbreviation = abbreviation;
        this.amountInSmallestUnit = amountInSmallestUnit;
    }

    @Override
    public String getTypeName() {
        return "Weight";
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
