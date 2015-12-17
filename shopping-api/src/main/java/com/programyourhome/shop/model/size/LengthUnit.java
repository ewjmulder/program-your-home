package com.programyourhome.shop.model.size;

import java.math.BigDecimal;

public enum LengthUnit implements UnitType {

    MILLIMETER("mm", BigDecimal.ONE),
    CENTIMETER("cm", BigDecimal.valueOf(10)),
    DECIMETER("dm", BigDecimal.valueOf(100)),
    METER("m", BigDecimal.valueOf(1_000));

    private String abbreviation;
    private BigDecimal amountInSmallestUnit;

    private LengthUnit(final String abbreviation, final BigDecimal amountInSmallestUnit) {
        this.abbreviation = abbreviation;
        this.amountInSmallestUnit = amountInSmallestUnit;
    }

    @Override
    public String getTypeName() {
        return "Length";
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
