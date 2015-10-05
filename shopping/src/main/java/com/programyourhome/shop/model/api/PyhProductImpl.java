package com.programyourhome.shop.model.api;

import com.programyourhome.common.model.PyhImpl;
import com.programyourhome.shop.model.PyhProduct;
import com.programyourhome.shop.model.PyhProductImage;
import com.programyourhome.shop.model.jpa.Product;

public class PyhProductImpl extends PyhImpl implements PyhProduct {

    private final int id;
    private final String barcode;
    private final String name;
    private final String description;
    private PyhProductImage image;
    private int amount;
    private int minimumAmount;
    private int maximumAmount;

    // TODO: constructor based on JPA entities / event store result for amount
    public PyhProductImpl(final Product product) {
        this.id = product.getId();
        this.barcode = product.getBarcode();
        this.name = product.getName();
        this.description = product.getDescription();
        // TODO: other field from other sources
        // this.
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getBarcode() {
        return this.barcode;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public PyhProductImage getImage() {
        return this.image;
    }

    @Override
    public int getAmount() {
        return this.amount;
    }

    @Override
    public int getMinimumAmount() {
        return this.minimumAmount;
    }

    @Override
    public int getMaximumAmount() {
        return this.maximumAmount;
    }

}
