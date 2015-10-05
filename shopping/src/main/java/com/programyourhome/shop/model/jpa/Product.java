package com.programyourhome.shop.model.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(unique = true, nullable = false)
    private String barcode;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;

    // private ProductImage image;
    // private

    /** For JPA */
    @SuppressWarnings("unused")
    private Product() {
    }

    public Product(final String barcode, final String name, final String description) {
        // Value for id should be auto-generated.
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
