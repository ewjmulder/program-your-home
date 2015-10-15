package com.programyourhome.shop.model.jpa;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.programyourhome.shop.common.NamedEntity;
import com.programyourhome.shop.model.PyhProduct;

@Entity
@JsonSerialize(as = PyhProduct.class)
public class Product extends NamedEntity implements PyhProduct {

    @Column(unique = true, nullable = false)
    private String barcode;

    @ManyToOne
    @JoinColumn(nullable = true)
    private Department department;

    @Embedded
    private ProductImage image;

    @OneToMany
    @JoinColumn(name = "product_id")
    private final Set<CompanyProduct> companyProducts;

    // Modeled as a collection to allow for one product to be a part of several aggregations.
    @OneToMany
    @JoinColumn(name = "product_id")
    private final Set<ProductAggregationPart> aggregationParts;

    public Product() {
        this(null, null, null, null, null);
    }

    public Product(final String name, final String description, final String barcode, final Department department, final ProductImage image) {
        super(name, description);
        this.companyProducts = new HashSet<>();
        this.aggregationParts = new HashSet<>();
        this.barcode = barcode;
        this.department = department;
        this.image = image;
    }

    @Override
    public String getBarcode() {
        return this.barcode;
    }

    public void setBarcode(final String barcode) {
        this.barcode = barcode;
    }

    public Department getDepartment() {
        return this.department;
    }

    public void setDepartment(final Department department) {
        this.department = department;
    }

    @Override
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
