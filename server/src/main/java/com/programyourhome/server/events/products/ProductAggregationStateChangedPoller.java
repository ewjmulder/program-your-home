package com.programyourhome.server.events.products;

import java.util.Collection;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.programyourhome.server.events.MapValueChangedPoller;
import com.programyourhome.shop.Shopping;
import com.programyourhome.shop.model.PyhProductAggregationState;

@Component
public class ProductAggregationStateChangedPoller extends MapValueChangedPoller<Integer, PyhProductAggregationState> {

    @Inject
    private Shopping shopping;

    public ProductAggregationStateChangedPoller() {
        super(PyhProductAggregationState.class, ProductAggregationStateChangedEvent.class);
    }

    @Override
    protected Collection<PyhProductAggregationState> getCurrentCollection() {
        return this.shopping.getProductAggregationStates();
    }

    @Override
    protected Integer getKey(final PyhProductAggregationState item) {
        return item.getId();
    }

    @Override
    public long getIntervalInMillis() {
        // FIXME: incorporate new insight that products have the amount and aggregations have a 'deduced' amount
        // TODO: make configurable. Should not be too often, since it will trigger a lot of EventStore processing.
        // More like 5 or 10 seconds.
        return this.millis(1000);
    }

}
