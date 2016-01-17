package com.programyourhome.server.events.products;

import com.programyourhome.server.events.ValueChangedEvent;
import com.programyourhome.shop.model.PyhProductState;

public class ProductStateChangedEvent extends ValueChangedEvent<PyhProductState> {

    private static final long serialVersionUID = 1L;

    public ProductStateChangedEvent(final PyhProductState oldValue, final PyhProductState newValue) {
        super(oldValue, newValue);
    }

    @Override
    public String getTopic() {
        return "/topic/shop/products/" + this.getNewValue().getId();
    }

}
