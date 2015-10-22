package com.programyourhome.shop.model.jpa;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.programyourhome.shop.common.NamedEntity;
import com.programyourhome.shop.model.PyhProduct;

@Entity
@JsonSerialize(as = PyhProduct.class)
public class Product extends NamedEntity implements PyhProduct {

    @Column(unique = true, nullable = false)
    private String barcode;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "product")
    private ProductImage image;

    @ManyToMany
    @JoinColumn(name = "product_id")
    private final Set<CompanyProduct> companyProducts;

    // Modeled as a collection to allow for one product to be a part of several aggregations.
    @ManyToMany(mappedBy = "product")
    private final Set<ProductAggregationPart> aggregationParts;

    public Product() {
        this(null, null, null);
    }

    public Product(final String name, final String description, final String barcode) {
        super(name, description);
        this.companyProducts = new HashSet<>();
        this.aggregationParts = new HashSet<>();
        this.barcode = barcode;
        this.image = null;
    }

    @Override
    public String getBarcode() {
        return this.barcode;
    }

    public void setBarcode(final String barcode) {
        this.barcode = barcode;
    }

    public boolean hasImage() {
        return this.image != null;
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

    public Set<ProductAggregationPart> getAggregationParts() {
        return this.aggregationParts;
    }

}
