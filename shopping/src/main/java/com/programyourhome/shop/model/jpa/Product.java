package com.programyourhome.shop.model.jpa;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;

import com.programyourhome.common.jpa.NamedEntity;

@Entity
public class Product extends NamedEntity {

    @Column(unique = true, nullable = false)
    private String barcode;

    @Embedded
    private ProductImage image;

    public Product() {
    }

    public Product(final String barcode, final String name, final String description, final ProductImage image) {
        super(name, description);
        this.barcode = barcode;
        this.image = image;
    }

    public String getBarcode() {
        return this.barcode;
    }

    public void setBarcode(final String barcode) {
        this.barcode = barcode;
    }

    public ProductImage getImage() {
        return this.image;
    }

    public void setImage(final ProductImage image) {
        this.image = image;
    }

}
