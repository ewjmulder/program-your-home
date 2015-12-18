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
import com.programyourhome.shop.model.size.SizeType;
import com.programyourhome.shop.model.size.SizeUnit;
import com.programyourhome.shop.model.size.UnitType;

@Entity
public class ProductAggregation extends NamedEntity implements PyhProductAggregation {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "aggregation")
    private final Set<ProductAggregationPart> aggregationParts;

    @Column(nullable = false)
    private SizeUnit sizeUnit;

    @Column(nullable = false)
    private BigDecimal minimumAmount;

    @Column(nullable = false)
    private BigDecimal maximumAmount;

    public ProductAggregation() {
        this(null, null, null, null, null);
    }

    public ProductAggregation(final String name, final String description, final SizeUnit sizeUnit, final BigDecimal minimumAmount,
            final BigDecimal maximumAmount) {
        super(name, description);
        this.sizeUnit = sizeUnit;
        this.minimumAmount = minimumAmount;
        this.maximumAmount = maximumAmount;
        this.aggregationParts = new HashSet<>();
        this.validate();
    }

    // TODO: Use Hibernate Validation (JSR thingy) for genericly solving these kinds of validations?
    private void validate() {
        if (this.minimumAmount != null && this.maximumAmount != null && this.minimumAmount.compareTo(this.maximumAmount) > 0) {
            throw new IllegalArgumentException("Minimum amount cannot be more than maximum amount.");
        }
    }

    public SizeUnit getSizeUnit() {
        return this.sizeUnit;
    }

    public void setSizeUnit(final SizeUnit sizeUnit) {
        this.sizeUnit = sizeUnit;
    }

    public SizeType getSizeType() {
        return this.sizeUnit.getSizeType();
    }

    @Override
    public UnitType getUnit() {
        return this.sizeUnit.getUnit();
    }

    @Override
    public BigDecimal getMinimumAmount() {
        return this.minimumAmount;
    }

    public void setMinimumAmount(final BigDecimal minimumAmount) {
        this.minimumAmount = minimumAmount;
        this.validate();
    }

    @Override
    public BigDecimal getMaximumAmount() {
        return this.maximumAmount;
    }

    public void setMaximumAmount(final BigDecimal maximumAmount) {
        this.maximumAmount = maximumAmount;
        this.validate();
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
        if (aggregationPart.getProduct().getSize().getSizeType() != this.sizeUnit.getSizeType()) {
            throw new IllegalArgumentException("All products of aggregation: '" + this.getName() + "' must be of size type: " + this.sizeUnit.getSizeType());
        }
        this.aggregationParts.add(aggregationPart);
    }

    public void removeAggregationPart(final int productId) {
        new HashSet<>(this.aggregationParts).stream()
        .filter(part -> part.getProduct().getId() == productId)
        .forEach(this.aggregationParts::remove);
    }

}
