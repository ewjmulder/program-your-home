package com.programyourhome.shop.model.jpa;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Product {

    @Id
    private int id;
    private String barcode;
    private String name;
    private String description;

    // private ProductImage image;
    // private

    /** For JPA */
    @SuppressWarnings("unused")
    private Product() {
    }

    public Product(final int id, final String barcode, final String name, final String description) {
        this.id = id;
        this.barcode = barcode;
        this.name = name;
        this.description = description;
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

}
