package com.programyourhome.shop.model.jpa;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.programyourhome.shop.common.Entity;

@javax.persistence.Entity
public class ProductAggregationPart extends Entity {

    @ManyToOne
    @JoinColumn(nullable = false)
    private ProductAggregation aggregation;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Product product;

    // How much this product 'adds' to the aggregation.
    // For example: whole bread = 1, half bread = 0.5
    @Column(nullable = true)
    private BigDecimal quantity;

    // How much this product is preferred in the aggregation. (higher values = more preferred)
    @Column(nullable = true)
    private Integer preference;

    /** Only for JPA, we don't want an instance of this type to be constructor without links to productaggregation and product. */
    @SuppressWarnings("unused")
    private ProductAggregationPart() {
    }

    public ProductAggregationPart(final ProductAggregation aggregation, final Product product) {
        this(aggregation, product, null, null);
    }

    public ProductAggregationPart(final ProductAggregation aggregation, final Product product, final BigDecimal quantity, final Integer preference) {
        this.aggregation = aggregation;
        this.product = product;
        this.quantity = quantity;
        this.preference = preference;
    }

    public ProductAggregation getAggregation() {
        return this.aggregation;
    }

    public Product getProduct() {
        return this.product;
    }

    public BigDecimal getQuantity() {
        return this.quantity;
    }

    public void setQuantity(final BigDecimal quantity) {
        this.quantity = quantity;
    }

    public Integer getPreference() {
        return this.preference;
    }

    public void setPreference(final Integer preference) {
        this.preference = preference;
    }

}
