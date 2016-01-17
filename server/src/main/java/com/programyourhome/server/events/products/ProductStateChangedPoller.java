package com.programyourhome.server.events.products;

import java.util.Collection;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.programyourhome.server.events.MapValueChangedPoller;
import com.programyourhome.shop.Shopping;
import com.programyourhome.shop.model.PyhProductState;

@Component
public class ProductStateChangedPoller extends MapValueChangedPoller<Integer, PyhProductState> {

    @Inject
    private Shopping shopping;

    public ProductStateChangedPoller() {
        super(PyhProductState.class, ProductStateChangedEvent.class);
    }

    @Override
    protected Collection<PyhProductState> getCurrentCollection() {
        return this.shopping.getProductStates();
    }

    @Override
    protected Integer getKey(final PyhProductState item) {
        return item.getId();
    }

    @Override
    public long getIntervalInMillis() {
        // TODO: make configurable
        return this.millis(2000);
    }

}
