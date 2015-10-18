package com.programyourhome.server.events.products;

import com.programyourhome.server.events.ValueChangedEvent;
import com.programyourhome.shop.model.PyhProductAggregationState;

public class ProductAggregationStateChangedEvent extends ValueChangedEvent<PyhProductAggregationState> {

    private static final long serialVersionUID = 1L;

    public ProductAggregationStateChangedEvent(final PyhProductAggregationState oldValue, final PyhProductAggregationState newValue) {
        super(oldValue, newValue);
    }

    @Override
    public String getTopic() {
        return "/topic/shop/productaggregations/" + this.getNewValue().getId();
    }

}
