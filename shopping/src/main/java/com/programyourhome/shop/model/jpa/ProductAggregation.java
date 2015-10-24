package com.programyourhome.shop.model.jpa;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.programyourhome.shop.common.NamedEntity;
import com.programyourhome.shop.model.PyhProductAggregation;

@Entity
public class ProductAggregation extends NamedEntity implements PyhProductAggregation {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "aggregation")
    private final Set<ProductAggregationPart> aggregationParts;

    @Column(nullable = false)
    private BigDecimal minimumAmount;

    @Column(nullable = false)
    private BigDecimal maximumAmount;

    public ProductAggregation() {
        this(null, null, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    public ProductAggregation(final String name, final String description, final BigDecimal minimumAmount, final BigDecimal maximumAmount) {
        super(name, description);
        this.minimumAmount = minimumAmount;
        this.maximumAmount = maximumAmount;
        this.aggregationParts = new HashSet<>();
    }

    @Override
    public BigDecimal getMinimumAmount() {
        return this.minimumAmount;
    }

    public void setMinimumAmount(final BigDecimal minimumAmount) {
        this.minimumAmount = minimumAmount;
    }

    @Override
    public BigDecimal getMaximumAmount() {
        return this.maximumAmount;
    }

    public void setMaximumAmount(final BigDecimal maximumAmount) {
        this.maximumAmount = maximumAmount;
    }

    public Set<ProductAggregationPart> getAggregationParts() {
        return this.aggregationParts;
    }

    public Optional<ProductAggregationPart> findAggregationPart(final int productId) {
        return this.aggregationParts.stream()
                .filter(part -> part.getProduct().getId() == productId)
                .findFirst();
    }

    public void addAggregationPart(final ProductAggregationPart aggregationPart) {
        this.aggregationParts.add(aggregationPart);
    }

    public void removeAggregationPart(final int productId) {
        new HashSet<>(this.aggregationParts).stream()
        .filter(part -> part.getProduct().getId() == productId)
        .forEach(this.aggregationParts::remove);
    }

}
