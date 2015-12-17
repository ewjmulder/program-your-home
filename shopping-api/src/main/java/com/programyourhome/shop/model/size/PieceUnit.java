package com.programyourhome.shop.model.size;

import java.math.BigDecimal;

public enum PieceUnit implements UnitType {

    PIECE("pc", BigDecimal.ONE);

    private String abbreviation;
    private BigDecimal amountInSmallestUnit;

    private PieceUnit(final String abbreviation, final BigDecimal amountInSmallestUnit) {
        this.abbreviation = abbreviation;
        this.amountInSmallestUnit = amountInSmallestUnit;
    }

    @Override
    public String getTypeName() {
        return "Piece";
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
