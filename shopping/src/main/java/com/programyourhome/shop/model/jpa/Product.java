package com.programyourhome.shop.model.jpa;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import com.programyourhome.shop.common.NamedEntity;

@Entity
public class Product extends NamedEntity {

    @Column(unique = true, nullable = false)
    private String barcode;

    @Embedded
    private ProductImage image;

    @OneToMany
    @JoinColumn(name = "product_id")
    private final Set<CompanyProduct> companyProducts;

    public Product() {
        this(null, null, null, null);
    }

    public Product(final String barcode, final String name, final String description, final ProductImage image) {
        super(name, description);
        this.companyProducts = new HashSet<>();
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

    public Set<CompanyProduct> getCompanyProducts() {
        return this.companyProducts;
    }

}
