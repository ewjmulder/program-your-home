package com.programyourhome.shop.model.api;

import java.math.BigDecimal;

import com.programyourhome.common.model.PyhImpl;
import com.programyourhome.shop.model.PyhProductAggregationState;
import com.programyourhome.shop.model.PyhProductSize;
import com.programyourhome.shop.model.jpa.ProductAggregation;
import com.programyourhome.shop.model.jpa.ProductSize;

public class PyhProductAggregationStateImpl extends PyhImpl implements PyhProductAggregationState {

    private final ProductAggregation productAggregation;
    private final BigDecimal amount;

    public PyhProductAggregationStateImpl(final ProductAggregation productAggregation, final BigDecimal amount) {
        this.productAggregation = productAggregation;
        this.amount = amount;
    }

    @Override
    public int getId() {
        return this.productAggregation.getId();
    }

    @Override
    public PyhProductSize getSize() {
        return new ProductSize(this.amount, this.productAggregation.getSizeUnit());
    }

}
