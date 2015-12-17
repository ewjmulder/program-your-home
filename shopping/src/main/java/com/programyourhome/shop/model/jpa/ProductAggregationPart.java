package com.programyourhome.shop.model.jpa;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.programyourhome.shop.common.Entity;
import com.programyourhome.shop.model.PyhProductAggregationPart;
import com.programyourhome.shop.model.PyhProductAggregationPartToProductAggregation;

@javax.persistence.Entity
public class ProductAggregationPart extends Entity implements PyhProductAggregationPart, PyhProductAggregationPartToProductAggregation {

    @ManyToOne
    @JoinColumn(nullable = false)
    private ProductAggregation aggregation;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Product product;

    // How much this product is preferred in the aggregation. (higher values = more preferred)
    @Column(nullable = true)
    private Integer preference;

    /** Only for JPA, we don't want an instance of this type to be constructed without links to productaggregation and product. */
    @SuppressWarnings("unused")
    private ProductAggregationPart() {
    }

    public ProductAggregationPart(final ProductAggregation aggregation, final Product product) {
        this(aggregation, product, null);
    }

    public ProductAggregationPart(final ProductAggregation aggregation, final Product product, final Integer preference) {
        this.aggregation = aggregation;
        this.product = product;
        this.preference = preference;
    }

    @Override
    public ProductAggregation getProductAggregation() {
        return this.aggregation;
    }

    @Override
    public Product getProduct() {
        return this.product;
    }

    @Override
    public Integer getPreference() {
        return this.preference;
    }

    public void setPreference(final Integer preference) {
        this.preference = preference;
    }

}
