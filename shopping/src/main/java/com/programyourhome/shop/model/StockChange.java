package com.programyourhome.shop.model;

public class StockChange {

    private final int productId;
    private final int amount;

    public StockChange(final int productId, final int amount) {
        this.productId = productId;
        this.amount = amount;
    }

    public int getProductId() {
        return this.productId;
    }

    public int getAmount() {
        return this.amount;
    }

}
