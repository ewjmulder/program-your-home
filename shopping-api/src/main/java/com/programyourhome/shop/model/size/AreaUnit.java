package com.programyourhome.shop.model.size;

import java.math.BigDecimal;

public enum AreaUnit implements UnitType {

    SQUARE_MILLIMETER("mm²", BigDecimal.ONE),
    SQUARE_CENTIMETER("cm²", BigDecimal.valueOf(100)),
    SQUARE_DECIMETER("dm²", BigDecimal.valueOf(10_000)),
    SQUARE_METER("m²", BigDecimal.valueOf(1_000_000));

    private String abbreviation;
    private BigDecimal amountInSmallestUnit;

    private AreaUnit(final String abbreviation, final BigDecimal amountInSmallestUnit) {
        this.abbreviation = abbreviation;
        this.amountInSmallestUnit = amountInSmallestUnit;
    }

    @Override
    public String getTypeName() {
        return "Area";
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
