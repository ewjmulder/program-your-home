package com.programyourhome.shop.model.jpa;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import com.programyourhome.shop.common.NamedEntity;
import com.programyourhome.shop.model.PyhProductAggregation;

@Entity
public class ProductAggregation extends NamedEntity implements PyhProductAggregation {

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "aggregation_id")
    private final Set<ProductAggregationPart> aggregationParts;

    @Column(nullable = false)
    private int minimumAmount;

    @Column(nullable = false)
    private int maximumAmount;

    public ProductAggregation() {
        this(null, null, 0, 0);
    }

    public ProductAggregation(final String name, final String description, final int minimumAmount, final int maximumAmount) {
        super(name, description);
        this.minimumAmount = minimumAmount;
        this.maximumAmount = maximumAmount;
        this.aggregationParts = new HashSet<>();
    }

    @Override
    public int getMinimumAmount() {
        return this.minimumAmount;
    }

    public void setMinimumAmount(final int minimumAmount) {
        this.minimumAmount = minimumAmount;
    }

    @Override
    public int getMaximumAmount() {
        return this.maximumAmount;
    }

    public void setMaximumAmount(final int maximumAmount) {
        this.maximumAmount = maximumAmount;
    }

    @Override
    public Set<ProductAggregationPart> getAggregationParts() {
        return this.aggregationParts;
    }

    public void addAggregationPart(final ProductAggregationPart aggregationPart) {
        this.aggregationParts.add(aggregationPart);
    }

    public void removeAggregationPart(final ProductAggregationPart aggregationPart) {
        this.aggregationParts.remove(aggregationPart);
    }

}
