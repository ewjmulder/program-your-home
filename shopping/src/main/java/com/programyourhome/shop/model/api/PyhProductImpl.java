package com.programyourhome.shop.model.api;

import com.programyourhome.common.model.PyhImpl;
import com.programyourhome.shop.model.PyhProductImage;
import com.programyourhome.shop.model.jpa.Product;

public class PyhProductImpl extends PyhImpl { // TODO: remove this class? implements PyhProduct {

    private final int id;
    private final String barcode;
    private final String name;
    private final String description;
    private PyhProductImage image;

    // TODO: constructor based on JPA entities / event store result for amount
    public PyhProductImpl(final Product product) {
        this.id = product.getId();
        this.barcode = product.getBarcode();
        this.name = product.getName();
        this.description = product.getDescription();
        // TODO: other field from other sources
        // this.
    }

    public int getId() {
        return this.id;
    }

    public String getBarcode() {
        return this.barcode;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public PyhProductImage getImage() {
        return this.image;
    }

}
