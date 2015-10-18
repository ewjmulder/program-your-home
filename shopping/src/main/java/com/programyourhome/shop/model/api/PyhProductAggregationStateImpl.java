package com.programyourhome.shop.model.api;

import java.math.BigDecimal;

import com.programyourhome.common.model.PyhImpl;
import com.programyourhome.shop.model.PyhProductAggregationState;

public class PyhProductAggregationStateImpl extends PyhImpl implements PyhProductAggregationState {

    private final int id;
    private final BigDecimal amount;

    public PyhProductAggregationStateImpl(final int id, final int amount) {
        this(id, BigDecimal.valueOf(amount));
    }

    public PyhProductAggregationStateImpl(final int id, final BigDecimal amount) {
        this.id = id;
        this.amount = amount;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public BigDecimal getAmount() {
        return this.amount;
    }

}
