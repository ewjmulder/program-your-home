package com.programyourhome.shop.model;

public enum StockEventType {

    INCREASE("increase"),
    DECREASE("decrease");

    private String typeName;

    private StockEventType(final String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return this.typeName;
    }

}
