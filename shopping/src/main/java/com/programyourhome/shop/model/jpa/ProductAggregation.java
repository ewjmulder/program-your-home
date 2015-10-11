package com.programyourhome.shop.model.jpa;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import com.programyourhome.shop.common.NamedEntity;

@Entity
public class ProductAggregation extends NamedEntity {

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "aggregation_id")
    private final Set<ProductAggregationPart> aggregationParts;

    public ProductAggregation() {
        this(null, null);
    }

    public ProductAggregation(final String name, final String description) {
        super(name, description);
        this.aggregationParts = new HashSet<>();
    }

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
